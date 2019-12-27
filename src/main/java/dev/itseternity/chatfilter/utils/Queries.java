package dev.itseternity.chatfilter.utils;

// I really don't like this class
// just want to put his out there
public class Queries {

    private static final String WHITELIST_TABLE = "whitelisted_words";
    private static final String BLACKLIST_TABLE = "blacklisted_words";

    public static final String WORD = "word";
    public static final String ADDED_BY = "added_by";

    public static final String CREATE_WHITELIST_TABLE = "CREATE TABLE IF NOT EXISTS " + WHITELIST_TABLE + "(" +
            WORD + " VARCHAR(36) NOT NULL, " +
            "PRIMARY KEY (" + WORD + ") " +
            ") ENGINE=INNODB";
    public static final String CREATE_BLACKLIST_TABLE = "CREATE TABLE IF NOT EXISTS " + BLACKLIST_TABLE + "(" +
            WORD + " VARCHAR(36) NOT NULL, " +
            "PRIMARY KEY (" + WORD + ") " +
            ") ENGINE=INNODB";

    public static final String GET_WHITELISTED_WORDS = "SELECT * FROM " + WHITELIST_TABLE + ";";
    public static final String GET_BLACKLISTED_WORDS = "SELECT * FROM " + BLACKLIST_TABLE + ";";
    public static final String INSERT_WHITELIST = "INSERT INTO " + WHITELIST_TABLE + " (" +
            WORD + "," +
            ADDED_BY +
            ") VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE " + WORD + "=" + WORD + ";";

    public static final String INSERT_BLACKLIST = "INSERT INTO " + BLACKLIST_TABLE + "(" +
            WORD + "," +
            ADDED_BY +
            ") VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE " + WORD + "=" + WORD + ";";
    public static final String DELETE_WHITELIST = "DELETE FROM " + WHITELIST_TABLE + " WHERE " + WORD + "=?";
    public static final String DELETE_BLACKLIST = "DELETE FROM " + BLACKLIST_TABLE + " WHERE " + WORD + "=?";
}
