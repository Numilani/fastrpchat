package me.numilani.fastrpchat.services;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.numilani.fastrpchat.FastRpChat;
import net.md_5.bungee.api.chat.BaseComponent;
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

    public void SendRangedMessage(Player player, BaseComponent[] componentMessage, String formatttedMessage, int radius){

        if (radius == -1){
            // do global chat
            for (var p : Bukkit.getOnlinePlayers()){
                if (!plugin.UserRangeMutes.containsKey(p.getUniqueId()) || !plugin.UserRangeMutes.get(p.getUniqueId()).contains(radius)){
                    p.spigot().sendMessage(componentMessage);
                }
            }
            return;
        }

        if (radius == -2){
            for (var p : Bukkit.getOnlinePlayers()){
                if (p.hasPermission("fastrpchat.staffchat")){
                    p.spigot().sendMessage(componentMessage);
                }
            }
            return;
        }

        // send message to self

        // inform player if no one is in range to hear
        if (player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, x -> x instanceof Player).size() <= 1){
            player.spigot().sendMessage(componentMessage);
            Bukkit.getServer().getConsoleSender().sendMessage(formatttedMessage + ChatColor.DARK_GRAY + " [out of range]");
            for (var id : plugin.chatspyUsers) {
                var p = Bukkit.getPlayer(id);
                if (p == null) continue;
//                p.sendMessage(ChatColor.DARK_RED + "[SPY]" + ChatColor.RESET + componentMessage + ChatColor.DARK_GRAY + " [out of range]");
                var fmt = new ComponentBuilder("[SPY]").color(net.md_5.bungee.api.ChatColor.DARK_RED).append("").color(net.md_5.bungee.api.ChatColor.RESET).append(componentMessage).append(" [out of range]").color(net.md_5.bungee.api.ChatColor.DARK_GRAY).create();
                p.spigot().sendMessage(fmt);
            }
            player.sendMessage(ChatColor.DARK_RED + "You speak, but there's no one close enough to hear you...");
            return;
        }

        // if players are in range, send msg to all of them
        for (var entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, x -> x instanceof Player)){
            if (!plugin.UserRangeMutes.containsKey(player.getUniqueId()) || !plugin.UserRangeMutes.get(player.getUniqueId()).contains(radius)){
                entity.spigot().sendMessage(componentMessage);
            }
        }
        Bukkit.getServer().getConsoleSender().sendMessage(formatttedMessage);
        for (var id : plugin.chatspyUsers) {
            var p = Bukkit.getPlayer(id);
            if (p == null) continue;
//            p.sendMessage(ChatColor.DARK_RED + "[SPY]" + ChatColor.RESET + componentMessage);
            var fmt = new ComponentBuilder("[SPY]").color(net.md_5.bungee.api.ChatColor.DARK_RED).append("").color(net.md_5.bungee.api.ChatColor.RESET).append(componentMessage).create();
            p.spigot().sendMessage(fmt);
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

        var spltMessage = message.split("@hand");

        var formattedComponent = new ComponentBuilder("[")
                .append(Character.toString(range.toUpperCase().toCharArray()[0]))
                    .color(GetRangeColor(range).asBungee())
                .append("] ")
                    .color(net.md_5.bungee.api.ChatColor.RESET)
                .append(CreateNameHoverComponent(player))
                .append(": ", ComponentBuilder.FormatRetention.NONE)
                    .color(net.md_5.bungee.api.ChatColor.RESET);

        if (spltMessage.length == 0){
            formattedComponent = formattedComponent.append(CreateItemHoverComponent(player), ComponentBuilder.FormatRetention.NONE);
        }
        else if (spltMessage.length == 1){
            formattedComponent = formattedComponent.append(message, ComponentBuilder.FormatRetention.NONE)
                    .color(GetRangeColor(range).asBungee());
        }
        else{
            for (int i = 0; i < spltMessage.length - 1; i++) {
                formattedComponent = formattedComponent.append(spltMessage[i], ComponentBuilder.FormatRetention.NONE)
                        .color(GetRangeColor(range).asBungee())
                        .append(CreateItemHoverComponent(player));
            }
            formattedComponent = formattedComponent.append(spltMessage[spltMessage.length-1], ComponentBuilder.FormatRetention.NONE)
                    .color(GetRangeColor(range).asBungee());

        }
        var finalComponent = formattedComponent.create();


        SendRangedMessage(player, finalComponent, formattedMsg, radius);
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

        var spltMessage = message.split("@hand");

        var formattedComponent = new ComponentBuilder("[")
                .append(Character.toString(range.toUpperCase().toCharArray()[0]))
                    .color(GetRangeColor(range).asBungee())
                .append("] ")
                    .color(net.md_5.bungee.api.ChatColor.RESET)
                .append(CreateNameHoverComponent(player))
                .append(" ", ComponentBuilder.FormatRetention.NONE)
                    .color(net.md_5.bungee.api.ChatColor.RESET);

        if (spltMessage.length == 1){
            formattedComponent = formattedComponent.append(message)
                .color(GetRangeColor(range).asBungee())
                .italic(true);
        }
        else{
            for (int i = 0; i < spltMessage.length - 1; i++) {
                formattedComponent = formattedComponent.append(spltMessage[i], ComponentBuilder.FormatRetention.NONE)
                    .color(GetRangeColor(range).asBungee())
                    .italic(true)
                .append(CreateItemHoverComponent(player));
            }
            formattedComponent = formattedComponent.append(spltMessage[spltMessage.length-1], ComponentBuilder.FormatRetention.NONE)
                .color(GetRangeColor(range).asBungee())
                .italic(true);

        }
        var finalComponent = formattedComponent.create();

        SendRangedMessage(player, finalComponent, formattedMsg, radius);
    }



    public TextComponent CreateItemHoverComponent(Player player){
        var item = NBTEditor.getNBTCompound(player.getInventory().getItemInMainHand());
        if (player.getInventory().getItemInMainHand().getItemMeta() == null){
            return new TextComponent("[air]");
        }
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()){
            var itemName = player.getInventory().getItemInMainHand().getType().name().replace("_", " ").toLowerCase();
            var x = new TextComponent("[" + itemName + "]");
            x.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(item.toJson()).create()));
            return x;
        }
        var x = new TextComponent("[" + player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() + "]");
        x.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(item.toJson()).create()));
        return x;
    }

    public TextComponent CreateNameHoverComponent(Player player){
        var x = new TextComponent(player.getDisplayName());
        x.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.format("Username: %s", player.getName())).create()));
        return x;
    }

}
