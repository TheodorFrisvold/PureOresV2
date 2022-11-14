package me.favn.pureores.events;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.favn.pureores.Pureores;
import me.favn.pureores.config.OresConfig.Ore;

public class BlockBreakHandler implements Listener {
    private final Pureores plugin;

    public BlockBreakHandler(Pureores plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Material block = e.getBlock().getType();
        Player player = e.getPlayer();
        boolean silkTouch = player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH);
        Ore ore = this.plugin.getOresConfig().getOre(block);

        if (!silkTouch && ore != null) {
            Random random = new Random();
            double chance = ore.getChance();
            if (random.nextDouble() <= chance) {
                ItemStack item = ore.toItemStack();
                e.setDropItems(false);
                this.plugin.givePure(player, item, true);
            }
        }
    }
}
