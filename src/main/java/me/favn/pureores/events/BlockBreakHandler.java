package me.favn.pureores.events;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.favn.pureores.PureDrops;
import me.favn.pureores.Pureores;

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
        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean silkTouch = tool.containsEnchantment(Enchantment.SILK_TOUCH);
        // Make sure the player can get drops from this block with their current tool
        // (Don't drop flawless diamonds when broken with a wood/stone pickaxe)
        boolean canDrop = e.getBlock().getDrops().isEmpty() || !e.getBlock().getDrops(tool).isEmpty();

        List<PureDrops<Material>> drops = this.plugin.getPureConfig().getBlockDrops(block);
        if (!silkTouch && canDrop) {
            for (PureDrops<Material> drop : drops) {
                ItemStack item = drop.rollForDrops();
                if (item != null) {
                    e.setDropItems(false);
                    this.plugin.getApi().givePure(player, item, true);
                }
            }
        }
    }
}
