package me.favn.pureores;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.favn.pureores.commands.GivePure;
import me.favn.pureores.config.OresConfig;
import me.favn.pureores.events.BlockBreakHandler;

public final class Pureores extends JavaPlugin {
    private OresConfig oresConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("§2Plugin enabled KEKW");

        // Initialize ores config
        this.oresConfig = new OresConfig(this);

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

    /**
     * A utility method for giving pure ore drops to players.
     * @param player The player to give the pure item to.
     * @param item The item stack to give the player.
     * @param natural Whether the item was dropped from ore, instead of given with a command.
     */
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

    /**
     * A utility method for giving pure ore drops to players.
     * This method assumes the pure item was dropped from ore, instead of given with a command.
     * @param player The player to give the pure item to.
     * @param item The item stack to give the player.
     */
    public void givePure(Player player, ItemStack item) {
        givePure(player, item, true);
    }
}

// Make API (Consider if needed, removed "TODO" for now)
