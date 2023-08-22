package me.numilani.fastrpchat;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

public final class FastRpChat extends JavaPlugin {

    public CloudSimpleHandler cmdHandler = new CloudSimpleHandler();
    public FileConfiguration cfg;

    @Override
    public void onEnable() {
        // First run setup
        var isFirstRun = false;
        if (!(new FileConfiguration(this, "config.yml").exists())) {
            isFirstRun = true;
            doPluginInit();
        }
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
