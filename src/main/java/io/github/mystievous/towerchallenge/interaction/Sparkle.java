package io.github.mystievous.towerchallenge.interaction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Represents a sparkling visual effect.
 */
public class Sparkle {

    private final Plugin plugin;

    private final float x;
    private final float y;
    private final float z;
    private final Location location;

    private BukkitTask task;

    /**
     * Creates a new Sparkle instance.
     *
     * @param plugin   The plugin instance.
     * @param location The location where the sparkle will appear.
     * @param x        The X offset of the sparkle.
     * @param y        The Y offset of the sparkle.
     * @param z        The Z offset of the sparkle.
     */
    public Sparkle(Plugin plugin, Location location, float x, float y, float z) {
        this.plugin = plugin;
        this.x = x;
        this.y = y;
        this.z = z;
        this.location = location;
        start();
    }

    /**
     * Starts the sparkle effect.
     */
    public void start() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            // /particle minecraft:wax_off ~-.5 ~2 ~-0.25 0.5 1 0.01 0.3 1 force
            location.getWorld().spawnParticle(Particle.WAX_OFF, location, 1, x, y, z, 0.3);
        }, 0, 2);
    }

    /**
     * Stops the sparkle effect.
     */
    public void stop() {
        if (!task.isCancelled()) {
            task.cancel();
        }
    }

}
