package dev.itseternity.chatfilter.filter;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoveFilter implements Filter, Listener {

    private final ChatFilterPlugin plugin;
    private final Map<UUID, Location> loginLocations = new HashMap<>();

    public MoveFilter(ChatFilterPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("group.donor") || player.hasPermission("chatfilter.staff")) {
            return;
        }

        loginLocations.put(player.getUniqueId(), player.getLocation());
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        loginLocations.remove(player.getUniqueId());
    }

    @Override
    public FilterResult filter(Player player, String message) {
        if (!loginLocations.containsKey(player.getUniqueId())) {
            return new FilterResult(false,  false, message);
        }

        Location location = loginLocations.get(player.getUniqueId());
        if (location.getWorld().equals(player.getWorld())) {
            if (location.distance(player.getLocation()) >= plugin.getMoveTrigger()) {
                loginLocations.remove(player.getUniqueId());

            } else {
                player.sendMessage(ChatColor.RED + "Please move around before you try and speak!");
                return new FilterResult(true,  false, message);
            }

        } else {
            loginLocations.remove(player.getUniqueId());
        }

        return new FilterResult(false,  false, message);
    }
}
