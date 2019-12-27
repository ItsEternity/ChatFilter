package dev.itseternity.chatfilter.filter;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class AdvertisingFilter implements Filter {

    private static final Pattern URL_REGEX = Pattern.compile(
            "^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
    private static final Pattern IP_REGEX = Pattern.compile(
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    private final ChatFilterPlugin plugin;

    public AdvertisingFilter(ChatFilterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public FilterResult filter(Player player, String message) {
        if (player.hasPermission("chatfilter.staff")) {
            return new FilterResult(false, false, message);
        }

        boolean caught = IP_REGEX.matcher(message).find() || URL_REGEX.matcher(message).find();
        if (caught) {
            plugin.staffBroadcast(plugin.getMessage("filter-alert")
                    .replace("%player%", player.getName())
                    .replace("%message%", message));
        }
        return new FilterResult(caught, false, message);
    }
}
