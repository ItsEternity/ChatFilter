package dev.itseternity.chatfilter.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class Menu implements InventoryHolder {

    private static final Consumer<Player> EMPTY_CONSUMER = player -> { };

    private final String name;
    private final int size;

    private final Map<Integer, MenuItem> items = new HashMap<>();

    private Consumer<Player> onClose = EMPTY_CONSUMER;

    public Menu(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public static void init(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
    }

    public Menu addItem(MenuItem item) {
        int slot = 0;

        for (int i = 0; i <= items.keySet().size(); i++) {
            Optional<MenuItem> optional = getItem(i);
            if (!optional.isPresent()) {
                slot = i;
                break;
            }
        }

        items.put(slot, item);
        return this;
    }

    public Menu setItem(int slot, MenuItem item) {
        items.put(slot, item);
        return this;
    }

    public Optional<MenuItem> getItem(int slot) {
        return Optional.ofNullable(items.get(slot));
    }

    public Consumer<Player> getOnClose() {
        return onClose;
    }

    public void setOnClose(Consumer<Player> onClose) {
        this.onClose = onClose;
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, size, name);
        items.forEach((slot, menuItem) -> inventory.setItem(slot, menuItem.getItem()));

        return inventory;
    }
}
