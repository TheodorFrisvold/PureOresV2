package me.favn.pureores.initials;

import me.favn.pureores.Pureores;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemCreator {

    public ItemCreator(String itemsection, Plugin plugin) {

        String checkthis = "Ores." + itemsection;
        Map<String, Object> oresSubSection = Objects.requireNonNull(plugin.getConfig().getConfigurationSection(checkthis)).getValues(false);
        createItem(oresSubSection);

    }

    private void createItem(Map<String, Object> SubSection) {

        String itemname = (String) SubSection.get("Name");
        Material material = Material.valueOf(SubSection.get("Material_Item").toString());
        String description = (String) SubSection.get("Description");
        String formatting = (String) SubSection.get("Formatting");

        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(ChatColor.RESET + formatting + itemname);
        List<String> lore = new ArrayList<>();
        lore.add("§r" + description);
        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        Pureores.items.put(itemname, item);
    }
}