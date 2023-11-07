package io.github.mystievous.towerchallenge.quest.instance;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.team.regions.EventRegion;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

/**
 * {@link EventRegion} for a specific {@link QuestInstance}
 */
public class QuestRegion extends EventRegion {

    /**
     * Creates or updates a new region, offset for this instance.
     *
     * @param plugin   Current plugin instance
     * @param instance {@link QuestInstance} this region is associated with
     * @param templateArea     Area for the region to cover
     * @param tag      Tag to name the region with
     */
    public QuestRegion(Plugin plugin, QuestInstance instance, Location[] templateArea, String tag) {
        super(plugin, Arrays.stream(templateArea).map(instance::offsetLocation).toArray(Location[]::new), instance.getTeam(), tag);
        setFlags(getRegion());
    }

    @Override
    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String parentRegionName() {
        return null;
    }

    @Override
    protected void setFlags(ProtectedRegion region) {
        region.setFlag(Flags.ITEM_FRAME_ROTATE, StateFlag.State.DENY);
        region.setFlag(Flags.ENTITY_ITEM_FRAME_DESTROY, StateFlag.State.DENY);
        region.setFlag(Flags.ENTITY_PAINTING_DESTROY, StateFlag.State.DENY);
        region.setFlag(Flags.USE, StateFlag.State.DENY);
        region.setFlag(Flags.INTERACT, StateFlag.State.DENY);
        region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.DENY);
        region.setFlag(Flags.DENY_MESSAGE, null);
        region.setFlag(Flags.ENTRY_DENY_MESSAGE, null);
        region.setFlag(Flags.EXIT_DENY_MESSAGE, null);
    }
}
