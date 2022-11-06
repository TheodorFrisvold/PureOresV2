package me.favn.pureores.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.favn.pureores.Pureores;
import me.favn.pureores.config.OresConfig.Ore;
import me.favn.pureores.events.PureoreEvent;

public final class PureGiver {
    private static Pureores plugin;

    public static void init(Pureores plugin) {
        if (PureGiver.plugin == null) {
            PureGiver.plugin = plugin;
        }
    }

    public static void givePure(Player player, ItemStack item, boolean natural) {
        if (player == null || item == null) {
            return;
        }
        Ore ore = plugin.getConfigManager().getOresConfig().getOreByItem(item.getType());
        String message = natural
            ? String.format("Found a %1$s!", ore.getName())
            : String.format("Added %1$d %2$s to your inventory!", item.getAmount(), ore.getName());
        PureoreEvent event = new PureoreEvent(ore, player, natural, item.getAmount());
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(message);
            player.sendMessage(String.format("The %1$s dropped at your feet because your inventory is full.", ore.getName()));
        } else {
            player.getInventory().addItem(item);
            player.sendMessage(message);
        }
    }
    public static void givePure(Player player, ItemStack item) {
        givePure(player, item, true);
    }

    private PureGiver() {}
}
