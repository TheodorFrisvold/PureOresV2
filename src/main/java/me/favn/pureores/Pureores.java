package me.favn.pureores;

import org.bukkit.plugin.java.JavaPlugin;

import me.favn.pureores.commands.GivePure;
import me.favn.pureores.config.ConfigManager;
import me.favn.pureores.events.BlockBreakHandler;
import me.favn.pureores.utils.PureGiver;

public final class Pureores extends JavaPlugin {
    private ConfigManager configManager;

    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("§2Plugin enabled KEKW");

        // Initialize static PureGiver util
        PureGiver.init(this);

        // Initialize config manager
        this.configManager = new ConfigManager(this);

        // Initialize givepure command
        new GivePure(this);

        // Initialize block break event handler
        new BlockBreakHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage("§2Plugin disabled KEKW");
    }
}

// Make API (Consider if needed, removed "TODO" for now)
