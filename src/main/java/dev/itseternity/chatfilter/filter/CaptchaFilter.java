package dev.itseternity.chatfilter.filter;

import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.menu.ItemBuilder;
import dev.itseternity.chatfilter.menu.Menu;
import dev.itseternity.chatfilter.menu.MenuItem;
import dev.itseternity.chatfilter.menu.MenuSize;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CaptchaFilter implements Filter, Listener {

    private final ChatFilterPlugin plugin;

    public CaptchaFilter(ChatFilterPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        System.out.println(player.hasPlayedBefore());
        if (player.hasPlayedBefore()) {
            return;
        }

        plugin.getCaptchaList().add(player.getUniqueId().toString());
        plugin.getCaptcha().save(plugin.getConfigFile());
    }

    @Override
    public FilterResult filter(Player player, String message) {
        if (!plugin.getCaptchaList().contains(player.getUniqueId().toString())) {
            return new FilterResult(false, false, message);
        }

        player.sendMessage(ChatColor.RED + "Opening captcha menu...");
        new CaptchaMenu(plugin, player);
        return new FilterResult(true, false, message);
    }
}

class CaptchaMenu {

    private static final List<Material> MATERIALS = ImmutableList.copyOf(Arrays.stream(Material.values())
            .filter(Material::isItem)
            .filter(material -> material != Material.DIAMOND)
            .collect(Collectors.toList()));

    private final Menu menu;

    CaptchaMenu(ChatFilterPlugin plugin, Player player) {
        this.menu = new Menu("Please select the diamond", MenuSize.SIX_ROWS);

        int slot = ThreadLocalRandom.current().nextInt(MenuSize.FIVE_ROWS);
        for (int i = 0; i < MenuSize.FIVE_ROWS; i++) {
            if (i == slot) {
                menu.addItem(new MenuItem() {
                    @Override
                    public ItemStack getItem() {
                        return ItemBuilder.of(Material.DIAMOND)
                                .name(ChatColor.GREEN + "Click me")
                                .lore(ChatColor.GRAY + "Select this diamond to complete the captcha!")
                                .build();
                    }

                    @Override
                    public void onClick(Player player, InventoryClickEvent e) {
                        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        plugin.getCaptchaList().remove(player.getUniqueId().toString());
                        plugin.getCaptcha().save();
                    }
                });
            } else {
                menu.addItem(new MenuItem() {
                    @Override
                    public ItemStack getItem() {
                        return ItemBuilder.of(getRandomMaterial())
                                .name(" ")
                                .build();
                    }

                    @Override
                    public void onClick(Player player, InventoryClickEvent e) {

                    }
                });
            }
        }

        menu.setOnClose(p -> {
            if (plugin.getCaptchaList().contains(p.getUniqueId().toString())) {
                p.sendMessage(ChatColor.RED + "You failed the captcha!");
            } else {
                p.sendMessage(ChatColor.GREEN + "You have completed the captcha, you may now speak...");
            }
        });

        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(menu.getInventory()));
    }

    private Material getRandomMaterial() {
        Random random = ThreadLocalRandom.current();
        return MATERIALS.get(random.nextInt(MATERIALS.size()));
    }
}
