package dev.itseternity.chatfilter.chat;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.filter.Filter;
import dev.itseternity.chatfilter.filter.FilterResult;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {

    private final ChatFilterPlugin plugin;

    public CommandListener(ChatFilterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String command = e.getMessage().toLowerCase();

        List<String> commands = plugin.getConfig().getStringList("commands");
        for (String filteredCommand : commands) {
            if (!command.contains(filteredCommand.toLowerCase())) {
                continue;
            }

            for (Filter filter : plugin.getChatManager().getFilters()) {
                FilterResult filterResult = filter.filter(player, command);

                if (filterResult.isShouldCancel()) {
                    e.setCancelled(true);

                } else if (filterResult.isMessageChanged()) {
                    e.setMessage(filterResult.getMessage());
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);
                    player.sendMessage(ChatColor.RED + "Your command was caught by the filter, it has filtered certain words!");
                }
            }
        }
    }
}
