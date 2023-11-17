package me.favn.pureores.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

import me.favn.pureores.Pureores;

public class LootChestHandler implements Listener {
    private final Pureores plugin;

    public LootChestHandler(Pureores plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        String structureName = "ABANDONED_MINESHAFT";
        List<ItemStack> loot = new ArrayList<>(event.getLoot());
        NamespacedKey eventLootTable = event.getLootTable().getKey();
        NamespacedKey fromConfig = Enum.valueOf(LootTables.class, structureName).getKey();
    }
}
