package dev.itseternity.chatfilter.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.chat.ChatManager;
import org.bukkit.command.CommandSender;

@CommandAlias("chatdelay")
@CommandPermission("chatfilter.chatdelay")
public class ChatDelayCommand extends BaseCommand {

    @Dependency
    private ChatFilterPlugin plugin;

    @Dependency
    private ChatManager chatManager;

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage(plugin.getMessage("chatdelay-state")
                .replace("%delay%", String.valueOf(chatManager.getChatDelay())));
    }

    @Subcommand("set")
    public void set(CommandSender sender, int delay) {
        if (delay < 0) {
            delay = 0;
        }

        chatManager.setChatDelay(delay);
        if (delay == 0) {
            plugin.staffBroadcast(plugin.getMessage("chatdelay-off")
                    .replace("%player%", sender.getName())
                    .replace("%delay%", String.valueOf(delay)));

        } else {
            plugin.staffBroadcast(plugin.getMessage("chatdelay-set")
                    .replace("%player%", sender.getName()));

        }
    }

    @Subcommand("off")
    public void off(CommandSender sender) {
        chatManager.setChatDelay(0);
        plugin.staffBroadcast(plugin.getMessage("chatdelay-off")
                .replace("%player%", sender.getName()));

    }
}
