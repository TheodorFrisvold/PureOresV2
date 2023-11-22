package me.favn.pureores.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;

import me.favn.pureores.Pureores;
import net.md_5.bungee.api.ChatColor;

// TODO: improve error messages
public class PureConfig {
    private static final String CONFIG_FILE_NAME = "pures.yml";

    private final Pureores plugin;
    private FileConfiguration config;

    private Map<String, PureOre> pureItems;
    private List<DropsConfig<Material>> blockDrops;
    private Map<Material, List<DropsConfig<Material>>> blockDropsMap;
    private List<DropsConfig<EntityType>> mobDrops;
    private Map<EntityType, List<DropsConfig<EntityType>>> mobDropsMap;
    private List<DropsConfig<LootTables>> chestLoot;
    private Map<LootTables, List<DropsConfig<LootTables>>> chestLootMap;

    public PureConfig(Pureores plugin) {
        this.plugin = plugin;

        pureItems = new HashMap<>();
        blockDrops = new ArrayList<>();
        blockDropsMap = new HashMap<>();
        mobDrops = new ArrayList<>();
        mobDropsMap = new HashMap<>();
        chestLoot = new ArrayList<>();
        chestLootMap = new HashMap<>();

        reload();
    }

    public void reload() {
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(CONFIG_FILE_NAME, false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        pureItems.clear();
        blockDrops.clear();
        blockDropsMap.clear();
        mobDrops.clear();
        mobDropsMap.clear();
        chestLoot.clear();
        chestLootMap.clear();

        loadPureItems();
        loadBlockDrops();
        loadMobDrops();
        loadChestLoot();
    }

    public PureOre getPureItem(String key) {
        return this.pureItems.get(key);
    }

    public PureOre getPureItem(Material base) {
        return this.pureItems.values().stream().filter(pure -> pure.getItem() == base).findFirst().orElse(null);
    }

    public List<DropsConfig<Material>> getBlockDrops(Material block) {
        return Collections.unmodifiableList(this.blockDropsMap.get(block));
    }

    public List<DropsConfig<EntityType>> getMobDrops(EntityType mob) {
        return Collections.unmodifiableList(this.mobDropsMap.get(mob));
    }

    public List<DropsConfig<LootTables>> getChestLoot(LootTables structure) {
        return Collections.unmodifiableList(this.chestLootMap.get(structure));
    }

    private void loadPureItems() {
        if (!config.contains("pure_items")) {
            warn("Missing pure_items section");
            return;
        }

        ConfigurationSection puresSection = config.getConfigurationSection("pure_items");
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
            pureItems.put(key, pure);
        }
    }

