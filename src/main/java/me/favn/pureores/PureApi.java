package me.favn.pureores;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.favn.pureores.config.TextConfig.Placeholders;

public class PureApi {
    private final Pureores plugin;

    PureApi(Pureores plugin) {
        this.plugin = plugin;
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
                ? this.plugin.getTextConfig().getMessage("ore-found", new Placeholders(null, item, null))
                : this.plugin.getTextConfig().getMessage("ore-added", new Placeholders(null, item, item.getAmount()));
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(message);
            player.sendMessage(
                    this.plugin.getTextConfig().getMessage("ore-dropped", new Placeholders(null, item, null)));
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

    /**
     * A method for getting the pure version of a base material, if it exists.
     * If there is no pure version of the given material, this method returns
     * {@code null}.
     *
     * @param baseMaterial The base material to get the pure version of.
     * @return A {@link PureItem} that is the pure version of the given material, or
     *         {@code null}.
     */
    public PureItem getPure(Material baseMaterial) {
        return this.plugin.getPureConfig().getPureItem(baseMaterial);
    }
}
