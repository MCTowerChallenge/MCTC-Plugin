package io.github.mctowerchallenge.mctcplugin.interaction;

import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Represents a sparkling visual effect.
 */
public class Sparkle {

    private final Plugin plugin;

    private final Vector scale;
    private final Location location;

    private BukkitTask task;

    private final Set<UUID> removedPlayers;

    /**
     * Creates a new Sparkle instance.
     *
     * @param plugin   The plugin instance.
     * @param location The location where the sparkle will appear.
     * @param scale Scale of the particle effect
     */
    public Sparkle(Plugin plugin, Location location, Vector scale) {
        this.plugin = plugin;
        this.scale = scale;
        this.location = location;
        removedPlayers = new HashSet<>();
        start();
    }

    public void removeTeam(TowerTeam team) {
        removedPlayers.addAll(team.getOfflinePlayers().stream().map(OfflinePlayer::getUniqueId).toList());
    }

    public void addTeam(TowerTeam team) {
        team.getOfflinePlayers().stream().map(OfflinePlayer::getUniqueId).toList().forEach(removedPlayers::remove);
    }

    /**
     * Starts the sparkle effect.
     */
    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                location.getWorld().spawnParticle(Particle.WAX_OFF, location, 1, scale.getX(), scale.getY(), scale.getZ(), 0.3);
            }
        }.runTaskTimer(plugin, 0L, 2L);
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
