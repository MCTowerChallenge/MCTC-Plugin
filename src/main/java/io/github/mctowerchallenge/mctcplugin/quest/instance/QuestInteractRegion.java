package io.github.mctowerchallenge.mctcplugin.quest.instance;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class QuestInteractRegion extends QuestRegion {

    public QuestInteractRegion(Plugin plugin, QuestInstance instance, Location[] templateArea, String tag) {
        super(plugin, instance, templateArea, tag);
    }

    @Override
    protected void setFlags(ProtectedRegion region) {
        super.setFlags(region);
        region.setFlag(Flags.USE, StateFlag.State.ALLOW);
        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        region.setPriority(1);
    }
}
