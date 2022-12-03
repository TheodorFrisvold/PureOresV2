package me.favn.pureores.config;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.favn.pureores.Pureores;

public final class TextConfig {
    private static final String CONFIG_FILE_NAME = "text.yml";

    private final Pureores plugin;
    private FileConfiguration config;

    public TextConfig(Pureores plugin) {
        this.plugin = plugin;

        this.reload();
    }

    public void reload() {
        File configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(CONFIG_FILE_NAME, false);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getMessage(String key) {
        return this.getMessage(key, null);
    }

    public String getMessage(String key, Placeholders options) {
        if (!this.config.contains(key)) {
            this.plugin.getLogger().warning("CONFIG ERROR: Invalid text key:" + key);
            return "";
        }
        String message = this.config.getString(key, "");
        if (message.isEmpty() || options == null) {
            return message;
        }

        return options.format(message);
    }

    public static final class Placeholders {
        private final String player;
        private final String item;
        private final String count;

        /**
         * Options to use when replacing placeholders in text. All parameters may be
         * {@code null}.
         *
         * @param player A Player or a String, to replace <code>{{player}}</code> with a
         *               player name
         * @param item   An ItemStack or a String, to replace <code>{{item}}</code> with
         *               an item name
         * @param count  An integer or null, to replace <code>{{count}}</code> with the
         *               number of pure items
         */
        public Placeholders(Object player, Object item, Integer count) {
            if (player instanceof Player) {
                this.player = ((Player) player).getName();
            } else if (player instanceof String) {
                this.player = (String) player;
            } else {
                this.player = "";
            }
            if (item instanceof ItemStack) {
                this.item = ((ItemStack) item).getItemMeta().getDisplayName();
            } else if (item instanceof String) {
                this.item = (String) item;
            } else {
                this.item = "";
            }
            this.count = count == null ? "" : count.toString();
        }

        protected String format(String str) {
            return str.replaceAll("\\{\\{player\\}\\}", this.player)
                    .replaceAll("\\{\\{item\\}\\}", this.item + ChatColor.RESET)
                    .replaceAll("\\{\\{count\\}\\}", this.count);
        }
    }
}