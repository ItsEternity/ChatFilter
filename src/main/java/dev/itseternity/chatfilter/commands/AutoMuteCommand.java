package dev.itseternity.chatfilter.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.chat.ChatManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("automute")
@CommandPermission("chatfilter.automute")
public class AutoMuteCommand extends BaseCommand {

    @Dependency
    private ChatFilterPlugin plugin;

    @Dependency
    private ChatManager chatManager;

    @Default
    public void onDefault(CommandSender sender) {
        boolean toggle = !chatManager.isAutoMute();
        chatManager.setAutoMute(toggle);

        plugin.staffBroadcast(plugin.getMessage("auto-mute")
                .replace("%player%", sender.getName())
                .replace("%state%", String.valueOf(toggle)));
    }

    @Subcommand("add")
    public void add(CommandSender sender, @Single String word) {
        boolean result = chatManager.addAutoMuteWord(word);
        if (result) {
            plugin.staffBroadcast(plugin.getMessage("auto-mute-add")
                    .replace("%player%", sender.getName())
                    .replace("%word%", word));
        } else {
            sender.sendMessage(plugin.getMessage("auto-mute-exists")
                    .replace("%word%", word));
        }
    }

    @Subcommand("remove")
    public void remove(CommandSender sender, @Single String word) {
        boolean result = chatManager.removeAutoMuteWord(word);
        if (result) {
            plugin.staffBroadcast(plugin.getMessage("auto-mute-remove")
                    .replace("%player%", sender.getName())
                    .replace("%word%", word));
        } else {
            sender.sendMessage(plugin.getMessage("auto-mute-doesnt-exist")
                    .replace("%word%", word));
        }
    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        int size = chatManager.getAutoMuteWords().size();

        if (size == 0) {
            sender.sendMessage(plugin.getMessage("auto-mute-empty"));
            return;
        }

        sender.sendMessage(plugin.getMessage("auto-mute-list-header")
                .replace("%amount%", String.valueOf(size)));
        for (String word : chatManager.getAutoMuteWords()) {
            sender.sendMessage(plugin.getMessage("auto-mute-list")
                    .replace("%word%", word));
        }
    }

    @Subcommand("unmute")
    @CommandCompletion("@players")
    public void unmute(CommandSender sender, OnlinePlayer onlinePlayer) {
        Player player = onlinePlayer.getPlayer();
        if (!chatManager.isAutoMuted(player)) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not AutoMuted!");
            return;
        }

        chatManager.removeAutoMute(player);
        sender.sendMessage(ChatColor.GREEN + "Removed " + player.getName() + "'s AutoMute!");
    }

    @Subcommand("check")
    public void check(CommandSender sender, OnlinePlayer player) {
        sender.sendMessage(plugin.getMessage("auto-mute-check")
                .replace("%player%", player.getPlayer().getName())
                .replace("%state%", String.valueOf(chatManager.isAutoMuted(player.getPlayer()))));
    }
}
