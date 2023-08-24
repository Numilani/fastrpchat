package me.numilani.fastrpchat.services;

import me.numilani.fastrpchat.FastRpChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class RangedChatService {

    private FastRpChat plugin;

    public RangedChatService(FastRpChat plugin) {
        this.plugin = plugin;
    }

    public void SendRangedMessage(Player player, String message, String range){

        int radius = 0;

        // todo: (FUTURE) move this to config
        switch (range.toLowerCase()){
            case "global":
                for (var p : Bukkit.getOnlinePlayers()){
                    p.sendMessage(String.format("[" + GetRangeColor(range) + range.toUpperCase().toCharArray()[0] + ChatColor.RESET  + "] %s:" + GetRangeColor(range) + " %s", player.getDisplayName(), message));
                }
                return;
            case "province":
                radius = 162;
            case "yell":
                radius = 54;
                break;
            case "local":
                radius = 18;
                break;
            case "quiet":
                radius = 6;
                break;
            case "whisper":
                radius = 2;
                break;
            default:
                player.sendMessage(String.format(ChatColor.DARK_RED + "Couldn't figure out what range you wanted to send in! (range value: %s)", range));
                return;
        }

        player.sendMessage(String.format("[" + GetRangeColor(range) + range.toUpperCase().toCharArray()[0] + ChatColor.RESET  + "] %s:" + GetRangeColor(range) + " %s", player.getDisplayName(), message));

        if (player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, x -> x instanceof Player).size() <= 1){
            player.sendMessage(ChatColor.DARK_RED + "You speak, but there's no one close enough to hear you...");
            return;
        }

        for (var entity : player.getNearbyEntities(radius, radius, radius)){
            if (entity instanceof Player){
                entity.sendMessage(String.format("[" + GetRangeColor(range) + range.toUpperCase().toCharArray()[0] + ChatColor.RESET  + "] %s:" + GetRangeColor(range) + " %s", player.getDisplayName(), message));
            }
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
}
