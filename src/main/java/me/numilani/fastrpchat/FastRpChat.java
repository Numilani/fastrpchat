package me.numilani.fastrpchat;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import me.numilani.fastrpchat.commands.ChatCommandHandler;
import me.numilani.fastrpchat.commands.NameUtilityCommandHandler;
import me.numilani.fastrpchat.data.IDataSourceConnector;
import me.numilani.fastrpchat.data.SqliteDataSourceConnector;
import me.numilani.fastrpchat.listeners.FastRpChatListeners;
import me.numilani.fastrpchat.services.RangedChatService;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FastRpChat extends JavaPlugin {

    public CloudSimpleHandler cmdHandler = new CloudSimpleHandler();
    public FileConfiguration cfg;
    public IDataSourceConnector dataSource;
    public RangedChatService rangedChatService = new RangedChatService(this);

    public List<UUID> chatspyUsers = new ArrayList<>();

    @Override
    public void onEnable() {
        // First run setup
        var isFirstRun = false;
        if (!(new FileConfiguration(this, "config.yml").exists())) {
            isFirstRun = true;
            doPluginInit();
        }

        cfg = new FileConfiguration(this, "config.yml");
        cfg.load();

        // do a check for datasourcetype once that's added to config
        // for now, just set datasource to sqlite always
        try {
            dataSource = new SqliteDataSourceConnector(this);
            if (isFirstRun) dataSource.initDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Register events
        getServer().getPluginManager().registerEvents(new FastRpChatListeners(this), this);

        // Register commands
        cmdHandler.enable(this);
        cmdHandler.getParser().parse(new ChatCommandHandler(this));
        cmdHandler.getParser().parse(new NameUtilityCommandHandler(this));
//        var testCmd = cmdHandler.getManager().commands().stream()
//                .filter(x -> x.getArguments().stream().findFirst().get().getName().equals("test"))
//                .findFirst().get();
//        cmdHandler.getManager().command(cmdHandler.getManager().commandBuilder("asdf", testCmd.getCommandMeta()).proxies(testCmd).build());

//        var chatRangeCmd = cmdHandler.getManager().commands().stream()
//                .filter(x -> x.getArguments().stream().findFirst().get().getName().equals("cr"))
//                .findFirst().get();

//        String[] ranges = {"global", "yell", "local", "quiet", "whisper"};
//        String[] aliases = {"g", "y", "l", "q", "w"};
//        for (var x : aliases){
//            cmdHandler.getManager().commandBuilder(x, chatRangeCmd.getCommandMeta()).proxies(chatRangeCmd).argument(StringArgument.of(x));
//        }
    }

    private void doPluginInit() {
        var cfgFile = new FileConfiguration(this, "config.yml");
        cfgFile.addHeader("This config file is unused. It will be populated in the future.");
        cfgFile.set("unusedValue", "");

        cfgFile.saveSync();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
