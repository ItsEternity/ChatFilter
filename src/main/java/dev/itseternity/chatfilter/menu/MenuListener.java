package dev.itseternity.chatfilter.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    @EventHandler
    public void on(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();

        // Check if a player clicked the inventory
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        // If it is null or the holder is not an instance of OldPaginatedMenu, return
        if (inventory == null || !(inventory.getHolder() instanceof Menu)) {
            return;
        }

        // It is one our Menus so let's cancel the event
        e.setCancelled(true);

        Menu menu = (Menu) inventory.getHolder();
        Player player = (Player) e.getWhoClicked();
        menu.getItem(e.getSlot()).ifPresent(menuItem -> menuItem.onClick(player, e));
    }

    @EventHandler
    public void on(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();

        if (!(inventory.getHolder() instanceof Menu)) {
            return;
        }

        Menu menu = (Menu) inventory.getHolder();
        Player player = (Player) e.getPlayer();

        menu.getOnClose().accept(player);
    }
}
