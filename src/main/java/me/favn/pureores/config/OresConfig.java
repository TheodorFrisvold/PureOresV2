package me.favn.pureores.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.Serializer;
import me.favn.pureores.Pureores;
import de.exlll.configlib.SerializeWith;

@Configuration
public final class OresConfig {
    static final Double DEFAULT_DROP_CHANCE = 0.5;

    static final class BlockMaterialSerializer implements Serializer<Set<Material>, String> {
        @Override
        public Set<Material> deserialize(String str) {
            Set<Material> result = new HashSet<>();
            for (String oreName : str.split(",")) {
                oreName = oreName.trim();
                Material ore = Material.getMaterial(oreName.toUpperCase());
                if (ore == null) {
                    Pureores.getPlugin().getLogger().warning("Error in ores config: Invalid material name " + oreName);
                } else if (!ore.isBlock()) {
                    Pureores.getPlugin().getLogger().warning("Error in ores config: Material is not a block " + oreName);
                } else {
                    result.add(ore);
                }
            }
            return result;
        }

        @Override
        public String serialize(Set<Material> ores) {
            // Serialize materials as a comma-separated list
            return ores.stream().map(ore -> ore.toString()).collect(Collectors.joining(", "));
        }
    }

    static final class MaterialSerializer implements Serializer<Material, String> {
        @Override
        public Material deserialize(String str) {
            Material item = Material.getMaterial(str.trim().toUpperCase());
            if (item == null) {
                Pureores.getPlugin().getLogger().warning("Error in ores config: Invalid item material: " + str.trim());
            }
            return item;
        }

        @Override
        public String serialize(Material item) {
            return item.toString();
        }
    }

