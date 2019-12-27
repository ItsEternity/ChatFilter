package dev.itseternity.chatfilter.filter;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import org.bukkit.entity.Player;

public class AutoMuteFilter implements Filter {

    private final ChatFilterPlugin plugin;

    public AutoMuteFilter(ChatFilterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public FilterResult filter(Player player, String message) {
        if (player.hasPermission("chatfilter.staffbypass")) {
            return new FilterResult(false, false, message);
        }

        for (String mutedWord : plugin.getChatManager().getAutoMuteWords()) {
            if (message.contains(mutedWord)) {
                plugin.getChatManager().autoMute(player);
                plugin.staffBroadcast(plugin.getMessage("filter-alert")
                        .replace("%player%", player.getName())
                        .replace("%message%", message)
                        .concat(" [AutoMute]"));
                return new FilterResult(true, false, message);
            }
        }

        return new FilterResult(false, false, message);
    }
}
