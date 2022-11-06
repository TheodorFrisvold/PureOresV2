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
import me.favn.pureores.utils.ItemFactory;
import me.favn.pureores.utils.PureGiver;

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
        Ore ore = this.plugin
            .getConfigManager()
            .getOresConfig()
            .getOreByBlock(block);

        if (!silkTouch && ore != null) {
            boolean useGlobalChance = this.plugin
                .getConfigManager()
                .getOresConfig()
                .isGlobalDropChanceOverride();
            Random random = new Random();
            Double chance = ore.getChance();
            if (chance == null || useGlobalChance) {
                chance = this.plugin
                    .getConfigManager()
                    .getOresConfig()
                    .getGlobalDropChance();
            }
            if (random.nextDouble() <= chance) {
                ItemStack item = ItemFactory.getItem(ore);
                e.setDropItems(false);
                PureGiver.givePure(player, item, true);
            }
        }
    }
}
