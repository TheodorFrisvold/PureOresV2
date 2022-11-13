package me.favn.pureores;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.favn.pureores.commands.GivePure;
import me.favn.pureores.config.OresConfig;
import me.favn.pureores.events.BlockBreakHandler;

public final class Pureores extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("§2Plugin enabled KEKW");

        // Initialize ores config
        // reset=true, so the config will be reloaded when the plugin is reloaded
        OresConfig.getConfig(this, true);

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

    public void givePure(Player player, ItemStack item, boolean natural) {
        if (player == null || item == null) {
            return;
        }
        String itemName = item.getItemMeta().getDisplayName();
        String message = natural
                ? String.format("Found a %1$s!", itemName)
                : String.format("Added %1$d %2$s to your inventory!", item.getAmount(), itemName);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(message);
            player.sendMessage(
                    String.format("The %1$s dropped at your feet because your inventory is full.", itemName));
        } else {
            player.getInventory().addItem(item);
            player.sendMessage(message);
        }
    }

    public void givePure(Player player, ItemStack item) {
        givePure(player, item, true);
    }
}

// Make API (Consider if needed, removed "TODO" for now)
