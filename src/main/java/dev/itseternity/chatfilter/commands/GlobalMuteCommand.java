package dev.itseternity.chatfilter.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import dev.itseternity.chatfilter.ChatFilterPlugin;
import dev.itseternity.chatfilter.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("globalmute")
@CommandPermission("chatfilter.globalmute")
public class GlobalMuteCommand extends BaseCommand {

    @Dependency
    private ChatFilterPlugin plugin;

    @Dependency
    private ChatManager chatManager;

    @Default
    public void onDefault(CommandSender sender) {
        boolean toggle = !chatManager.isGlobalMute();
        chatManager.setGlobalMute(toggle);
        Bukkit.broadcast(plugin.getMessage("global-mute")
                .replace("%player%", sender.getName())
                .replace("%state%", toggle ? "enabled" : "disabled"), "group.default");
    }



}
