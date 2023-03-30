package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.gallery;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.EvilTower;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.teams.regions.EventRegion;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;

import java.util.Arrays;

public class GalleryRegion extends EventRegion {

    private static final Location[] baseCorners = {
            new Location(Worlds.eviltowers(), -35, 100, -39),
            new Location(Worlds.eviltowers(), -20, 119, -59)
    };

    public GalleryRegion(TowerChallenge plugin, EvilTower evilTower, TowerTeam team) {
        super(plugin, Arrays.stream(baseCorners).map(evilTower::offsetLocation).toArray(Location[]::new), team);
    }

    @Override
    public void unregisterEvents() {}

    @Override
    public String getRegionName() {
        return TeamUtils.toTeamTag(getTeam(), "gallery_area");
    }

    @Override
    public String parentRegionName() {
        return null;
    }

    @Override
    protected void setFlags(ProtectedRegion region) {
        region.setFlag(Flags.ENTRY, StateFlag.State.DENY);
        region.setFlag(Flags.DENY_MESSAGE, null);
        region.setFlag(Flags.EXIT_DENY_MESSAGE, null);
        region.setFlag(Flags.ENTRY_DENY_MESSAGE, LegacyComponentSerializer.legacyAmpersand().serialize(Component.text("A dark force keeps you back...").decoration(TextDecoration.ITALIC, true)));
    }


}
