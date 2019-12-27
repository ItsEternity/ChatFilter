package dev.itseternity.chatfilter.filter;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import org.bukkit.entity.Player;

public class ParrotFilter implements Filter {

    private final ChatFilterPlugin plugin;

    private String lastMessage = "FIRST_MESSAGE";
    private long lastMessageTime = System.currentTimeMillis();
    private int counter = 0;

    public ParrotFilter(ChatFilterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public FilterResult filter(Player player, String message) {
        if (player.hasPermission("chatfilter.staffbypass")) {
            return new FilterResult(false, false, message);
        }

        if (message.startsWith(lastMessage)) {
            if ((System.currentTimeMillis() - lastMessageTime) <= 1000) {
                counter += 1;

                if (counter >= plugin.getParrotTrigger()) {
                    return new FilterResult(true, false, message + " [" + counter + "|" + plugin.getParrotTrigger() + "]");
                }
            }

        } else {
            lastMessage = message;
            counter = 0;
        }

        return new FilterResult(false, false, message);
    }
}
