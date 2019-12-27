package dev.itseternity.chatfilter.utils;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

@Data
public class SQLCredentials {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    public static SQLCredentials fromConfig(ConfigurationSection section) {
        return new SQLCredentials(
                section.getString("host"),
                section.getInt("port"),
                section.getString("username"),
                section.getString("password"),
                section.getString("database")
        );
    }
}
