package io.github.mystievous.towerchallenge.quest.instance;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.PortalLocation;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import io.github.mystievous.towerchallenge.utility.MVPortalUtils;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * Instance of a quest area, offset for a specific {@link TowerTeam}.
 */
public class QuestInstance {

    /**
     * Offset from the base location for this instance
     */
    private final Vector offset;

    private MultiversePortals multiversePortals;
    private final TowerTeam team;

    /**
     * Creates a quest instance, and
     * generates the offset based
     * on the {@link Location} parameters
     *
     * @param team             Team this instance is associated with
     * @param baseLocation     Location of the base instance
     * @param instanceLocation Location of this instance
     */
    public QuestInstance(TowerTeam team, Location baseLocation, Location instanceLocation) {
        this.team = team;
        this.offset = instanceLocation.clone().subtract(baseLocation).toVector();

        Plugin mvpPlugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Portals");
        if (mvpPlugin instanceof MultiversePortals portals) {
            this.multiversePortals = portals;
        }
    }

    public TowerTeam getTeam() {
        return team;
    }

    /**
     * Offsets a {@link Location} from the base instance, to
     * the relative {@link Location} in this one.
     *
     * @param baseLocation The location in the base instance.
     * @return The relative location in this instance.
     */
    public Location offsetLocation(Location baseLocation) {
        return baseLocation.clone().add(offset);
    }

    /**
     * Creates/Updates a {@link MVPortal} for this instance
     *
     * @param portalName          Name of the portal. Must be unique across the server!
     * @param baseCorners         Corners of the portal in the base instance.
     * @param destinationLocation Absolute location on the server to teleport the player to.
     */
    public void initPortal(String portalName, Location[] baseCorners, Location destinationLocation) {
        if (team != null) {
            String teamPortalName = TeamUtils.toTeamTag(team, portalName);
            Location[] instanceCorners = Arrays.stream(baseCorners).map(this::offsetLocation).toArray(Location[]::new);
            MVPortalUtils.initPortal(teamPortalName, instanceCorners, destinationLocation);
        }
    }

    /**
     * Fills an area in this instance with a specific block type.
     *
     * @param baseArea  Area in the base instance to fill.
     * @param blockType Material to fill the area with.
     */
    public void fillArea(Location[] baseArea, Material blockType) {
        Location[] offsetArea = Arrays.stream(baseArea).map(this::offsetLocation).toArray(Location[]::new);
        for (int x = offsetArea[0].getBlockX(); x <= offsetArea[1].getBlockX(); x++) {
            for (int y = offsetArea[0].getBlockY(); y <= offsetArea[1].getBlockY(); y++) {
                for (int z = offsetArea[0].getBlockZ(); z <= offsetArea[1].getBlockZ(); z++) {
                    Location location = new Location(offsetArea[0].getWorld(), x, y, z);
                    Block block = location.getBlock();
                    block.setType(blockType);
                }
            }
        }
        Location center = offsetArea[0].clone().add(offsetArea[1]).multiply(0.5);
        center.getWorld().playSound(center, Sound.BLOCK_ANVIL_LAND, SoundCategory.RECORDS, 1f, 1f);
    }

}
