package dev.itseternity.chatfilter.chat;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class FilterUpdateTask extends BukkitRunnable {

    private final ChatFilterPlugin plugin;

    public FilterUpdateTask(ChatFilterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Set<String> blacklisted = plugin.getStorage().getBlacklistedWords();
        Set<String> whitelisted = plugin.getStorage().getWhitelistedWords();

        int blacklistedDiff = blacklisted.size() - plugin.getChatManager().getBlacklistedWords().size();
        int whitelistedDiff = whitelisted.size() - plugin.getChatManager().getWhitelistedWords().size();

        plugin.getLogger().info("Updating blacklisted words: " + blacklistedDiff + " word(s)");
        plugin.getLogger().info("Updating whitelisted words: " + whitelistedDiff + " word(s)");

        plugin.getChatManager().getBlacklistedWords().addAll(blacklisted);
        plugin.getChatManager().getWhitelistedWords().addAll(whitelisted);
    }
}
