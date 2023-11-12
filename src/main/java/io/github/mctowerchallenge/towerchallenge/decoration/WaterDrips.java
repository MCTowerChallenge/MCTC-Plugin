package io.github.mctowerchallenge.towerchallenge.decoration;

import io.github.mctowerchallenge.towerchallenge.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Manages the visual effect of water drips at specific locations.
 */
public class WaterDrips {

    private final static Random RANDOM = new Random();

    public final Plugin plugin;
    public final Database database;

    /**
     * Collection of all currently running drip tasks.
     */
    public final Collection<BukkitTask> dripTasks;

    /**
     * Initializes a new instance of the WaterDrips class.
     *
     * @param plugin   The main plugin instance.
     * @param database The database used to store drip locations.
     */
    public WaterDrips(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        dripTasks = new ArrayList<>();
        loadDrips();
    }

    /**
     * Loads and activates water drips from the database.
     * Existing drip tasks are cancelled before loading.
     */
    public void loadDrips() {
        unloadDrips();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                for (Location location : database.getWaterDrips()) {
                    dripTasks.add(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        location.getWorld().spawnParticle(Particle.DRIP_WATER, location.getX() + .5, location.getY() - 0.1, location.getZ() + .5, 1, 0.2, 0, 0.2, 0.1);
                    }, RANDOM.nextLong(100), 10 + RANDOM.nextLong(20)));
                }
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Error loading water drips: " + e.getMessage());
            }
        });
    }

    /**
     * Cancels all running drip tasks, effectively stopping the water drip visual effect.
     */
    public void unloadDrips() {
        for (BukkitTask task : dripTasks) {
            task.cancel();
        }
    }

}
