package dev.itseternity.chatfilter.filter;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface Filter {

    FilterResult filter(Player player, String message);

}
