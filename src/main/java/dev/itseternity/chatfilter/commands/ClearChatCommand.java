package dev.itseternity.chatfilter.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.itseternity.chatfilter.ChatFilterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("clearchat")
public class ClearChatCommand extends BaseCommand {

    @Dependency
    private ChatFilterPlugin plugin;

    @Default
    @CommandPermission("chatfilter.clearchat")
    public void onDefault(CommandSender sender) {
        String staff = plugin.getMessage("chat-cleared-staff")
                .replace("%staff%", sender.getName());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("chatfilter.staffbypass")) {
                onlinePlayer.sendMessage(staff);
            } else {
                for (int i = 0; i <= 150; i++) {
                    onlinePlayer.sendMessage(" ");
                }
                onlinePlayer.sendMessage(plugin.getMessage("chat-cleared"));
            }
        }

    }
}
