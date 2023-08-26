package me.numilani.fastrpchat.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.specifier.Greedy;
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
    public void ChangeChannelOrSend(CommandSender sender, @Argument("range") String range, @Greedy @Argument("msg") String msg) throws SQLException {
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
            plugin.rangedChatService.SendRangedChat((Player) sender, msg, range);
        }
    }

    @CommandMethod("emote <range> <emote>")
    public void SendEmote(CommandSender sender, @Argument("range") String range, @Greedy @Argument("emote") String emote){
            if (!Arrays.stream(knownRanges).toList().contains(range.toLowerCase())){
                sender.sendMessage(String.format(ChatColor.DARK_RED + "Unknown range %s", range));
            }
        else{
            plugin.rangedChatService.SendRangedEmote((Player) sender, emote, range);
        }
    }

    @CommandMethod("ranges")
    public void ListRanges(CommandSender sender) throws Exception {
        sender.sendMessage(String.format(ChatColor.BOLD + "" +  ChatColor.GRAY + "=== RANGES ==="));
        for (var rg: knownRanges) {
            sender.sendMessage(String.format("- "+ plugin.rangedChatService.GetRangeColor(rg) + rg + ChatColor.RESET + " (%s blocks)", plugin.rangedChatService.GetRangeRadius(rg)));
        }
    }


    // EVERYTHING BELOW THIS LINE IS A PROXY FOR THE ABOVE COMMANDS
    // EVENTUALLY THIS SHOULD ALL GET REFACTORED TO BE DYNAMIC COMMANDS

    @ProxiedBy("g")
    @CommandMethod("global [msg]")
    public void ChatGlobal(CommandSender sender, @Greedy @Argument("msg") String msg) throws SQLException {
        ChangeChannelOrSend(sender, "global", msg);
    }

    @ProxiedBy("p")
    @CommandMethod("province [msg]")
    public void ChatProvince(CommandSender sender, @Greedy @Argument("msg") String msg) throws SQLException {
        ChangeChannelOrSend(sender, "province", msg);
    }

    @ProxiedBy("y")
    @CommandMethod("yell [msg]")
    public void ChatYell(CommandSender sender, @Greedy @Argument("msg") String msg) throws SQLException {
        ChangeChannelOrSend(sender, "yell", msg);
    }

    @ProxiedBy("l")
    @CommandMethod("local [msg]")
    public void ChatLocal(CommandSender sender, @Greedy @Argument("msg") String msg) throws SQLException {
        ChangeChannelOrSend(sender, "local", msg);
    }

    @ProxiedBy("q")
    @CommandMethod("quiet [msg]")
    public void ChatQuiet(CommandSender sender, @Greedy @Argument("msg") String msg) throws SQLException {
        ChangeChannelOrSend(sender, "quiet", msg);
    }

    @ProxiedBy("wh")
    @CommandMethod("whisper [msg]")
    public void ChatWhisper(CommandSender sender, @Greedy @Argument("msg") String msg) throws SQLException {
        ChangeChannelOrSend(sender, "whisper", msg);
    }

    @CommandMethod("meg <emote>")
    public void EmoteGlobal(CommandSender sender, @Greedy @Argument("emote") String emote){
        SendEmote(sender, "global", emote);
    }

    @CommandMethod("mep <emote>")
    public void EmoteProvince(CommandSender sender, @Greedy @Argument("emote") String emote){
        SendEmote(sender, "province", emote);
    }

    @CommandMethod("mey <emote>")
    public void EmoteYell(CommandSender sender, @Greedy @Argument("emote") String emote){
        SendEmote(sender, "yell", emote);
    }

    @CommandMethod("mel <emote>")
    public void EmoteLocal(CommandSender sender, @Greedy @Argument("emote") String emote){
        SendEmote(sender, "local", emote);
    }

    @CommandMethod("meq <emote>")
    public void EmoteQuiet(CommandSender sender, @Greedy @Argument("emote") String emote){
        SendEmote(sender, "quiet", emote);
    }

    @ProxiedBy("mewh")
    @CommandMethod("mew <emote>")
    public void EmoteWhisper(CommandSender sender, @Greedy @Argument("emote") String emote){
        SendEmote(sender, "whisper", emote);
    }

}
