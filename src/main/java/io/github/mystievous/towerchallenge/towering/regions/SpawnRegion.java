package io.github.mystievous.towerchallenge.towering.regions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class SpawnRegion extends EventRegion implements Listener {

    private final ProtectedRegion region;

    public SpawnRegion(ParticipantTeam team, ChallengeManager manager, ProtectedRegion region) {
        super(team, manager, region);
        JavaPlugin plugin = manager.getPlugin();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        this.region = region;
    }

    public @Nullable Location getSpawnpoint() {
        return BukkitAdapter.adapt(region.getFlag(Flags.SPAWN_LOC));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (isMember(event.getPlayer())) {
            Location location = getSpawnpoint();
            if (location != null) {
                event.setRespawnLocation(location);
            }
        }
    }

}
