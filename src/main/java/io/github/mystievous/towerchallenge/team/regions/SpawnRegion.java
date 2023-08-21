package io.github.mystievous.towerchallenge.team.regions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.portal.EndPortal;
import io.github.mystievous.towerchallenge.team.ParticipantTeam;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.Nullable;

public class SpawnRegion extends EventRegion {

    public static final String REGION_TAG = "spawn";

    private final Location spawnLocation;

    public SpawnRegion(TowerChallenge plugin, Location[] bounds, Location spawnLocation, ParticipantTeam team) {
        super(plugin, bounds, team, REGION_TAG);
        this.spawnLocation = spawnLocation;
        setFlags(getRegion());
    }

    /**
     * Gets the {@link Location} of the
     * player spawn tag of
     * this region.
     *
     * @return the {@link Location}.
     */
    public @Nullable Location getSpawnpoint() {
        com.sk89q.worldedit.util.Location spawnLocation = getRegion().getFlag(Flags.SPAWN_LOC);
        if (spawnLocation != null) {
            return BukkitAdapter.adapt(spawnLocation);
        } else {
            return null;
        }
    }

    /**
     * When a player respawns, makes them
     * respawn at this region's spawnpoint
     * if they are a member of the region.
     *
     * @param event The respawn event.
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (isMember(event.getPlayer())) {
            Location newSpawnLocation = event.getRespawnLocation();
            if (newSpawnLocation.getWorld().equals(spawnLocation.getWorld())) {
                if (event.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)) {
                    event.setRespawnLocation(EndPortal.overworldSpawn);
                }
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
        com.sk89q.worldedit.util.Location spawnLoc = BukkitAdapter.adapt(spawnLocation);
        region.setPriority(1);
        region.setFlag(Flags.SPAWN_LOC, spawnLoc);
        region.setFlag(Flags.TELE_LOC, spawnLoc);
        region.setFlag(Flags.DENY_MESSAGE, null);
        region.setFlag(Flags.ENTRY_DENY_MESSAGE, null);
        region.setFlag(Flags.EXIT_DENY_MESSAGE, null);

        region.setFlag(Flags.MUSHROOMS, StateFlag.State.ALLOW);
        region.setFlag(Flags.LEAF_DECAY, StateFlag.State.ALLOW);
        region.setFlag(Flags.GRASS_SPREAD, StateFlag.State.ALLOW);
        region.setFlag(Flags.MYCELIUM_SPREAD, StateFlag.State.ALLOW);
        region.setFlag(Flags.VINE_GROWTH, StateFlag.State.ALLOW);
        region.setFlag(Flags.ROCK_GROWTH, StateFlag.State.ALLOW);
        region.setFlag(Flags.CROP_GROWTH, StateFlag.State.ALLOW);
        region.setFlag(Flags.SOIL_DRY, StateFlag.State.ALLOW);
        region.setFlag(Flags.CORAL_FADE, StateFlag.State.ALLOW);
    }

    @Override
    public String parentRegionName() {
        return null;
    }
}
