package dev.itseternity.chatfilter.storage;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.utils.Queries;
import dev.itseternity.chatfilter.utils.SQLCredentials;
import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//TODO Clean this class up 18/09/19
public class SQLFilterStorage implements FilterStorage {

    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
    private static final long LEAK_DETECTION = TimeUnit.SECONDS.toMillis(10);

    private final ChatFilterPlugin plugin;
    private final SQLCredentials credentials;
    private HikariDataSource dataSource;

    public SQLFilterStorage(ChatFilterPlugin plugin, SQLCredentials credentials) {
        Preconditions.checkNotNull(plugin, "plugin null");
        Preconditions.checkNotNull(credentials, "credentials null");

        this.plugin = plugin;
        this.credentials = credentials;
    }

    @Override
    public void init() {
        HikariConfig config = generateConfig(credentials);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            dataSource = new HikariDataSource(config);
            setupTables();

            plugin.getChatManager().getBlacklistedWords().addAll(getBlacklistedWords());
            plugin.getChatManager().getWhitelistedWords().addAll(getWhitelistedWords());
            plugin.getLogger().info(String.format("Loaded in %d blacklisted words", plugin.getChatManager().getBlacklistedWords().size()));
            plugin.getLogger().info(String.format("Loaded in %d whitelisted words", plugin.getChatManager().getWhitelistedWords().size()));
        });
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public Set<String> getWhitelistedWords() {
        Set<String> whitelist = new HashSet<>();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.GET_WHITELISTED_WORDS)) {
                ResultSet results = statement.executeQuery();

                while (results.next()) {
                    String word = results.getString(Queries.WORD).toLowerCase();
                    whitelist.add(word);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return whitelist;
    }

    @Override
    public Set<String> getBlacklistedWords() {
        Set<String> whitelist = new HashSet<>();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.GET_BLACKLISTED_WORDS)) {
                ResultSet results = statement.executeQuery();

                while (results.next()) {
                    String word = results.getString(Queries.WORD).toLowerCase();
                    whitelist.add(word);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return whitelist;
    }

    @Override
    public int addBlacklistedWord(String word, CommandSender player) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.INSERT_BLACKLIST)) {
                statement.setString(1, word);
                statement.setString(2, player.getName());

                statement.executeUpdate();
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int addWhitelistedWord(String word, CommandSender player) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.INSERT_WHITELIST)) {
                statement.setString(1, word);
                statement.setString(2, player.getName());
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int removeWhitelistedWord(String word) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.DELETE_WHITELIST)) {
                statement.setString(1, word);

                statement.executeUpdate();
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int removeBlacklistedWord(String word) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.DELETE_BLACKLIST)) {
                statement.setString(1, word);
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private Connection getConnection() throws SQLException {
        Preconditions.checkArgument(!dataSource.isClosed(), "datasource is closed");
        return dataSource.getConnection();
    }

    private void setupTables() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.CREATE_WHITELIST_TABLE)) {
                statement.executeUpdate();
                statement.executeUpdate(Queries.CREATE_BLACKLIST_TABLE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HikariConfig generateConfig(SQLCredentials credentials) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("chatfilter-pool-" + POOL_COUNTER.getAndIncrement());
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", credentials.getHost());
        config.addDataSourceProperty("port", credentials.getPort());
        config.addDataSourceProperty("databaseName", credentials.getDatabase());
        config.addDataSourceProperty("user", credentials.getUsername());
        config.addDataSourceProperty("password", credentials.getPassword());

        // See: https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
        config.setMaximumPoolSize(Math.max(Runtime.getRuntime().availableProcessors(), 4));
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setLeakDetectionThreshold(LEAK_DETECTION);

        return config;
    }
}
