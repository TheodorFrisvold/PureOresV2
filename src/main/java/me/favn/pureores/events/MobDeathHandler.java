package me.favn.pureores.events;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.favn.pureores.Pureores;

public class MobDeathHandler implements Listener {
    private final Pureores plugin;

    public MobDeathHandler(Pureores plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        EntityType type = event.getEntityType();
        Player killer = event.getEntity().getKiller();

        // determine if config exists for the given entity type

        // roll to drop pure loot

        // give pure loot to player
    }
}
