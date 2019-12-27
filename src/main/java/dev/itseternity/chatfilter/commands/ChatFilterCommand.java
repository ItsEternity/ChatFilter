package dev.itseternity.chatfilter.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.chat.ChatManager;
import dev.itseternity.chatfilter.storage.FilterStorage;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.DataMutateResult;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.stream.Stream;

@CommandAlias("chatfilter")
@CommandPermission("chatfilter.filter")
public class ChatFilterCommand extends BaseCommand {

    @Dependency
    private ChatFilterPlugin plugin;

    @Dependency
    private ChatManager chatManager;

    @Dependency
    private FilterStorage storage;

    @Default
    @CommandPermission("chatfilter.filter")
    public void onDefault(CommandSender sender) {
        Stream.of(
                ChatColor.GREEN + "/filter wl <word> - Add a word to the whitelist",
                ChatColor.GREEN + "/filter bl <word> - Add a word to the blacklist",
                ChatColor.GREEN + "/filter unwl <word> - Remove a word from the blacklist",
                ChatColor.GREEN + "/filter unbl <word> - Remove a word from the blacklist"
        ).forEach(sender::sendMessage);
    }

    @Subcommand("notification")
    @CommandPermission("group.helper")
    public void notification(Player player) {
        LuckPermsApi api = LuckPerms.getApi();
        User user = api.getUser(player.getUniqueId());
        if (user == null) {
            player.sendMessage(ChatColor.RED + "Could not grab your LuckPerms data!");
            return;
        }

        Node node = api.buildNode("chatfilter.alert").build();
        DataMutateResult result;
        boolean removed = true;
        if (player.hasPermission("chatfilter.alert")) {
            result = user.unsetPermission(node);

        } else {
            result = user.setPermission(node);
            removed = false;
        }

        api.getUserManager().saveUser(user);
        if (result.wasFailure()) {
            player.sendMessage(ChatColor.RED + "Something went wrong with changing your permission: " + result.name());
        } else {
            player.sendMessage(ChatColor.GREEN + "Turned " + (removed ? "off" : "on") + " ChatFilter notifications");
        }
    }

    @Subcommand("parrotfilter")
    @CommandPermission("chatfilter.filter")
    public void setParrotTrigger(CommandSender sender, Integer newAmount) {
        plugin.setParrotTrigger(newAmount);
        sender.sendMessage(ChatColor.GREEN + "You have set the parrot trigger to " + newAmount);
    }

    @Subcommand("parrottimer")
    @CommandPermission("chatfilter.filter")
    public void setParrotTimer(CommandSender sender, Integer newAmount) {
        plugin.setParrotTimer(newAmount * 1000);
        sender.sendMessage(ChatColor.GREEN + "You have set the parrot timer to " + newAmount);
    }

    @Subcommand("movefilter")
    @CommandPermission("chatfilter.filter")
    public void setMoveTrigger(CommandSender sender, Integer newAmount) {
        plugin.setMoveTrigger(newAmount);
        sender.sendMessage(ChatColor.GREEN + "You have set the move trigger to " + newAmount);
    }

    @Subcommand("reload")
    @CommandPermission("group.developer")
    public void reload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Reloaded the config");
    }

    @Subcommand("whitelist|wl")
    @CommandPermission("chatfilter.filter")
    public void whitelist(CommandSender sender, String word) {
        boolean added = chatManager.getWhitelistedWords().add(word);
        if (added) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int count = storage.addWhitelistedWord(word, sender);
                handleUpdateCount(sender, count, v -> plugin.staffBroadcast(plugin.getMessage("filter-whitelist")
                        .replace("%player%", sender.getName())
                        .replace("%word%", word)));
            });

        } else {
            sender.sendMessage(plugin.getMessage("filter-exists")
                    .replace("%word%", word));
        }
    }

    @Subcommand("blacklist|bl")
    @CommandPermission("chatfilter.filter")
    public void blacklist(CommandSender sender, String word) {
        boolean added = chatManager.getBlacklistedWords().add(word);
        if (added) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int count = storage.addBlacklistedWord(word, sender);
                handleUpdateCount(sender, count, v -> plugin.staffBroadcast(plugin.getMessage("filter-blacklist")
                        .replace("%player%", sender.getName())
                        .replace("%word%", word)));
            });

        } else {
            sender.sendMessage(plugin.getMessage("filter-exists")
                    .replace("%word%", word));
        }
    }

    @Subcommand("unwhitelist|unwl")
    @CommandPermission("chatfilter.filter")
    public void unWhitelist(CommandSender sender, String word) {
        boolean removed = chatManager.getWhitelistedWords().remove(word);
        if (removed) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int count = storage.removeWhitelistedWord(word);
                handleUpdateCount(sender, count, v -> plugin.staffBroadcast(plugin.getMessage("filter-unwhitelist")
                        .replace("%player%", sender.getName())
                        .replace("%word%", word)));
            });

        } else {
            sender.sendMessage(plugin.getMessage("filter-not-exists")
                    .replace("%word%", word));
        }
    }

    @Subcommand("unblacklist|unbl")
    @CommandPermission("chatfilter.filter")
    public void unBlacklist(CommandSender sender, String word) {
        boolean removed = chatManager.getBlacklistedWords().remove(word);
        if (removed) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int count = storage.removeBlacklistedWord(word);
                handleUpdateCount(sender, count, v -> plugin.staffBroadcast(plugin.getMessage("filter-unblacklist")
                        .replace("%player%", sender.getName())
                        .replace("%word%", word)));
            });

        } else {
            sender.sendMessage(plugin.getMessage("filter-not-exists")
                    .replace("%word%", word));
        }
    }

    @Subcommand("migrate")
    @CommandPermission("chatfilter.migrate")
    public void migrate(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Attempting to migrate words...");
        for (String curse : chatManager.getCurses()) {
            storage.addBlacklistedWord(curse, sender);
        }
    }

    @Subcommand("debug")
    @CommandPermission("chatfilter.debug")
    public void debug(CommandSender sender) {
        boolean debug = !plugin.isDebug();
        plugin.setDebug(debug);
        sender.sendMessage(ChatColor.GREEN + "Debug set to: " + debug);
    }

    private void handleUpdateCount(CommandSender player, int count, Consumer<Void> onSuccess) {
        if (count == -1) {
            player.sendMessage(ChatColor.RED + "Something went wrong with updating SQL!");
            return;
        }

        onSuccess.accept(null);
    }

    private Node getNotificationNode(LuckPermsApi api) {
        return api.buildNode("chatfilter.alert").build();
    }
}
