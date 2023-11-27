package me.favn.pureores;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PureItem {
    private String name;
    private String description;
    private String formatting;
    private Material item;

    public PureItem(String name, String description, String formatting, Material item) {
        this.name = name;
        this.description = description;
        this.formatting = formatting;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description;
    }

    public String getFormatting() {
        if (formatting == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', formatting);
    }

    public Material getItem() {
        return item;
    }

    public String getDisplayName() {
        return ChatColor.RESET + getFormatting() + getName();
    }

    public ItemStack toItemStack(int amount) {
        if (amount <= 0) {
            return null;
        }

        ItemStack item = new ItemStack(getItem(), amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getDisplayName());
        if (!getDescription().isEmpty()) {
            meta.setLore(Arrays.asList(ChatColor.RESET + getDescription()));
        }
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack toItemStack() {
        return toItemStack(1);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
