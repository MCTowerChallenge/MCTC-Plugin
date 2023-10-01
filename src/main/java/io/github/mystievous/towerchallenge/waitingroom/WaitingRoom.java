package io.github.mystievous.towerchallenge.waitingroom;

import io.github.mystievous.towerchallenge.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class WaitingRoom implements Listener {

    private boolean enabled;
    private Location spawnpoint;

    public WaitingRoom(Plugin plugin) {
        this.enabled = false;
        this.spawnpoint = Worlds.Waiting().getSpawnLocation();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSpawn(final PlayerSpawnLocationEvent event) {
        if (enabled) {
            event.setSpawnLocation(spawnpoint);
        }
    }

}