    static final class DropChanceSerializer implements Serializer<Double, Object> {
        @Override
        public Double deserialize(Object obj) {
            try {
                return Double.parseDouble(obj.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public String serialize(Double chance) {
            return Double.toString(chance);
        }
    }

    @Configuration
    public static final class Ore {
        private String name;
        private String formatting;
        @SerializeWith(serializer = MaterialSerializer.class)
        private Material item;
        @SerializeWith(serializer = BlockMaterialSerializer.class)
        private Set<Material> blocks;
        @SerializeWith(serializer = DropChanceSerializer.class)
        private Double chance;
        private String description;

        public Ore(String name, String formatting, Material item, Set<Material> blocks, double chance, String description) {
            this.name = name;
            this.formatting = formatting;
            this.item = item;
            this.blocks = blocks;
            this.chance = chance;
            this.description = description;
        }

        public Ore(String name, String formatting, Material item, Material blocks, double chance, String description) {
            this.name = name;
            this.formatting = formatting;
            this.item = item;
            this.blocks = new HashSet<>(Arrays.asList(blocks));
            this.chance = chance;
            this.description = description;
        }

        private Ore() {
        }

        public String getName() {
            return name;
        }

        public String getFormatting() {
            return formatting.replace("&", "§");
        }

        public Material getItem() {
            return item;
        }

        public Set<Material> getBlocks() {
            return Collections.unmodifiableSet(blocks);
        }

        public Double getChance() {
            return chance;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getFormatting() + getName() + ": " + getBlocks() + " > " + getItem() + " @ " + getChance();
        }
    }

    private OresConfig() {
    }

    @Comment({
        "The list of pure ores and their options. Each item should have the following properties:",
        "  name: What the item name will be in-game. You cannot include color codes here",
        "  formatting: The color/formatting codes to use with the item name above (use '&' or '§')",
        "  item: The material for the dropped item",
        "  blocks: A comma-separated list of materials for blocks to drop the item from",
        "  chance: The drop chance as a decimal (e.g. 0.5 for 50% or 0.005 for 0.5%)",
        "  description: Text to display in the item's lore",
        " ",
        "Example item:",
        "  - name: Flawless Diamond",
        "    formatting: §b",
        "    item: DIAMOND",
        "    blocks: DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE",
        "    chance: 0.5",
        "    description: A rare form of Diamond!",
        " ",
        "Guide for color codes: https://minecraft.fandom.com/wiki/Formatting_codes",
        "List of valid materials: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html"
    })
    private List<Ore> ores = new ArrayList<>(Arrays.asList(
    new Ore("Flawless Diamond", "§b", Material.DIAMOND,
    new HashSet<>(Arrays.asList(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)), DEFAULT_DROP_CHANCE,
    "A rare form of Diamond!"),
    new Ore("Flawless Emerald", "§2", Material.EMERALD,
    new HashSet<>(Arrays.asList(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)), DEFAULT_DROP_CHANCE,
    "A rare form of Emerald!"),
    new Ore("Pure Gold", "§e", Material.GOLD_INGOT,
    new HashSet<>(Arrays.asList(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE)), DEFAULT_DROP_CHANCE, "A rare form of Gold!"),
    new Ore("Pure Iron", "§f", Material.IRON_INGOT,
    new HashSet<>(Arrays.asList(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE)), DEFAULT_DROP_CHANCE, "A rare form of Iron!"),
    new Ore("Pure Redstone", "§c", Material.REDSTONE,
    new HashSet<>(Arrays.asList(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE)), DEFAULT_DROP_CHANCE,
    "A rare form of Redstone!"),
    new Ore("Pure Copper", "§6", Material.COPPER_INGOT,
    new HashSet<>(Arrays.asList(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)), DEFAULT_DROP_CHANCE,
    "A rare form of Copper!"),
    new Ore("Pure Coal", "§8", Material.COAL,
    new HashSet<>(Arrays.asList(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)), DEFAULT_DROP_CHANCE, "A rare form of Coal!"),
    new Ore("Flawless Lapis", "§9", Material.LAPIS_LAZULI,
    new HashSet<>(Arrays.asList(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE)), DEFAULT_DROP_CHANCE, "A rare form of Lapis!"),
    new Ore("Flawless Amethyst Shard", "§d", Material.AMETHYST_SHARD, Material.AMETHYST_CLUSTER, DEFAULT_DROP_CHANCE,
    "A rare form of Amethyst!")));

    @Comment({ "", "The value to use if an item does not have a drop chance" })
    @SerializeWith(serializer = DropChanceSerializer.class)
    private Double globalDropChance = DEFAULT_DROP_CHANCE;
    @Comment("Whether all items should use the global drop chance instead of their own drop chance")
    private boolean globalDropChanceOverride = false;

    public List<Ore> getOres() {
        return Collections.unmodifiableList(ores);
    }

    public Double getGlobalDropChance() {
        return globalDropChance;
    }

    public boolean isGlobalDropChanceOverride() {
        return globalDropChanceOverride;
    }

    public boolean validate() {
        int errors = 0;
        if (this.getGlobalDropChance() == null) {
            Pureores.getPlugin().getLogger().warning("Error in ores config: Missing global drop chance, setting to default of " + DEFAULT_DROP_CHANCE);
            errors++;
            this.globalDropChance = DEFAULT_DROP_CHANCE;
        }
        if (this.getOres() != null & this.getOres().size() > 0) {
            List<Ore> toRemove = new ArrayList<>();
            for (Ore ore : this.getOres()) {
                if (ore.getName() == null || ore.getName().isEmpty()) {
                    Pureores.getPlugin().getLogger().warning("Error in ores config: Missing pure item name, removing from list");
                    toRemove.add(ore);
                    errors++;
                    continue;
                }
                if (ore.getItem() == null) {
                    Pureores.getPlugin().getLogger().warning("Error in ores config: Missing item material for " + ore.getName() + ", removing from list");
                    toRemove.add(ore);
                    errors++;
                    continue;
                }
                if (ore.getBlocks() == null || ore.getBlocks().size() == 0) {
                    Pureores.getPlugin().getLogger().warning("Error in ores config: Missing block materials for " + ore.getName() + ", removing from list");
                    toRemove.add(ore);
                    errors++;
                    continue;
                }
                if (ore.getChance() != null) {
                    if (ore.getChance() < 0) {
                        Pureores.getPlugin().getLogger().warning("Error in ores config: Chance for " + ore.getName() + " cannot be negative");
                        ore.chance = 0.0;
                        errors++;
                    } else if (ore.getChance() > 1) {
                        Pureores.getPlugin().getLogger().warning("Error in ores config: Chance for " + ore.getName() + " cannot be >1");
                        ore.chance = 1.0;
                        errors++;
                    }
                }
                if (ore.getFormatting() == null || !ore.getFormatting().matches("^((&|§)[0-9a-fklmnor])*$")) {
                    Pureores.getPlugin().getLogger().warning("Error in ores config: Invalid formatting for " + ore.getName() + ", removing formatting");
                    ore.formatting = "";
                    errors++;
                }
            }
            for (Ore ore : toRemove) {
                this.ores.remove(ore);
            }
        }
        if (errors > 0) {
            Pureores.getPlugin().getLogger().warning("There are " + errors + " errors in your ores config. Please update your config using the log messages above.");
        }
        return errors == 0;
    }
}
