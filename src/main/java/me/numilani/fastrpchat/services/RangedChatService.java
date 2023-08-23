package me.numilani.fastrpchat.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RangedChatService {

    public static void SendRangedMessage(Player player, String message, String range){

        int radius = 0;

        switch (range){
            case "global":
                for (var p : Bukkit.getOnlinePlayers()){
                    p.sendMessage(message); // todo: format properly
                }
                return;
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
                player.sendMessage(String.format("Couldn't figure out what range you wanted to send in! (range value: %s", range));
                return;
        }

        // todo: narrow this to PLAYERS nearby (this checks all entities)
        if (player.getNearbyEntities(radius,radius,radius).isEmpty()){
            player.sendMessage("You speak, but there's no one close enough to hear you...");
        }

        // todo: add player to this as well (player doesn't count as "nearby")
        for (var entity : player.getNearbyEntities(radius, radius, radius)){
            if (entity instanceof Player){
                entity.sendMessage(message); // todo: format properly
            }
        }
    }
}
