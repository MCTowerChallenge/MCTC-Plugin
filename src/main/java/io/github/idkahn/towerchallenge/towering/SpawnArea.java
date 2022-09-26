package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnArea implements Listener {

    private ProtectedRegion region;
    private JavaPlugin plugin;

    public SpawnArea(JavaPlugin plugin, ProtectedRegion region) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        this.region = region;
    }

    public boolean isMember(Player player) {
        return region.isMember(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    public void addPlayer(OfflinePlayer player) {
        region.getMembers().addPlayer(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        region.getMembers().removePlayer(player.getUniqueId());
    }

    public Location getSpawnpoint() {
        return BukkitAdapter.adapt(region.getFlag(Flags.SPAWN_LOC));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        try {
            if (isMember(event.getPlayer())) {
                event.setRespawnLocation(getSpawnpoint());
            }
        } catch (NullPointerException e) {

        }
    }

}
