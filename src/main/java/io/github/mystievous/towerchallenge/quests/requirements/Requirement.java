package io.github.mystievous.towerchallenge.quests.requirements;

import io.github.mystievous.mystigui.element.Representable;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

/**
 * A requirement for a {@link RequirementsQuest}
 */
public abstract class Requirement implements Representable {

    private final int required;
    private int current;
    private Component name;

    public Requirement(int required) {

        this.required = required;
        this.current = 0;

    }

    /**
     * Makes a copy of this requirement.
     *
     * @return the new {@link Requirement}
     */
    public abstract Requirement copy();

    public void setName(Component name) {
        this.name = name;
    }

    public Component getName() {
        return name;
    }

    public int getRequired() {
        return required;
    }

    public int getCurrent() {
        return current;
    }

    public int getRemaining() {
        return required - current;
    }

    protected int subtractCurrent(int amount) {
        current = current + amount;
        int taken = amount;
        if (current > required) {
            taken = amount - (current-required);
            current = required;
        }
        return taken;
    }

    /**
     * Checks whether the given item can
     * contribute to this requirement.
     *
     * @param item The item to check
     * @return True, if it matches
     *         the requirement
     */
    public abstract boolean matchItem(ItemStack item);

    /**
     * Turns in items to this requirement
     * @param item - Itemstack being turned in
     */
    public void turnIn(ItemStack item) {
        if (matchItem(item)) {
            int taken = subtractCurrent(item.getAmount());
            item.subtract(taken);
        }
    }

}