    private void loadBlockDrops() {
        if (!config.contains("block_drops")) {
            warn("Missing block_drops section");
            return;
        }

        for (Map<?, ?> drop : config.getMapList("block_drops")) {
            String pureItemKey = (String) drop.get("pure_item");
            PureOre pureItem = pureItems.getOrDefault(pureItemKey, null);
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
                String invalid = dropFromMaterials
                    .stream()
                    .filter(name -> {
                        Material m = Material.getMaterial(name);
                        return m == null || !m.isBlock();
                    })
                    .collect(Collectors.joining(","));
                warn("Invalid block material(s) for block drop: " + invalid);
                continue;
            }

            DropsConfig<Material> dropsConfig = new DropsConfig<>(pureItem, dropChance, dropAmount, dropFrom);

            blockDrops.add(dropsConfig);

            dropsConfig.getDropFrom().forEach(material -> {
                List<DropsConfig<Material>> dropsForBlock = blockDropsMap.get(material);
                if (dropsForBlock == null) {
                    dropsForBlock = new ArrayList<>();
                    blockDropsMap.put(material, dropsForBlock);
                }
                dropsForBlock.add(dropsConfig);
            });
        }
    }

    private void loadMobDrops() {
        if (!config.contains("mob_drops")) {
            warn("Missing mob_drops section");
            return;
        }

        for (Map<?, ?> drop : config.getMapList("mob_drops")) {
            String pureItemKey = (String) drop.get("pure_item");
            PureOre pureItem = pureItems.getOrDefault(pureItemKey, null);
            if (pureItem == null) {
                warn("Missing/invalid pure item for mob drop");
                continue;
            }
            Integer dropAmount = (Integer) drop.get("drop_amount");
            if (dropAmount == null) {
                warn("Missing drop amount for mob drop");
                continue;
            }
            Double dropChance = (Double) drop.get("drop_chance");
            if (dropChance == null) {
                warn("Missing drop chance for mob drop");
                continue;
            }
            List<String> dropFromMobs = (List<String>) drop.get("drop_from");
            if (dropFromMobs == null || dropFromMobs.size() == 0) {
                warn("Missing mobs for mob drop");
                continue;
            }
            List<EntityType> dropFrom = dropFromMobs.stream().map(name -> {
                try {
                    return Enum.valueOf(EntityType.class, name);
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toList());
            if (!dropFrom.stream().allMatch(mob -> mob != null && mob.isAlive())) {
                String invalid = dropFromMobs
                    .stream()
                    .filter(name -> {
                        try {
                            EntityType m = Enum.valueOf(EntityType.class, name);
                            return !m.isAlive();
                        } catch (Exception e) {
                            return true;
                        }
                    })
                    .collect(Collectors.joining(","));
                warn("Invalid entity type(s) for mob drop: " + invalid);
                continue;
            }

            DropsConfig<EntityType> dropsConfig = new DropsConfig<>(pureItem, dropChance, dropAmount, dropFrom);

            mobDrops.add(dropsConfig);

            dropsConfig.getDropFrom().forEach(mob -> {
                List<DropsConfig<EntityType>> dropsForMob = mobDropsMap.get(mob);
                if (dropsForMob == null) {
                    dropsForMob = new ArrayList<>();
                    mobDropsMap.put(mob, dropsForMob);
                }
                dropsForMob.add(dropsConfig);
            });
        }
    }

    private void loadChestLoot() {
        if (!config.contains("chest_loot")) {
            warn("Missing chest_loot section");
            return;
        }

        for (Map<?, ?> drop : config.getMapList("chest_loot")) {
            String pureItemKey = (String) drop.get("pure_item");
            PureOre pureItem = pureItems.getOrDefault(pureItemKey, null);
            if (pureItem == null) {
                warn("Missing/invalid pure item for chest loot");
                continue;
            }
            Integer dropAmount = (Integer) drop.get("drop_amount");
            if (dropAmount == null) {
                warn("Missing drop amount for chest loot");
                continue;
            }
            Double dropChance = (Double) drop.get("drop_chance");
            if (dropChance == null) {
                warn("Missing drop chance for chest loot");
                continue;
            }
            List<String> dropInStructures = (List<String>) drop.get("drop_from");
            if (dropInStructures == null || dropInStructures.size() == 0) {
                warn("Missing structures for chest loot");
                continue;
            }
            List<LootTables> dropFrom = dropInStructures.stream().map(name -> {
                try {
                    return Enum.valueOf(LootTables.class, name);
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toList());
            if (!dropFrom.stream().allMatch(loot -> loot != null && loot.getKey().getKey().startsWith("chest/"))) {
                String invalid = dropInStructures
                    .stream()
                    .filter(name -> {
                        try {
                            LootTables l = Enum.valueOf(LootTables.class, name);
                            return !l.getKey().getKey().startsWith("chest/");
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.joining(","));
                warn("Invalid structure type(s) for chest loot: " + invalid);
                continue;
            }

            DropsConfig<LootTables> dropsConfig = new DropsConfig<>(pureItem, dropChance, dropAmount, dropFrom);

            chestLoot.add(dropsConfig);

            dropsConfig.getDropFrom().forEach(loot -> {
                List<DropsConfig<LootTables>> dropsForStructure = chestLootMap.get(loot);
                if (dropsForStructure == null) {
                    dropsForStructure = new ArrayList<>();
                    chestLootMap.put(loot, dropsForStructure);
                }
                dropsForStructure.add(dropsConfig);
            });
        }
    }

    private void warn(String message) {
        plugin.getLogger().warning("CONFIG ERROR: " + message);
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
        return dropFrom.contains(thing);
    }

    public boolean dropsFrom(String thing) {
        for (T t : dropFrom) {
            if (thing.equals(t.name())) {
                return true;
            }
        }
        return false;
    }

    public boolean roll() {
        Random random = new Random();
        double rolled = random.nextDouble();
        return rolled < getDropChance();
    }

    public ItemStack rollForDrops() {
        if (roll()) {
            return getPureItem().toItemStack(getDropAmount());
        }
        return null;
    }
}
