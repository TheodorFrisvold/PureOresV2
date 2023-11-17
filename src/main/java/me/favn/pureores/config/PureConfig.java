package me.favn.pureores.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;

import me.favn.pureores.Pureores;
import net.md_5.bungee.api.ChatColor;

public class PureConfig {
    private static final String CONFIG_FILE_NAME = "pures.yml";

    private final Pureores plugin;
    private FileConfiguration config;

    public PureConfig(Pureores plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(CONFIG_FILE_NAME, false);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }
}

class PureOre {
    private String name;
    private String description;
    private String formatting;
    private Material item;

    public PureOre(String name, String description, String formatting, Material item) {
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

class DropsConfig<T extends Enum<?>> {
    private PureOre pureItem;
    private double dropChance;
    private int dropAmount;
    private List<T> dropFrom;

    public DropsConfig(PureOre pureItem, double dropChance, int dropAmount, List<String> dropFrom) throws Exception {
        this.pureItem = pureItem;
        this.dropChance = Math.max(Math.min(dropChance, 1), 0);
        this.dropAmount = Math.max(Math.min(dropAmount, pureItem.getItem().getMaxStackSize()), 0);
        this.dropAmount = dropAmount;
        this.dropFrom = new ArrayList<>();

        try {
            Class<?> thisClass = this.getClass();
            Field dropFromField;
            dropFromField = thisClass.getDeclaredField("dropFrom");
            ParameterizedType dropFromListType = (ParameterizedType) dropFromField.getGenericType();
            Class<?> dropFromType = (Class<?>) dropFromListType.getActualTypeArguments()[0];

            for (String name : dropFrom) {
                if (dropFromType == EntityType.class) {
                    try {
                        EntityType et = Enum.valueOf(EntityType.class, name);
                        this.dropFrom.add((T) et);
                    } catch (IllegalArgumentException e) {
                        throw new Exception(String.format("Invalid EntityType %s", name));
                    }
                } else if (dropFromType == Material.class) {
                    try {
                        Material mat = Enum.valueOf(Material.class, name);
                        this.dropFrom.add((T) mat);
                    } catch (IllegalArgumentException e) {
                        throw new Exception(String.format("Invalid Material %s", name));
                    }
                } else if (dropFromType == LootTables.class) {
                    try {
                        LootTables lt = Enum.valueOf(LootTables.class, name);
                        this.dropFrom.add((T) lt);
                    } catch (IllegalArgumentException e) {
                        throw new Exception(String.format("Invalid LootTables %s", name));
                    }
                } else {
                    throw new Exception("dropFrom must be a list of either EntityType, Material, or LootTables names");
                }
            }
        } catch (NoSuchFieldException e) {
            // We know `dropField` exists in this class, so we should never end up here.
        }
    }

    public PureOre getPureItem() {
        return pureItem;
    }

    public double getDropChance() {
        return dropChance;
    }

    public int getDropAmount() {
        return dropAmount;
    }

    public List<T> getDropFrom() {
        return dropFrom;
    }

    public boolean dropsFrom(T thing) {
        return this.dropFrom.contains(thing);
    }

    public boolean dropsFrom(String thing) {
        for (T t : dropFrom) {
            if (thing.equals(t.name())) {
                return true;
            }
        }
        return false;
    }

    public ItemStack roll() {
        Random random = new Random();
        double rolled = random.nextDouble();
        if (rolled < getDropChance()) {
            return getPureItem().toItemStack(getDropAmount());
        }
        return null;
    }
}
