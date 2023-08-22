package me.numilani.fastrpchat;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import me.numilani.fastrpchat.data.IDataSourceConnector;
import me.numilani.fastrpchat.data.SqliteDataSourceConnector;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

import java.sql.SQLException;

public final class FastRpChat extends JavaPlugin {

    public CloudSimpleHandler cmdHandler = new CloudSimpleHandler();
    public FileConfiguration cfg;
    public IDataSourceConnector dataSource;

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

        // todo: do a check for datasourcetype once that's added to config
        // for now, just set datasource to sqlite always
        try {
            dataSource = new SqliteDataSourceConnector(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        cmdHandler.enable(this);
//        cmdHandler.getParser().parse()
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
