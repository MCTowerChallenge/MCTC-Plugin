package io.github.mctowerchallenge.mctcplugin.utility;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.PortalLocation;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class MVPortalUtils {

    /**
     * Creates/Updates a {@link MVPortal} for this instance
     *
     * @param portalName          Name of the portal. Must be unique across the server!
     * @param corners             Corners of the portal in the base instance.
     * @param destinationLocation Absolute location on the server to teleport the player to.
     */
    public static @Nullable MVPortal initPortal(String portalName, Location[] corners, Location destinationLocation) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Portals") instanceof MultiversePortals multiversePortals) {
            PortalManager portalManager = multiversePortals.getPortalManager();
            MVWorldManager worldManager = multiversePortals.getCore().getMVWorldManager();
            PortalLocation portalLocation = new PortalLocation(corners[0].toVector(), corners[1].toVector(), worldManager.getMVWorld(corners[0].getWorld()));
            portalManager.addPortal(new MVPortal(multiversePortals, portalName, "Mystievous", portalLocation));
            MVPortal portal = portalManager.getPortal(portalName);
            portal.setPortalLocation(portalLocation);
            portal.setExactDestination(destinationLocation);
            return portal;
        }
        return null;
    }

}
