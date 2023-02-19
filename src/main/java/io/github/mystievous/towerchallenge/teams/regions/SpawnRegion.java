package io.github.mystievous.towerchallenge.teams.regions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.teams.ParticipantTeam;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SpawnRegion extends EventRegion {

    public static final Location referenceLocation = new Location(Worlds.Feb2023(), 136, 63, -2100, 0, 0);

    public static final Location baseSpawn = new Location(Worlds.Feb2023(), 142, 64, -2094);
    public static final Location[] baseBounds = new Location[]{
            new Location(Worlds.Feb2023(), 133, 64, -2103),
            new Location(Worlds.Feb2023(), 150, 319, -2086)
    };

    public static final Map<Integer, Location> teamLocations = new HashMap<>() {{
        put(2, referenceLocation);
        put(3, new Location(Worlds.Feb2023(), 158, 63, -2123, -90, 0));
        put(6, new Location(Worlds.Feb2023(), 175, 63, -2173, 180, 0));
        put(7, new Location(Worlds.Feb2023(), 197, 63, -2150, -90, 0));
        put(8, new Location(Worlds.Feb2023(), 197, 63, -2123, -90, 0));
        put(10, new Location(Worlds.Feb2023(), 214, 63, -2100, 0, 0));
        put(11, new Location(Worlds.Feb2023(), 236, 63, -2123, -90, 0));
        put(13, new Location(Worlds.Feb2023(), 214, 63, -2173, 180, 0));
        put(14, new Location(Worlds.Feb2023(), 253, 63, -2173, 180, 0));
    }};


    public SpawnRegion(TowerChallenge plugin, ParticipantTeam team) {
        super(plugin, Arrays.stream(baseBounds).map(location -> {
            Location teamLocation = teamLocations.get(team.getDatabaseId());
            Vector offset = teamLocation.clone().subtract(referenceLocation).toVector();

            return location.clone().add(offset).setDirection(teamLocation.getDirection());
        }).toArray(Location[]::new), team);
    }

    private Location offsetLocation(Location location) {
        Location teamLocation = teamLocations.get(getTeam().getDatabaseId());
        Vector offset = teamLocation.clone().subtract(referenceLocation).toVector();

        return location.clone().add(offset).setDirection(teamLocation.getDirection());
    }

    @Override
    public String getRegionName() {
        return String.format("%s-spawn", getTeam().getServerTeamName());
    }

    public @Nullable Location getSpawnpoint() {
        com.sk89q.worldedit.util.Location spawnLocation = getRegion().getFlag(Flags.SPAWN_LOC);
        if (spawnLocation != null) {
            return BukkitAdapter.adapt(spawnLocation);
        } else {
            return null;
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (isMember(event.getPlayer())) {
            Location spawnLocation = event.getRespawnLocation();
            if (spawnLocation.getWorld().equals(Worlds.Feb2023())) {
                Location location = getSpawnpoint();
                if (location != null) {
                    event.setRespawnLocation(location);
                }
            }
        }
    }

    @Override
    public void unregisterEvents() {
        PlayerRespawnEvent.getHandlerList().unregister(this);
    }

    @Override
    protected void setFlags(ProtectedRegion region) {
        com.sk89q.worldedit.util.Location spawnLoc = BukkitAdapter.adapt(offsetLocation(baseSpawn));
        region.setPriority(1);
        region.setFlag(Flags.SPAWN_LOC, spawnLoc);
        region.setFlag(Flags.TELE_LOC, spawnLoc);
        region.setFlag(Flags.DENY_MESSAGE, null);
        region.setFlag(Flags.ENTRY_DENY_MESSAGE, null);
        region.setFlag(Flags.EXIT_DENY_MESSAGE, null);
    }

    @Override
    public String parentRegionName() {
        return null;
    }
}
