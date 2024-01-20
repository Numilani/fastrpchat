package me.numilani.fastrpchat.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.specifier.FlagYielding;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import me.numilani.fastrpchat.FastRpChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ChatCommandHandler {

    private FastRpChat plugin;

    // TODO: (FUTURE) move this to config check
    private String[] knownRanges = {"global", "province", "yell", "local", "quiet", "whisper", "staff"};

    public ChatCommandHandler(FastRpChat plugin){
        this.plugin = plugin;
    }

    @CommandMethod("ch <range> [msg]")
    public void ChangeChannelOrSend(CommandSender sender, @Argument(value = "range", suggestions = "rangeSuggestions") String range, @Greedy @Argument("msg") String msg) throws SQLException {
        if (msg == null || msg.isBlank() || msg.isEmpty()){

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

    @CommandMethod("pausechat <radius>")
    public void PauseNearbyChat(CommandSender sender, @Argument("radius") int radius){
        plugin.rangedChatService.PauseChatsNearby((Player) sender, radius);
    }

    @CommandMethod("unpausechat")
    public void UnpauseChats(CommandSender sender){
        plugin.rangedChatService.UnpauseChats((Player) sender);
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

    @CommandPermission("fastrpchat.chatspy")
    @CommandMethod("chatspy")
    public void ToggleChatspy(CommandSender sender) {
        var userId = ((Player)sender).getUniqueId();
        if (plugin.chatspyUsers.contains(userId)){
            plugin.chatspyUsers.remove(userId);
            sender.sendMessage("Disabled Chatspy");
        }
        else{
            plugin.chatspyUsers.add(userId);
            sender.sendMessage("Enabled Chatspy");
        }
    }

    @CommandPermission("fastrpchat.staffchat")
    @ProxiedBy("st")
    @CommandMethod("staff [msg]")
    public void ChatStaff(CommandSender sender, @Greedy @Argument(value = "msg") String msg) throws SQLException {
        ChangeChannelOrSend(sender, "staff", msg);
    }

    @Suggestions("rangeSuggestions")
    public List<String> rangeSuggestions(CommandContext ctx, String input){
        return Arrays.stream(knownRanges).toList();
    }

    @CommandMethod("muterange <range>")
    public void MuteRange(CommandSender sender, @Argument(value = "range", suggestions = "rangeSuggestions")String range) throws Exception {
        if (!Arrays.stream(knownRanges).toList().contains(range.toLowerCase())){
            sender.sendMessage(String.format(ChatColor.DARK_RED + "Unknown range %s", range));
        }
        else{
            var x = plugin.rangedChatService.GetRangeRadius(range);
            if (plugin.UserRangeMutes.containsKey(((Player)sender).getUniqueId()) && plugin.UserRangeMutes.get(((Player)sender).getUniqueId()).contains(x)){
                plugin.UserRangeMutes.remove(((Player)sender).getUniqueId(), x);
                sender.sendMessage(String.format("Unmuted range %s", range));
                return;
            }
            else{
                plugin.UserRangeMutes.put(((Player)sender).getUniqueId(), x);
                sender.sendMessage(String.format("Muted range %s", range));
            }
        }
    }
}
