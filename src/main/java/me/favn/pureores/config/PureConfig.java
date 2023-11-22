package me.favn.pureores.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTables;

import me.favn.pureores.PureDrops;
import me.favn.pureores.PureOre;
import me.favn.pureores.Pureores;

// TODO: improve error messages
public class PureConfig {
    private static final String CONFIG_FILE_NAME = "pures.yml";

    private final Pureores plugin;
    private FileConfiguration config;

    private Map<String, PureOre> pureItems;
    private List<PureDrops<Material>> blockDrops;
    private Map<Material, List<PureDrops<Material>>> blockDropsMap;
    private List<PureDrops<EntityType>> mobDrops;
    private Map<EntityType, List<PureDrops<EntityType>>> mobDropsMap;
    private List<PureDrops<LootTables>> chestLoot;
    private Map<LootTables, List<PureDrops<LootTables>>> chestLootMap;

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

    public Map<String, PureOre> getPureItems() {
        return Collections.unmodifiableMap(this.pureItems);
    }

    public List<PureDrops<Material>> getBlockDrops(Material block) {
        return Collections.unmodifiableList(this.blockDropsMap.get(block));
    }

    public List<PureDrops<Material>> getBlockDrops() {
        return Collections.unmodifiableList(this.blockDrops);
    }

    public List<PureDrops<EntityType>> getMobDrops(EntityType mob) {
        return Collections.unmodifiableList(this.mobDropsMap.get(mob));
    }

    public List<PureDrops<EntityType>> getMobDrops() {
        return Collections.unmodifiableList(this.mobDrops);
    }

    public List<PureDrops<LootTables>> getChestLoot(LootTables structure) {
        return Collections.unmodifiableList(this.chestLootMap.get(structure));
    }

    public List<PureDrops<LootTables>> getChestLoot() {
        return Collections.unmodifiableList(this.chestLoot);
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

            PureDrops<Material> dropsConfig = new PureDrops<>(pureItem, dropChance, dropAmount, dropFrom);

            blockDrops.add(dropsConfig);

            dropsConfig.getDropFrom().forEach(material -> {
                List<PureDrops<Material>> dropsForBlock = blockDropsMap.get(material);
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

            PureDrops<EntityType> dropsConfig = new PureDrops<>(pureItem, dropChance, dropAmount, dropFrom);

            mobDrops.add(dropsConfig);

            dropsConfig.getDropFrom().forEach(mob -> {
                List<PureDrops<EntityType>> dropsForMob = mobDropsMap.get(mob);
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

            PureDrops<LootTables> dropsConfig = new PureDrops<>(pureItem, dropChance, dropAmount, dropFrom);

            chestLoot.add(dropsConfig);

            dropsConfig.getDropFrom().forEach(loot -> {
                List<PureDrops<LootTables>> dropsForStructure = chestLootMap.get(loot);
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
