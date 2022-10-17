package me.favn.pureores;

import me.favn.pureores.commands.GivePure;
import me.favn.pureores.events.onBlockBreak;
import me.favn.pureores.initials.BlockListGen;
import me.favn.pureores.initials.Configuration;
import me.favn.pureores.initials.ItemGenerator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

// error code PIGEON = issue in givepure class
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


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getServer().getConsoleSender().sendMessage("§2Plugin enabled KEKW");
        Configuration config = new Configuration(this, "config.yml");
        config.saveDefaultConfig();
        
        // Initialize givepure command
        new GivePure(this);

        new ItemGenerator(plugin);
        new BlockListGen(this);
        getServer().getPluginManager().registerEvents(new onBlockBreak(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage("§2Plugin disabled KEKW");
    }
}

// TODO: Check if block broken is naturally generated - USE COREPROTECT FOR THIS
// Resource world: Disallow placing oreblocks
// Settlement world: No plugin or use 0% droprate
// TODO: Add alternative text to chat messages & any static text whatsoever.
// TODO: Add checks if syntax in config is fucked so the plugin doesn't FUCKING CRASH
// TODO: Move from Int to Double to allow for decimals
// TODO: Make API