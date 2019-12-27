package dev.itseternity.chatfilter;

import co.aikar.commands.CommandManager;
import co.aikar.commands.PaperCommandManager;
import dev.itseternity.chatfilter.chat.ChatListener;
import dev.itseternity.chatfilter.chat.ChatManager;
import dev.itseternity.chatfilter.chat.CommandListener;
import dev.itseternity.chatfilter.commands.AutoMuteCommand;
import dev.itseternity.chatfilter.commands.ChatDelayCommand;
import dev.itseternity.chatfilter.commands.ChatFilterCommand;
import dev.itseternity.chatfilter.commands.ClearChatCommand;
import dev.itseternity.chatfilter.commands.GlobalMuteCommand;
import dev.itseternity.chatfilter.menu.Menu;
import dev.itseternity.chatfilter.storage.FilterStorage;
import dev.itseternity.chatfilter.storage.SQLFilterStorage;
import dev.itseternity.chatfilter.utils.SQLCredentials;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Getter
public final class ChatFilterPlugin extends JavaPlugin {

    private ChatManager chatManager;
    private CommandManager commandManager;

    private FilterStorage storage;

    @Setter
    private  boolean debug = false;

    @Setter
    private int parrotTrigger = 3;

    @Setter
    private int parrotTimer = 1000;

    @Setter
    private double moveTrigger = 5;

    private YamlConfiguration captcha;

    @Override
    public void onEnable() {
        createConfig("config");
        captcha = createConfig("captcha");
        chatManager = new ChatManager(this);

        storage = new SQLFilterStorage(this, SQLCredentials.fromConfig(getConfig().getConfigurationSection("mysql")));
        storage.init();

        Menu.init(this);

        commandManager = new PaperCommandManager(this);
        commandManager.registerDependency(ChatManager.class, chatManager);
        commandManager.registerDependency(FilterStorage.class, storage);
        Stream.of(
                new ChatDelayCommand(),
                new GlobalMuteCommand(),
                new AutoMuteCommand(),
                new ChatFilterCommand(),
                new ClearChatCommand()
        ).forEach(command -> commandManager.registerCommand(command));

        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);
    }

    @Override
    public void onDisable() {
        storage.close();
    }

    public String getMessage(String key) {
        String message = getConfig().getString("messages." + key);
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

    public List<String> getCaptchaList() {
        return captcha.getStringList("players");
    }

    private YamlConfiguration createConfig(String name) {
        File configFile = new File(getDataFolder(), name + ".yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(name + ".yml", false);
        }

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        return config;
    }

    public File getConfigFile(String name) {
       return new File(getDataFolder(), name + ".yml");
    }

    public void staffBroadcast(String message) {
        Bukkit.broadcast(message, "chatfilter.alert");
    }

    public void debug(String message){
        Bukkit.broadcast(message, "chatfilter.debug");
    }
}
