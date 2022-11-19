package me.favn.pureores;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.favn.pureores.commands.GivePure;
import me.favn.pureores.config.OresConfig;
import me.favn.pureores.config.TextConfig;
import me.favn.pureores.config.TextConfig.Placeholders;
import me.favn.pureores.events.BlockBreakHandler;

public final class Pureores extends JavaPlugin {
    private OresConfig oresConfig;
    private TextConfig textConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("§2Plugin enabled KEKW");

        // Initialize ores config
        this.oresConfig = new OresConfig(this);
        this.textConfig = new TextConfig(this);

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

    /**
     * A utility method for giving pure ore drops to players.
     * 
     * @param player  The player to give the pure item to.
     * @param item    The item stack to give the player.
     * @param natural Whether the item was dropped from ore, instead of given with a
     *                command.
     */
    public void givePure(Player player, ItemStack item, boolean natural) {
        if (player == null || item == null) {
            return;
        }
        String message = natural
                ? getTextConfig().getMessage("ore-found", new Placeholders(null, item, null))
                : getTextConfig().getMessage("ore-added", new Placeholders(null, item, item.getAmount()));
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(message);
            player.sendMessage(getTextConfig().getMessage("ore-dropped", new Placeholders(null, item, null)));
        } else {
            player.getInventory().addItem(item);
            player.sendMessage(message);
        }
    }

    /**
     * A utility method for giving pure ore drops to players.
     * This method assumes the pure item was dropped from ore, instead of given with
     * a command.
     * 
     * @param player The player to give the pure item to.
     * @param item   The item stack to give the player.
     */
    public void givePure(Player player, ItemStack item) {
        givePure(player, item, true);
    }
}

// Make API (Consider if needed, removed "TODO" for now)
