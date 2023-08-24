package me.numilani.fastrpchat.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import me.numilani.fastrpchat.FastRpChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NameUtilityCommandHandler {
    private FastRpChat plugin;

    public NameUtilityCommandHandler(FastRpChat plugin){
        this.plugin = plugin;
    }

    // TODO: add preprocessor for autocompleting usernames of online players
    @CommandMethod("nickname <username>")
    public void Nickname(CommandSender sender, @Argument("username")String username){
        var results = Bukkit.getOnlinePlayers().stream().filter(x -> x.getName().equals(username)).toList();
        if (results.isEmpty()){
            sender.sendMessage(String.format(ChatColor.DARK_RED + "No player nick found for player %s", username));
        } else{
            sender.sendMessage(String.format(ChatColor.WHITE + username + ChatColor.GOLD + " is " + ChatColor.WHITE + results.stream().findFirst().get().getDisplayName()));
        }
    }
}
