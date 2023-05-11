package io.github.mystievous.towerchallenge.quests.instances;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.teams.regions.EventRegion;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class QuestRegion extends EventRegion {

    public QuestRegion(Plugin plugin, QuestInstance instance, Location[] area, String tag) {
        super(plugin, Arrays.stream(area).map(instance::offsetLocation).toArray(Location[]::new), instance.getTeam(), tag);
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
        region.setFlag(Flags.USE, StateFlag.State.ALLOW);
        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
        region.setFlag(Flags.DENY_MESSAGE, null);
        region.setFlag(Flags.ENTRY_DENY_MESSAGE, null);
        region.setFlag(Flags.EXIT_DENY_MESSAGE, null);
    }
}
