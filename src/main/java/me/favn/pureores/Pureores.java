package me.favn.pureores;

import org.bukkit.plugin.java.JavaPlugin;

import me.favn.pureores.commands.GivePure;
import me.favn.pureores.config.OresConfig;
import me.favn.pureores.config.PureConfig;
import me.favn.pureores.config.TextConfig;
import me.favn.pureores.events.BlockBreakHandler;

public final class Pureores extends JavaPlugin {
    private OresConfig oresConfig;
    private TextConfig textConfig;
    private PureConfig pureConfig;
    private PureApi api;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("§2Plugin enabled KEKW");

        // Initialize ores config
        this.oresConfig = new OresConfig(this);
        this.textConfig = new TextConfig(this);
        this.pureConfig = new PureConfig(this);

        // Initialize api
        this.api = new PureApi(this);

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

    public OresConfig getOresConfig() {
        return this.oresConfig;
    }

    public TextConfig getTextConfig() {
        return this.textConfig;
    }

    public PureApi getApi() {
        return this.api;
    }
}
