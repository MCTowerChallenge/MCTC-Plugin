package io.github.mystievous.towerchallenge.decoration;

import io.github.mystievous.towerchallenge.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class WaterDrips {

    private final static Random RANDOM = new Random();

    public final Plugin plugin;
    public final Database database;

    /**
     * Collection of all currently running drips.
     */
    public final Collection<BukkitTask> dripTasks;

    public WaterDrips(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        dripTasks = new ArrayList<>();
        loadDrips();
    }

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

    public void unloadDrips() {
        for (BukkitTask task : dripTasks) {
            task.cancel();
        }
    }

}
