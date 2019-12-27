package dev.itseternity.chatfilter.storage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public interface FilterStorage {

    void init();

    void close();

    Set<String> getWhitelistedWords();

    Set<String> getBlacklistedWords();

    int addBlacklistedWord(String word, CommandSender player);

    int addWhitelistedWord(String word, CommandSender name);

    int removeWhitelistedWord(String word);

    int removeBlacklistedWord(String word);
}
