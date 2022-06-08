package me.favn.pureores.events;

import me.favn.pureores.Pureores;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class onBlockBreak implements Listener {

    @EventHandler
    public static void onMineBlock(BlockBreakEvent e) {
        Material b = e.getBlock().getType();
        boolean usingSilktouch = e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH);

        if (Pureores.blocks.contains(b) && !usingSilktouch) {
            onBreak(e);
        }

    }

    public static void onBreak(BlockBreakEvent e) {
        if (Pureores.blocks.contains(e.getBlock().getType())) {

            Material b = e.getBlock().getType();
            Player p = e.getPlayer();
            Random random = new Random();
            ItemStack item = Pureores.items.get(Pureores.blockidentifier.get(b));
            String name = Pureores.blockidentifier.get(b);

            int chance;

            if (Pureores.useglobalchance) {
                chance = (Pureores.getPlugin().getConfig().getConfigurationSection("Globals.DropChance").getInt("Chance"));
            } else {
                chance = Pureores.dropchances.get(b);
            }

            if (random.nextInt(100) <= chance) {
                if (p.getInventory().firstEmpty() == -1) {
                    e.setDropItems(false);
                    p.getWorld().dropItem(p.getLocation(), item);
                    p.sendMessage("Found a rare " + name + "!");
                    p.sendMessage("The " + name + " dropped at your feet because your inventory was full.");
                } else {
                    p.getInventory().addItem(item);
                    e.setDropItems(false);
                    p.sendMessage("Found a rare " + name + "!");
                }


            }
        }
    }
}