package me.favn.pureores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

public class PureDrops<T extends Enum<?>> {
    private PureItem pureItem;
    private double dropChance;
    private int dropAmount;
    private List<T> dropFrom;

    public PureDrops(PureItem pureItem, double dropChance, int dropAmount, List<T> dropFrom) {
        this.pureItem = pureItem;
        this.dropChance = Math.max(Math.min(dropChance, 1), 0);
        this.dropAmount = Math.max(Math.min(dropAmount, pureItem.getItem().getMaxStackSize()), 0);
        this.dropAmount = dropAmount;
        this.dropFrom = new ArrayList<>(dropFrom);
    }

    public PureItem getPureItem() {
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
