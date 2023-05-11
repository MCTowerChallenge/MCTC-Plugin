package io.github.mystievous.towerchallenge.quests.instances;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.PortalLocation;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class QuestInstance {

    private final Vector offset;

    private MultiversePortals multiversePortals;
    private final TowerTeam team;

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

    public Location offsetLocation(Location baseLocation) {
        return baseLocation.clone().add(offset);
    }

    public void initPortal(String portalName, Location[] baseCorners, Location destinationLocation) {
        if (team != null) {
            String teamPortalName = TeamUtils.toTeamTag(team, portalName);
            Location[] instanceCorners = Arrays.stream(baseCorners).map(this::offsetLocation).toArray(Location[]::new);
            PortalManager portalManager = multiversePortals.getPortalManager();
            MVWorldManager worldManager = multiversePortals.getCore().getMVWorldManager();
            PortalLocation portalLocation = new PortalLocation(instanceCorners[0].toVector(), instanceCorners[1].toVector(), worldManager.getMVWorld(instanceCorners[0].getWorld()));
            portalManager.addPortal(new MVPortal(multiversePortals, teamPortalName, "Mystievous", portalLocation));
            MVPortal portal = portalManager.getPortal(teamPortalName);
            portal.setPortalLocation(portalLocation);
            portal.setExactDestination(destinationLocation);
        }
    }

    public void clearArea(Location[] baseArea) {
        Location[] offsetArea = Arrays.stream(baseArea).map(this::offsetLocation).toArray(Location[]::new);
        for (int x = offsetArea[0].getBlockX(); x <= offsetArea[1].getBlockX(); x++) {
            for (int y = offsetArea[0].getBlockY(); y <= offsetArea[1].getBlockY(); y++) {
                for (int z = offsetArea[0].getBlockZ(); z <= offsetArea[1].getBlockZ(); z++) {
                    Location location = new Location(offsetArea[0].getWorld(), x, y, z);
                    Block block = location.getBlock();
                    block.setType(Material.AIR);
                }
            }
        }
        Location center = offsetArea[0].clone().add(offsetArea[1]).multiply(0.5);
        center.getWorld().playSound(center, Sound.BLOCK_ANVIL_LAND, SoundCategory.RECORDS, 1f, 1f);
    }

}
