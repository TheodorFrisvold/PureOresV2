package me.favn.pureores.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.favn.pureores.config.OresConfig.Ore;

public class PureoreEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Ore ore;
    private final Player player;
    private final boolean natural;
    private final int amount;

    private boolean isCancelled;

    public PureoreEvent(Ore ore, Player player, boolean natural, int amount) {
        this.ore = ore;
        this.player = player;
        this.natural = natural;
        this.amount = amount;
        this.isCancelled = false;
    }


    public Ore getItem() {
        return ore;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isNatural() {
        return natural;
    }

    public int getAmount() {
        return amount;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

}
