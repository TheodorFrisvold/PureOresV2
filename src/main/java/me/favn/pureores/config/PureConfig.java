package me.favn.pureores.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.favn.pureores.Pureores;
import net.md_5.bungee.api.ChatColor;

public class PureConfig {
    private static final String CONFIG_FILE_NAME = "pures.yml";

    private final Pureores plugin;
    private FileConfiguration config;

    private Map<String, PureOre> pureItems;
    private List<DropsConfig<Material>> allBlockDrops;
    private Map<Material, List<DropsConfig<Material>>> blockDrops;

    public PureConfig(Pureores plugin) {
        this.plugin = plugin;

        reload();

        blockDrops = new HashMap<>();
    }

    public void reload() {
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(CONFIG_FILE_NAME, false);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);

        pureItems = loadPureItems();
        allBlockDrops = loadBlockDrops();
        loadMobDrops();
        loadChestLoot();
    }

    private Map<String, PureOre> loadPureItems() {
        Map<String, PureOre> pures = new HashMap<>();

        if (!this.config.contains("pure_items")) {
            warn("Missing pure_items section");
            return pures;
        }

        ConfigurationSection puresSection = this.config.getConfigurationSection("pure_items");
        Set<String> keys = puresSection.getKeys(false);

        for (String key : keys) {
            ConfigurationSection section = puresSection.getConfigurationSection(key);

            String name = section.getString("name", "");
            if (name.isEmpty()) {
                warn("Missing display name for " + key);
                continue;
            }
            String formatting = section.getString("formatting", "");
            String description = section.getString("description", "");
            Material item = Material.getMaterial(section.getString("item", ""));
            if (item == null) {
                warn("Missing/invalid item material for " + key);
                continue;
            }

            PureOre pure = new PureOre(name, description, formatting, item);
            pures.put(key, pure);
        }

        return pures;
    }

    private List<DropsConfig<Material>> loadBlockDrops() {
        List<DropsConfig<Material>> drops = new ArrayList<>();

        if (!this.config.contains("block_drops")) {
            warn("Missing block_drops section");
            return drops;
        }

        for (Map<?, ?> drop : this.config.getMapList("block_drops")) {
            String pureItemKey = (String) drop.get("pure_item");
            PureOre pureItem = this.pureItems.getOrDefault(pureItemKey, null);
            if (pureItem == null) {
                warn("Missing/invalid pure item for block drop");
                continue;
            }
            Integer dropAmount = (Integer) drop.get("drop_amount");
            if (dropAmount == null) {
                warn("Missing drop amount for block drop");
                continue;
            }
            Double dropChance = (Double) drop.get("drop_chance");
            if (dropChance == null) {
                warn("Missing drop chance for block drop");
                continue;
            }
            List<String> dropFromMaterials = (List<String>) drop.get("drop_from");
            if (dropFromMaterials == null || dropFromMaterials.size() == 0) {
                warn("Missing blocks for block drop");
                continue;
            }
            List<Material> dropFrom = dropFromMaterials.stream().map(name -> Material.getMaterial(name)).collect(Collectors.toList());
            if (!dropFrom.stream().allMatch(material -> material != null && material.isBlock())) {
                String invalid = dropFromMaterials.stream().map(name -> Material.getMaterial(name)).filter(m -> m == null || !m.isBlock()).map(m -> m.toString()).collect(Collectors.joining(","));
                warn("Invalid block material(s) for block drop: " + invalid);
                continue;
            }

            DropsConfig<Material> dropsConfig = new DropsConfig<>(pureItem, dropChance, dropAmount, dropFrom);

            drops.add(dropsConfig);

            dropsConfig.getDropFrom().forEach(material -> {
                List<DropsConfig<Material>> dropsForBlock = blockDrops.get(material);
                if (dropsForBlock == null) {
                    dropsForBlock = new ArrayList<>();
                    blockDrops.put(material, dropsForBlock);
                }
                dropsForBlock.add(dropsConfig);
            });
        }

        return drops;
    }

    private void loadMobDrops() {}

    private void loadChestLoot() {
        // lt.getKey().getKey().startsWith("chest/");
    }

    private void warn(String message) {
        this.plugin.getLogger().warning("CONFIG ERROR: " + message);
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

    public DropsConfig(PureOre pureItem, double dropChance, int dropAmount, List<T> dropFrom) {
        this.pureItem = pureItem;
        this.dropChance = Math.max(Math.min(dropChance, 1), 0);
        this.dropAmount = Math.max(Math.min(dropAmount, pureItem.getItem().getMaxStackSize()), 0);
        this.dropAmount = dropAmount;
        this.dropFrom = new ArrayList<>(dropFrom);
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
