package me.numilani.fastrpchat.services;

import me.numilani.fastrpchat.FastRpChat;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RangedChatService {

    private FastRpChat plugin;

    public RangedChatService(FastRpChat plugin) {
        this.plugin = plugin;
    }

    public int GetRangeRadius(String range) throws Exception {
        switch (range.toLowerCase()){
            case "global":
                return -1;
            case "province":
                return 192;
            case "yell":
                return 64;
            case "local":
                return 20;
            case "quiet":
                return 8;
            case "whisper":
                return 3;
            case "staff":
                return -2;
            default:
                throw new Exception(String.format(ChatColor.DARK_RED + "Couldn't figure out what range you wanted to send in! (range value: %s)", range));
        }
    }

    public ChatColor GetRangeColor(String range){
        switch (range){
            case "global":
                return ChatColor.AQUA;
            case "province":
                return ChatColor.DARK_AQUA;
            case "yell":
                return ChatColor.YELLOW;
            case "local":
                return ChatColor.WHITE;
            case "quiet":
                return ChatColor.GRAY;
            case "whisper":
                return ChatColor.DARK_GRAY;
            case "staff":
                return ChatColor.DARK_GREEN;
            default:
                return ChatColor.RESET;
        }
    }

    public void SendRangedMessage(Player player, String formattedMessage, int radius){

        if (radius == -1){
            // do global chat
            for (var p : Bukkit.getOnlinePlayers()){
                if (!plugin.UserRangeMutes.containsKey(p.getUniqueId()) || !plugin.UserRangeMutes.get(p.getUniqueId()).contains(radius)){
                    p.sendMessage(formattedMessage);
                }
//                p.sendMessage(formattedMessage);
            }
            return;
        }

        if (radius == -2){
            for (var p : Bukkit.getOnlinePlayers()){
                if (p.hasPermission("fastrpchat.staffchat")){
                    p.sendMessage(formattedMessage);
                }
            }
            return;
        }

        // send message to self

        // inform player if no one is in range to hear
        if (player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, x -> x instanceof Player).size() <= 1){
            player.sendMessage(formattedMessage);
            Bukkit.getServer().getConsoleSender().sendMessage(formattedMessage + ChatColor.DARK_GRAY + " [out of range]");
            for (var id : plugin.chatspyUsers) {
                var p = Bukkit.getPlayer(id);
                if (p == null) continue;
                p.sendMessage(ChatColor.DARK_RED + "[SPY]" + ChatColor.RESET + formattedMessage + ChatColor.DARK_GRAY + " [out of range]");
            }
            player.sendMessage(ChatColor.DARK_RED + "You speak, but there's no one close enough to hear you...");
            return;
        }

        // if players are in range, send msg to all of them
        for (var entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, x -> x instanceof Player)){
            if (!plugin.UserRangeMutes.containsKey(player.getUniqueId()) || !plugin.UserRangeMutes.get(player.getUniqueId()).contains(radius)){
                entity.sendMessage(formattedMessage);
            }
        }
        Bukkit.getServer().getConsoleSender().sendMessage(formattedMessage);
        for (var id : plugin.chatspyUsers) {
            var p = Bukkit.getPlayer(id);
            if (p == null) continue;
            p.sendMessage(ChatColor.DARK_RED + "[SPY]" + ChatColor.RESET + formattedMessage);
        }
    }

    public void SendRangedChat(Player player, String message, String range){

        int radius;

        try{
            radius = GetRangeRadius(range);
        }catch (Exception e){
            player.sendMessage(e.getMessage());
            return;
        }

        var formattedMsg = String.format("[" + GetRangeColor(range) + range.toUpperCase().toCharArray()[0] + ChatColor.RESET  + "] %s:" + GetRangeColor(range) + " %s",     player.getDisplayName(), message);

        SendRangedMessage(player, formattedMsg, radius);
    }

    public void SendRangedEmote(Player player, String message, String range){
        int radius;

        try{
            radius = GetRangeRadius(range);
        }catch (Exception e){
            player.sendMessage(e.getMessage());
            return;
        }

        var formattedMsg = String.format("[" + GetRangeColor(range) + range.toUpperCase().toCharArray()[0] + ChatColor.RESET  + "] %s" + GetRangeColor(range) + ChatColor.ITALIC + " %s", player.getDisplayName(), message);

        SendRangedMessage(player, formattedMsg, radius);
    }

}
