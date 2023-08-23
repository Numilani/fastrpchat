package me.numilani.fastrpchat.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import me.numilani.fastrpchat.FastRpChat;
import me.numilani.fastrpchat.services.RangedChatService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class ChatCommandHandler {

    private FastRpChat plugin;

    public ChatCommandHandler(FastRpChat plugin){
        this.plugin = plugin;
    }

//    @CommandMethod("test <blah>")
    public void testBlah(CommandSender sender, @Argument("blah") String blah){
        plugin.getLogger().info(String.format("test command: %s", blah));
    }

    @CommandMethod("cr <range> [msg]")
    public void ChangeChannel(CommandSender sender, @Argument("range") String range, @Argument("msg") String msg) throws SQLException {
        if (msg == null || msg.isEmpty()){
            plugin.dataSource.setChatRange(((Player) sender).getUniqueId().toString(), range);
        }
        else{
            RangedChatService.SendRangedMessage((Player) sender, msg, range);
        }
    }
}
