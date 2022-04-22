package me.favn.pureores.initials;

import me.favn.pureores.Pureores;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class ItemGenerator {
    public Map<String, Object> OresSection;
    private Plugin plugin;

    public ItemGenerator(Plugin config) {
        plugin = config;
        fetchItems();
    }

    public void fetchItems() {
        OresSection = plugin.getConfig().getConfigurationSection("Ores").getValues(false);
        for (Map.Entry<String, Object> entry : OresSection.entrySet()) {
            String key = entry.getKey();
            new ItemCreator(key, plugin);
        }
    }
}