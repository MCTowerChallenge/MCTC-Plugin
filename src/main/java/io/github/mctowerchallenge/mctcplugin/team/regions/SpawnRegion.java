package io.github.mctowerchallenge.mctcplugin.team.regions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.hideentity.HiddenEntityManager;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnRegion extends EventRegion {

    public static final Map<Integer, Location> TeleporterLocations = new HashMap<>() {{
        put(2, new Location(Worlds.Oct2023_the_end(), 0, 60, -60));    // Red
        put(3, new Location(Worlds.Oct2023_the_end(), 23, 61, -55));    // Orange
        put(4, new Location(Worlds.Oct2023_the_end(), 42, 62, -42));    // Yellow
        put(5, new Location(Worlds.Oct2023_the_end(), 55, 62, -23));    // Lime
        put(6, new Location(Worlds.Oct2023_the_end(), 60, 60, 0));    // Green
        put(7, new Location(Worlds.Oct2023_the_end(), 55, 59, 23));    // Cyan
        put(8, new Location(Worlds.Oct2023_the_end(), 42, 59, 42));    // Light Blue
        put(9, new Location(Worlds.Oct2023_the_end(), 23, 59, 55));    // Blue
        put(10, new Location(Worlds.Oct2023_the_end(), 0, 59, 60));   // Purple
        put(11, new Location(Worlds.Oct2023_the_end(), -23, 60, 55));   // Magenta
        put(12, new Location(Worlds.Oct2023_the_end(), -42, 58, 42));   // Pink
        put(13, new Location(Worlds.Oct2023_the_end(), -55, 58, 23));   // White
        put(14, new Location(Worlds.Oct2023_the_end(), -60, 60, 0));   // Light Gray
        put(15, new Location(Worlds.Oct2023_the_end(), -55, 61, -23));   // Gray
        put(16, new Location(Worlds.Oct2023_the_end(), -42, 62, -42));   // Black
        put(17, new Location(Worlds.Oct2023_the_end(), -23, 60, -55));   // Brown
    }};

    public static final String REGION_TAG = "spawn";

    private final Location spawnLocation;

    private final BlockDisplay highlightEntity;
    private final Map<UUID, BukkitTask> highlighted;

    public SpawnRegion(Plugin plugin, Location[] bounds, Location spawnLocation, ParticipantTeam team) {
        super(plugin, bounds, team, REGION_TAG);
        this.spawnLocation = spawnLocation;
        setFlags(getRegion());
        Location[] borderBounds = { bounds[0].add(0, -24, 0), bounds[1] };
        new SpawnBorderRegion(plugin, borderBounds, team);
        Location highlightLocation = spawnLocation.clone().subtract(0, 1, 0);
        highlightLocation.setPitch(0.0f);
        highlightLocation.setYaw(0.0f);
        highlightLocation.getChunk().load();
        highlightEntity = (BlockDisplay) highlightLocation.getWorld().spawnEntity(highlightLocation, EntityType.BLOCK_DISPLAY);
        highlightEntity.addScoreboardTag("team-spawn-highlight");
        highlightEntity.setBlock(Bukkit.createBlockData(Material.valueOf(String.format("%s_STAINED_GLASS", team.getDye()))));
        highlightEntity.setGlowing(true);
        highlightEntity.setGlowColorOverride(team.getColor().toBukkitColor());
        highlightEntity.setTransformation(new Transformation(new Vector3f(-3.5f, 0.01f, -3.5f), new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f), new Vector3f(7.0f, 0.98f, 7.0f), new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f)));
        HiddenEntityManager.register(highlightEntity);
        highlighted = new HashMap<>();
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

    public void showHighlight(Player player) {
        HiddenEntityManager.showToPlayer(highlightEntity, player);
        UUID uuid = player.getUniqueId();
        BukkitTask task = highlighted.get(uuid);
        if (task != null) {
            task.cancel();
            highlighted.remove(uuid);
        }
        highlighted.put(uuid, new BukkitRunnable() {
            @Override
            public void run() {
                HiddenEntityManager.hideFromPlayer(highlightEntity, player);
            }
        }.runTaskLater(plugin, 100));
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
                    return;
                }
                Location location = getSpawnpoint();
                if (location != null) {
                    event.setRespawnLocation(location);
                }
            }
        }
    }

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        spawnLocation.getChunk().load();
        highlightEntity.remove();
        for (Map.Entry<UUID, BukkitTask> entry : highlighted.entrySet()) {
            entry.getValue().cancel();
        }
    }

    @Override
    public void unregisterEvents() {
        PlayerRespawnEvent.getHandlerList().unregister(this);
        HiddenEntityManager.unregister(highlightEntity);
        highlightEntity.getLocation().getChunk().load();
        highlightEntity.remove();
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
        region.setFlag(Flags.COPPER_FADE, StateFlag.State.ALLOW);

        setMobDamageFlags(region);

    }

    public static void setMobDamageFlags(ProtectedRegion region) {
        region.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
        region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(Flags.ENDERDRAGON_BLOCK_DAMAGE, StateFlag.State.DENY);
        region.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.DENY);
        region.setFlag(Flags.OTHER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(Flags.WITHER_DAMAGE, StateFlag.State.DENY);
        region.setFlag(Flags.ENDER_BUILD, StateFlag.State.DENY);
        region.setFlag(Flags.RAVAGER_RAVAGE, StateFlag.State.DENY);
        region.setFlag(Flags.ENTITY_PAINTING_DESTROY, StateFlag.State.DENY);
        region.setFlag(Flags.ENTITY_ITEM_FRAME_DESTROY, StateFlag.State.DENY);
    }

    @Override
    public String parentRegionName() {
        return null;
    }
}
