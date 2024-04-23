package io.github.mctowerchallenge.mctcplugin.quest.instance;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.utility.RegionUtils;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class QuestInteractRegion extends QuestRegion {

    public QuestInteractRegion(Plugin plugin, QuestInstance instance, Location[] templateArea, String tag) {
        super(plugin, instance, templateArea, tag);
        BlockVector3 minPoint = getRegion().getMinimumPoint();
        Location minLoc = new Location(Worlds.May2024_quest(), minPoint.getX(), minPoint.getY(), minPoint.getZ());

        BlockVector3 maxPoint = getRegion().getMaximumPoint();
        Location maxLoc = new Location(Worlds.May2024_quest(), maxPoint.getX(), maxPoint.getY(), maxPoint.getZ());

        RegionUtils.loopThroughChunks(minLoc, maxLoc, chunk -> chunk.addPluginChunkTicket(plugin));
    }

    @Override
    protected void setFlags(ProtectedRegion region) {
        super.setFlags(region);
        region.setFlag(Flags.USE, StateFlag.State.ALLOW);
        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        region.setPriority(1);
    }
}
