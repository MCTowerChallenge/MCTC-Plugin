package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.PortalLocation;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class TowerPortalManager {

    public static final String HUB_ENTRANCE = "hub-entrance";
    public static final Location[] baseHubEntranceCorners = {new Location(Worlds.eviltowers(), 0, 99, -8), new Location(Worlds.eviltowers(), 0, 103, -10)};

    public static final String HUB_GALLERY = "hub-gallery";
    public static final Location[] baseHubGalleryCorners = {new Location(Worlds.eviltowers(), 5, 99, -16), new Location(Worlds.eviltowers(), 6, 103, -16)};
    public static final Location baseHubGalleryDestination = new Location(Worlds.eviltowers(), -35.5, 102, -33.5, -90, 0);

    public static final String GALLERY_HUB = "gallery-hub";
    public static final Location[] baseGalleryHubCorners = {new Location(Worlds.eviltowers(), -36, 102, -33), new Location(Worlds.eviltowers(), -36, 106, -35)};
    public static final Location baseGalleryHubDestination = new Location(Worlds.eviltowers(), 6, 99, -14, 0, 0);

    public static final String HUB_MAZE = "hub-maze";
    public static final Location[] baseHubMazeCorners = {new Location(Worlds.eviltowers(), 10, 99, -16), new Location(Worlds.eviltowers(), 11, 68, -18)};
    public static final Location baseHubMazeDestination = new Location(Worlds.eviltowers(), 11, 65, -18, 180, 0);

    public static final String MAZE_HUB = "maze-hub";
    public static final Location[] baseMazeHubCorners = {new Location(Worlds.eviltowers(), 10, 65, -18), new Location(Worlds.eviltowers(), 11, 103, -16)};
    public static final Location baseMazeHubDestination = new Location(Worlds.eviltowers(), 11, 99, -14, 0, 0);

    public static final String MAZE_EXIT = "maze-exit";
    public static final Location[] baseMazeExitCorners = {new Location(Worlds.eviltowers(), 10, 65, -71), new Location(Worlds.eviltowers(), 11, 67, -71)};

    public static final String MAZE_RESET = "maze-reset";
    public static final Location[] baseMazeResetCorners = {new Location(Worlds.eviltowers(), 35, 62, -20), new Location(Worlds.eviltowers(), -14, 62, -69)};

    public static final String HUB_OCEAN = "hub-ocean";
    public static final Location[] baseHubOceanCorners = {new Location(Worlds.eviltowers(), 15, 99, -16), new Location(Worlds.eviltowers(), 16, 103, -16)};
    public static final Location baseHubOceanDestination = new Location(Worlds.eviltowers(), 52.5, 105, -76, 0, 0);

    public static final String OCEAN_HUB = "ocean-hub";
    public static final Location[] baseOceanHubCorners = {new Location(Worlds.eviltowers(), 51, 105, -77), new Location(Worlds.eviltowers(), 53, 107, -77)};
    public static final Location baseOceanHubDestination = new Location(Worlds.eviltowers(), 16, 99, -14, 0, 0);

    public static final String HUB_TOWER = "hub-tower";
    public static final Location[] baseHubTowerCorners = {new Location(Worlds.eviltowers(), 21, 99, -11), new Location(Worlds.eviltowers(), 21, 103, -7)};

    private final EvilTower evilTower;
    private final TeamManager teamManager;
    private final int teamId;
    private MultiversePortals multiversePortals;

    public TowerPortalManager(EvilTower evilTower, TeamManager teamManager, int teamId) {
        this.evilTower = evilTower;
        this.teamManager = teamManager;
        this.teamId = teamId;

        Plugin mvpPlugin = Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Portals");

        if (mvpPlugin instanceof MultiversePortals portals) {
            this.multiversePortals = portals;
        }

        loadPortals();

    }

    public void loadPortals() {
        initPortal(HUB_ENTRANCE, baseHubEntranceCorners, EvilTowerManager.towerExit);
        initPortal(HUB_GALLERY, baseHubGalleryCorners, evilTower.offsetLocation(baseHubGalleryDestination));
        initPortal(GALLERY_HUB, baseGalleryHubCorners, evilTower.offsetLocation(baseGalleryHubDestination));
        initPortal(HUB_MAZE, baseHubMazeCorners, evilTower.offsetLocation(baseHubMazeDestination));
        initPortal(MAZE_HUB, baseMazeHubCorners, evilTower.offsetLocation(baseMazeHubDestination));
        initPortal(MAZE_EXIT, baseMazeExitCorners, evilTower.offsetLocation(baseMazeHubDestination));
        initPortal(MAZE_RESET, baseMazeResetCorners, evilTower.offsetLocation(baseHubMazeDestination));
        initPortal(HUB_OCEAN, baseHubOceanCorners, evilTower.offsetLocation(baseHubOceanDestination));
        initPortal(OCEAN_HUB, baseOceanHubCorners, evilTower.offsetLocation(baseOceanHubDestination));
        initPortal(HUB_TOWER, baseHubTowerCorners, EvilTowerManager.towerTop);
    }

    private void initPortal(String portalName, Location[] baseCorners, Location destinationLocation) {
        TowerTeam team = teamManager.getTeam(teamId);
        if (team != null) {
            String teamPortalName = TeamUtils.toTeamTag(team, portalName);
            Location[] corners = Arrays.stream(baseCorners).map(evilTower::offsetLocation).toArray(Location[]::new);
            PortalManager portalManager = multiversePortals.getPortalManager();
            MVWorldManager worldManager = multiversePortals.getCore().getMVWorldManager();
            PortalLocation portalLocation = new PortalLocation(corners[0].toVector(), corners[1].toVector(), worldManager.getMVWorld(corners[0].getWorld()));
            portalManager.addPortal(new MVPortal(multiversePortals, teamPortalName, "Mystievous", portalLocation));
            MVPortal portal = portalManager.getPortal(teamPortalName);
            portal.setPortalLocation(portalLocation);
            portal.setExactDestination(destinationLocation);
        }
    }

}
