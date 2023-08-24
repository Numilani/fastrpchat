package me.numilani.fastrpchat.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import me.numilani.fastrpchat.FastRpChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;

public class ChatCommandHandler {

    private FastRpChat plugin;

    // TODO: (FUTURE) move this to config check
    private String[] knownRanges = {"global", "province", "yell", "local", "quiet", "whisper"};

    public ChatCommandHandler(FastRpChat plugin){
        this.plugin = plugin;
    }

//    @CommandMethod("test <blah>")
    public void testBlah(CommandSender sender, @Argument("blah") String blah){
        plugin.getLogger().info(String.format("test command: %s", blah));
    }

    @CommandMethod("ch <range> [msg]")
    public void ChangeChannelOrSend(CommandSender sender, @Argument("range") String range, @Argument("msg") String msg) throws SQLException {
        if (msg == null || msg.isEmpty()){

            if (!Arrays.stream(knownRanges).toList().contains(range.toLowerCase())){
                sender.sendMessage(String.format(ChatColor.DARK_RED + "Unknown range %s", range));
            }
            else{
                plugin.dataSource.setChatRange(((Player) sender).getUniqueId().toString(), range);
                sender.sendMessage(String.format(ChatColor.DARK_GRAY + "Range changed to %s", range));
            }
        }
        else{
            plugin.rangedChatService.SendRangedMessage((Player) sender, msg, range);
        }
    }
}
