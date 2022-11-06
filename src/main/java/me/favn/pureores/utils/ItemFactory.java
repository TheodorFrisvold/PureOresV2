package me.favn.pureores.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.favn.pureores.config.OresConfig.Ore;

public final class ItemFactory {
    public static ItemStack getItem(Ore ore) {
        return getItem(ore, 1);
    }
    public static ItemStack getItem(Ore ore, int amount) {
        ItemStack item = new ItemStack(ore.getItem(), amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + ore.getFormatting() + ore.getName());
        String description = ore.getDescription();
        if (description != null) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RESET + description);
            meta.setLore(lore);
        }
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    private ItemFactory() {}
}
