package me.favn.pureores;

import me.favn.pureores.commands.givepure;
import me.favn.pureores.events.onBlockBreak;
import me.favn.pureores.initials.BlockListGen;
import me.favn.pureores.initials.Configuration;
import me.favn.pureores.initials.ItemGenerator;
import me.favn.pureores.sql.MySQL;
import me.favn.pureores.sql.SQLGetter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.*;

// error code PIGEON = issue in givepure class
public final class Pureores extends JavaPlugin implements Listener {

    public static SQLGetter data;
    public MySQL SQL;
//    public SQLGetter data;
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
        this.SQL = new MySQL();
        this.data = new SQLGetter(this);
        try {
            SQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            //e.printStackTrace();
            getServer().getConsoleSender().sendMessage("§4Database is not connected!");
        }
        if (SQL.isConnected()) {
            getServer().getConsoleSender().sendMessage("§2Database is connected!");
            data.createTable();
        }
        getServer().getConsoleSender().sendMessage("§2Plugin enabled KEKW");
        Configuration config = new Configuration(this, "config.yml");
        config.saveDefaultConfig();
        getCommand("givepure").setExecutor(new givepure());
        new ItemGenerator(plugin);
        new BlockListGen(this);
        getServer().getPluginManager().registerEvents(new onBlockBreak(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage("§2Plugin disabled KEKW");
        SQL.disconnect();
    }
}

// TODO: Check if block broken is naturally generated
// TODO: Add alternative text to chat messages & any static text whatsoever.
// Resource world: Disallow placing oreblocks
// Settlement world: No plugin or use 0% droprate
// TODO: Save placed ore-blocks in arrays, separated into chunks for more lightweight searches.
// TODO: Add checks if syntax in config is fucked so the plugin doesn't FUCKING CRASH
// TODO: Move from Int to Double to allow for decimals
// TODO: Make API
// TODO: in givepure, pass amount value as well when sending invalid syntax, if amount value is null run original invalidsyntax, else run new invalidsyntax part adding if number is 0 or negative
// TODO: in givepure, if player sends name as amount value, send invalidsyntax with message regarding not using text for amount
// TODO: Properly implement database for placed oreblocks, check comment: onBlockBreak.java:55