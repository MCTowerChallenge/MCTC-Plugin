package io.github.mctowerchallenge.towerchallenge.team.regions;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mctowerchallenge.towerchallenge.TowerChallenge;
import io.github.mctowerchallenge.towerchallenge.team.ParticipantTeam;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnBorderRegion extends EventRegion {

    public static final String REGION_TAG = "spawn_border";

    public SpawnBorderRegion(TowerChallenge plugin, Location[] bounds, ParticipantTeam team) {
        super(plugin, bounds, team, REGION_TAG);
        setFlags(getRegion());
    }

    @Override
    public void unregisterEvents() {
        PlayerRespawnEvent.getHandlerList().unregister(this);
    }

    @Override
    protected void setFlags(ProtectedRegion region) {
        region.setPriority(0);
        region.setFlag(Flags.DENY_MESSAGE, null);
        region.setFlag(Flags.ENTRY_DENY_MESSAGE, null);
        region.setFlag(Flags.EXIT_DENY_MESSAGE, null);
        region.setFlag(Flags.USE, StateFlag.State.ALLOW);
        region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);

        region.setFlag(Flags.MUSHROOMS, StateFlag.State.DENY);
        region.setFlag(Flags.LEAF_DECAY, StateFlag.State.DENY);
        region.setFlag(Flags.GRASS_SPREAD, StateFlag.State.DENY);
        region.setFlag(Flags.MYCELIUM_SPREAD, StateFlag.State.DENY);
        region.setFlag(Flags.VINE_GROWTH, StateFlag.State.DENY);
        region.setFlag(Flags.ROCK_GROWTH, StateFlag.State.DENY);
        region.setFlag(Flags.CROP_GROWTH, StateFlag.State.DENY);
        region.setFlag(Flags.SOIL_DRY, StateFlag.State.DENY);
        region.setFlag(Flags.CORAL_FADE, StateFlag.State.DENY);
        region.setFlag(Flags.COPPER_FADE, StateFlag.State.DENY);

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
