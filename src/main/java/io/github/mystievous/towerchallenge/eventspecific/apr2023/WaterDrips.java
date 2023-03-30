package io.github.mystievous.towerchallenge.eventspecific.apr2023;

import io.github.mystievous.towerchallenge.Database;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class WaterDrips {

    private final static SecureRandom RANDOM = new SecureRandom();

    public final Plugin plugin;
    public final Database database;

    public final Collection<BukkitTask> tasks;

    public WaterDrips(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        tasks = new ArrayList<>();
        loadDrips();
    }

    public void loadDrips() {
        unloadDrips();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                for (Location location : database.getWaterDrips()) {
                    tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        location.getWorld().spawnParticle(Particle.DRIP_WATER, location.getX()+.5, location.getY()-0.1, location.getZ()+.5, 1, 0.2, 0, 0.2, 0.1);
                    }, RANDOM.nextLong(100), 10+RANDOM.nextLong(20)));
                }
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Error loading water drips: " + e.getMessage());
            }
        });
    }

    public void unloadDrips() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }

}
