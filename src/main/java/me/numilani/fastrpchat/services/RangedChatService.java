package me.numilani.fastrpchat.services;

import me.numilani.fastrpchat.FastRpChat;
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
                return 162;
            case "yell":
                return 54;
            case "local":
                return 18;
            case "quiet":
                return 6;
            case "whisper":
                return 2;
            default:
                throw new Exception(String.format(ChatColor.DARK_RED + "Couldn't figure out what range you wanted to send in! (range value: %s)", range));
        }
    }

    public ChatColor GetRangeColor(String range){
        switch (range){
            case "global", "province":
                return ChatColor.DARK_AQUA;
            case "yell":
                return ChatColor.YELLOW;
            case "local":
                return ChatColor.WHITE;
            case "quiet":
                return ChatColor.GRAY;
            case "whisper":
                return ChatColor.DARK_GRAY;
            default:
                return ChatColor.RESET;
        }
    }

    public void SendRangedMessage(Player player, String formattedMessage, int radius){

        if (radius == -1){
            // do global chat
            for (var p : Bukkit.getOnlinePlayers()){
                p.sendMessage(formattedMessage);
            }
            return;
        }

        // send message to self
        player.sendMessage(formattedMessage);

        // inform player if no one is in range to hear
        if (player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, x -> x instanceof Player).size() <= 1){
            player.sendMessage(ChatColor.DARK_RED + "You speak, but there's no one close enough to hear you...");
            return;
        }

        // if players are in range, send msg to all of them
        for (var entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, x -> x instanceof Player)){
                entity.sendMessage(formattedMessage);
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

        var formattedMsg = String.format("[" + GetRangeColor(range) + range.toUpperCase().toCharArray()[0] + ChatColor.RESET  + "] %s:" + GetRangeColor(range) + " %s", player.getDisplayName(), message);

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
