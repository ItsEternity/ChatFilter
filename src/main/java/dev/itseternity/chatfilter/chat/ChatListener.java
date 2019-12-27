package dev.itseternity.chatfilter.chat;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.filter.Filter;
import dev.itseternity.chatfilter.filter.FilterResult;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class ChatListener implements Listener {

    private static final int DEFAULT_DELAY_SECONDS = 2;

    private final ChatFilterPlugin plugin;

    public ChatListener(ChatFilterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();
        // Global Mute
        if (plugin.getChatManager().isGlobalMute() && !player.hasPermission("chatfilter.staffbypass")) {
            player.sendMessage(plugin.getMessage("chat-disabled"));
            e.setCancelled(true);
            return;
        }

        // Auto Mute
        if (plugin.getChatManager().isAutoMuted(player)) {
            player.sendMessage(plugin.getMessage("auto-mute-muted"));
            e.setCancelled(true);
            return;
        }

        Optional<ChatMessage> optional = plugin.getChatManager().getLastMessage(player);
        if (optional.isPresent()) {
            ChatMessage chatMessage = optional.get();

            if (checkChatDelay(player, chatMessage)) {
                e.setCancelled(true);
                return;
            }

            // Same message as before
            if (chatMessage.getMessage().contentEquals(message)) {
                player.sendMessage(plugin.getMessage("same-message"));
                e.setCancelled(true);
                return;
            }
        }

        ChatMessage chatMessage = new ChatMessage(e.getMessage(), System.currentTimeMillis());
        plugin.getChatManager().setLastMessage(e.getPlayer(), chatMessage);

        for (Filter filter : plugin.getChatManager().getFilters()) {
            FilterResult filterResult = filter.filter(player, message);

            if (filterResult.isShouldCancel()) {
                e.setCancelled(true);

                plugin.staffBroadcast(plugin.getMessage("filter-alert")
                        .replace("%player%", player.getName())
                        .replace("%message%", filterResult.getMessage() + " (" + filter.getClass().getSimpleName() + ")"));
                return;

            } else if (filterResult.isMessageChanged()) {
                e.setMessage(filterResult.getMessage());
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);

                plugin.staffBroadcast(plugin.getMessage("filter-alert")
                        .replace("%player%", player.getName())
                        .replace("%message%", message));
            }
        }
    }

    private boolean hasTimePassed(long when, int duration) {
        long diff = System.currentTimeMillis() - when;
        return diff >= duration * 1000;
    }

    private boolean checkChatDelay(Player player, ChatMessage chatMessage) {
        if (player.hasPermission("chatfilter.staffbypass")) {
            return false;
        }

        // Check if moderator chat delay is enabled
        int chatDelay = plugin.getChatManager().getChatDelay();
        if (chatDelay > 0) {
            if (!hasTimePassed(chatMessage.getTimestamp(), chatDelay)) {
                player.sendMessage(plugin.getMessage("mod-chat-delay"));
                return true;
            }

        } else {
            // Moderator chat delay is not on, using default chat delay
            if (!player.hasPermission("chatfilter.defaultbypass") && !hasTimePassed(chatMessage.getTimestamp(), DEFAULT_DELAY_SECONDS)) {
                player.sendMessage(plugin.getMessage("chat-delay"));
                return true;
            }
        }

        return false;
    }
}
