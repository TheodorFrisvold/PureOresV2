package me.favn.pureores.initials;

import me.favn.pureores.Pureores;
import org.bukkit.Material;

import java.util.Map;
import java.util.Objects;

public class BlockListGen {
    private final Pureores main;

    public BlockListGen(Pureores plugin) {
        main = plugin;
        addBlocks();
    }


    public void addBlocks() {

        Map<String, Object> checkOreBlocks = Objects.requireNonNull(main.getConfig().getConfigurationSection("Ores")).getValues(false);
        for (Map.Entry<String, Object> entry : checkOreBlocks.entrySet()) {
            String key = entry.getKey();
            BlockListGenerator(key, main);
        }
        Map<String, Object> globalDropSettings = Objects.requireNonNull(main.getConfig().getConfigurationSection("Globals.DropChance"), "DropChance cannot be null").getValues(false);
        if (((Boolean) globalDropSettings.get("Use"))) {
            Pureores.useglobalchance = true;
        }

    }

    public void BlockListGenerator(Object itemsection, Pureores main) {
        String checkthis = "Ores." + itemsection.toString();
        Map<String, Object> subSection = Objects.requireNonNull(main.getConfig().getConfigurationSection(checkthis), "SubSection cannot be null").getValues(false);
        addBlock(subSection, itemsection.toString());
        Pureores.names.add(String.valueOf(itemsection));


    }

    private void addBlock(Map<String, Object> SubSection, String item) {
        Pureores.NameAndItemname.put(item.toUpperCase(), String.valueOf(SubSection.get("Name")));
        Material regular = Material.valueOf(SubSection.get("Material_Block").toString());
        Pureores.blocks.add(regular);
        Pureores.blockidentifier.put(regular, String.valueOf(SubSection.get("Name")));
        Pureores.dropchances.put(regular, ((Integer) SubSection.get("Chance")));

        if (((boolean) SubSection.get("Deepslate"))) {
            Pureores.blocks.add(Material.valueOf("DEEPSLATE_" + regular));
            Pureores.blockidentifier.put(Material.valueOf("DEEPSLATE_" + regular), String.valueOf(SubSection.get("Name")));
            Pureores.dropchances.put(Material.valueOf("DEEPSLATE_" + regular), ((Integer) SubSection.get("Chance")));

        }
    }
}