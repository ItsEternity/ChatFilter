package dev.itseternity.chatfilter.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface MenuItem {

    ItemStack getItem();

    void onClick(Player player, InventoryClickEvent e);
}
