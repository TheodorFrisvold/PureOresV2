package me.favn.pureores;

import me.favn.pureores.commands.GivePure;
import me.favn.pureores.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public final class Pureores extends JavaPlugin {
    public static Plugin plugin;
    public static List<Material> blocks = new ArrayList<>();
    public static Map<Material, String> blockidentifier = new HashMap<>();
    public static Map<String, ItemStack> items = new HashMap<>();
    public static Map<Material, Integer> dropchances = new HashMap<>();
    public static List<String> names = new ArrayList<>();
    public static Map<String, String> NameAndItemname = new HashMap<>();
    public static boolean useglobalchance = false;

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void setPlugin(Plugin plugin) {
        Pureores.plugin = plugin;
    }

    public ConfigManager config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getServer().getConsoleSender().sendMessage("§2Plugin enabled KEKW");

        this.config = new ConfigManager(this);

        // Initialize givepure command
        new GivePure(this);

        // TODO: Update these to use new config, then uncomment
        // new ItemGenerator(this);
        // new BlockListGen(this);

        // getServer().getPluginManager().registerEvents(new onBlockBreak(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage("§2Plugin disabled KEKW");
    }
}

// Make API (Consider if needed, removed "TODO" for now)
