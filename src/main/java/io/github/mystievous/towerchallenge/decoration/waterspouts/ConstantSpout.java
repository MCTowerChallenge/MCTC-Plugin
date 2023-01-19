package io.github.mystievous.towerchallenge.decoration.waterspouts;

import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Same as the other {@link Spout} class, but runs at
 * a constant height instead of a variable one
 */
public class ConstantSpout extends Spout {

    private final double height;

    /**
     * Same as the other Spout class, but runs at
     * a constant height instead of a variable one
     */
    public ConstantSpout(Location location, double height) {
        super(location);
        this.height = height;
    }

    /**
     * Runs the spout, with constant height
     */
    @Override
    public void runSpout() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(TowerChallenge.getInstance(), () -> {
            spray(height);
        }, 0, 1);
    }
}
