package me.numilani.fastrpchat.listeners;

import me.numilani.fastrpchat.FastRpChat;
import me.numilani.fastrpchat.services.RangedChatService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class FastRpChatListeners implements Listener {

    private FastRpChat plugin;
    public FastRpChatListeners(FastRpChat plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        if (plugin.dataSource.getChatRange(event.getPlayer().getUniqueId().toString()).equals("none")){
            plugin.dataSource.initPlayerRange(event.getPlayer().getUniqueId().toString());
        }
    }

    // todo: listener isn't working as expected
    @EventHandler
    public void onChatAsync(AsyncPlayerChatEvent event) throws SQLException {
        RangedChatService.SendRangedMessage(
                event.getPlayer(),
                event.getMessage(),
                plugin.dataSource.getChatRange(event.getPlayer().getUniqueId().toString()));
    }
}
