package me.favn.pureores.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.favn.pureores.Pureores;

public final class OresConfig {
    private static final String CONFIG_FILE_NAME = "ores.yml";

    private final Pureores plugin;
    private FileConfiguration config;

    public OresConfig(Pureores plugin) {
        this.plugin = plugin;

        this.reload();

        this.memoizedOres = new HashMap<>();
    }

    public void reload() {
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(CONFIG_FILE_NAME, false);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * The global drop chance setting.
     * This value is used when an {@link Ore} doesn't have a drop chance,
     * or {@link OresConfig#useGlobalDropChance()} returns {@code true}.
     *
     * @return
     */
    public double getGlobalDropChance() {
        double chance = this.config.getDouble("globals.chance.value");
        if (chance == 0) {
            this.warn("Missing global drop chance, using 0%");
        }
        return chance;
    }

    /**
     * Whether all pure ores should use the global drop chance.
     */
    public boolean useGlobalDropChance() {
        return this.config.getBoolean("globals.chance.use", false);
    }

    /**
     * A map of aliases and {@link Ore}s.
     * The keys of this map are memoized values returned from
     * {@link OresConfig#getOre(String)}.
     */
    private Map<String, Ore> memoizedOres;

    /**
     * Gets a pure ore object by its alias and validates all its properties.
     * If there are any validation errors, they are logged, and {@code null} is
     * returned.
     *
     * @param alias The alias to get the ore by.
     * @return An {@link Ore} if {@code alias} and its ore are valid, otherwise
     *         {@code null}.
     */
    public Ore getOre(String alias) {
        if (alias == null) {
            return null;
        }
        // Memoize results so we don't have to hit the config and validte
        // twice for the same alias
        Ore foundOre = this.memoizedOres.get(alias.trim().toLowerCase());
        if (foundOre != null) {
            return foundOre;
        }

        ConfigurationSection section = this.config.getConfigurationSection("ores." + alias.trim());
        if (section == null) {
            this.warn("Missing ore section for " + alias);
            return null;
        }
        String name = section.getString("name", "");
        if (name.isEmpty()) {
            this.warn("Missing display name for " + alias);
            return null;
        }
        String formatting = section.getString("formatting");
        Material item = Material.getMaterial(section.getString("item", ""));
        if (item == null) {
            this.warn("Missing/invalid item material for " + alias);
            return null;
        }
        Set<Material> blocks = new HashSet<Material>();
        List<String> blockNames = section.getStringList("blocks");
        for (String blockName : blockNames) {
            Material block = Material.getMaterial(blockName.trim().toUpperCase());
            if (block != null && block.isBlock()) {
                blocks.add(block);
            } else {
                this.warn("Block material for " + alias + " is not a block (" + blockName + ")");
            }
        }
        if (blocks.isEmpty()) {
            // Try to get a single block material
            String blockName = section.getString("blocks");
            if (blockName != null) {
                Material block = Material.getMaterial(blockName.trim().toUpperCase());
                if (block != null && block.isBlock()) {
                    blocks.add(block);
                } else {
                    this.warn("Block material for " + alias + " is not a block (" + blockName + ")");
                }
            }
        }
        if (blocks.isEmpty()) {
            this.warn("No valid block materials for " + alias);
            return null;
        }
        Double chance = section.getDouble("chance", getGlobalDropChance());
        String description = section.getString("description");

        Ore ore = new Ore(alias, name, formatting, item, blocks, chance, description);
        this.memoizedOres.put(alias.trim().toLowerCase(), ore);
        return ore;
    }

    /**
     * Gets a pure ore object that drops from {@code block}, if there is one.
     *
     * @param block The block material to look for pure ore drops from.
     * @return An {@link Ore} if there are any that are valid and drop from
     *         {@code block}, otherwise {@code null}.
     */
    public Ore getOre(Material block) {
        for (Ore ore : this.getOres()) {
            if (ore.getBlocks().contains(block)) {
                return ore;
            }
        }
        return null;
    }

    /**
     * Gets a list of all valid pure ore objects.
     *
     * @return A list of {@link Ore}s.
     */
    public List<Ore> getOres() {
        List<Ore> ores = new ArrayList<>();
        if (!this.config.contains("ores")) {
            this.warn("Missing ores section");
            return ores;
        }
        Set<String> aliases = this.config.getConfigurationSection("ores").getKeys(false);
        for (String alias : aliases) {
            Ore ore = this.getOre(alias);
            if (ore != null) {
                ores.add(ore);
            }
        }
        return ores;
    }

    public final class Ore {
        protected Ore(String alias, String name, String formatting, Material item, Set<Material> blocks, Double chance,
                String description) {
            this.alias = alias;
            this.name = name;
            this.formatting = formatting;
            this.item = item;
            this.blocks = blocks;
            this.chance = chance;
            this.description = description;
        }

        private String alias;
        private String name;
        private String formatting;
        private Material item;
        private Set<Material> blocks;
        private Double chance;
        private String description;

        /**
         * Gets the alias to use with the /givepure command.
         */
        public String getAlias() {
            return alias;
        }

        /**
         * Gets the name of the pure item without formatting.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the display name of the dropped pure item, with any formatting.
         */
        public String getDisplayName() {
            return ChatColor.RESET + this.getFormatting() + this.getName() + ChatColor.RESET;
        }

        /**
         * Gets the formatting and color codes to use with the pure item's name.
         */
        public String getFormatting() {
            if (formatting == null) {
                return "";
            }
            return formatting.trim().replaceAll("&", "§");
        }

        /**
         * Gets the material that is dropped as a pure item.
         */
        public Material getItem() {
            return item;
        }

        /**
         * Gets block materials that drop a pure item.
         */
        public Set<Material> getBlocks() {
            return Collections.unmodifiableSet(blocks);
        }

        /**
         * Gets the drop chance for this pure ore.
         * The global drop chance is returned if
         * {@link OresConfig#useGlobalDropChance()} returns true,
         * or if this ore doesn't have a drop chance.
         */
        public Double getChance() {
            if (chance == null || useGlobalDropChance()) {
                return getGlobalDropChance();
            }
            return chance;
        }

        /**
         * Gets the lore text for the dropped pure item.
         */
        public String getDescription() {
            if (description == null) {
                return "";
            }
            return description;
        }

        /**
         * Get the item dropped by a pure ore.
         *
         * @param amount The amount of items in the returned item stack.
         */
        public ItemStack toItemStack(int amount) {
            if (amount <= 0) {
                return null;
            }
            ItemStack item = new ItemStack(getItem(), amount);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(this.getDisplayName());
            if (!getDescription().isEmpty()) {
                meta.setLore(Arrays.asList(ChatColor.RESET + getDescription()));
            }
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);

            return item;
        }

        /**
         * Get the item dropped by a pure ore, with one item in the returned item stack.
         */
        public ItemStack toItemStack() {
            return this.toItemStack(1);
        }

        /**
         * @see Ore#getDisplayName()
         */
        @Override
        public String toString() {
            return this.getDisplayName();
        }
    }

    /**
     * A utility method to avoid typing {@code this.plugin.getLogger()} everywhere.
     * @see java.util.logging.Logger#warning(String)
     * @see org.bukkit.plugin.java.JavaPlugin#getLogger()
     */
    private void warn(String message) {
        this.plugin.getLogger().warning("CONFIG ERROR: " + message);
    }
}
