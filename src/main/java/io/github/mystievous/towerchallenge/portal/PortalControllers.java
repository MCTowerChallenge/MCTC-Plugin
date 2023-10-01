package io.github.mystievous.towerchallenge.portal;

import io.github.mystievous.towerchallenge.team.TeamManager;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * Manages instances of both the EndPortal and NetherPortal classes, providing access to their functionalities.
 */
public class PortalControllers {

    private final EndPortal endPortal;
    private final NetherPortal netherPortal;

    /**
     * Constructs a new PortalControllers instance.
     *
     * @param plugin The plugin instance for event registration and access to the server.
     * @param teamManager The team manager instance to be used by the EndPortal.
     */
    public PortalControllers(Plugin plugin, TeamManager teamManager) {
        this.endPortal = new EndPortal(plugin, teamManager);
        this.netherPortal = new NetherPortal();
    }

    /**
     * Gets the EndPortal instance managed by this controller.
     *
     * @return The EndPortal instance.
     */
    public EndPortal getEndPortal() {
        return endPortal;
    }

    /**
     * Gets the NetherPortal instance managed by this controller.
     *
     * @return The NetherPortal instance.
     */
    public NetherPortal getNetherPortal() {
        return netherPortal;
    }

    public static Vector randomPointInFlatRing(Vector center, double minimumRadius, double maximumRadius) {
        double r = Math.sqrt(Math.random() * (Math.pow(maximumRadius, 2) - Math.pow(minimumRadius, 2)) + Math.pow(minimumRadius, 2));
        double theta = Math.random() * 2 * Math.PI;
        double x = center.getX() + r * Math.cos(theta);
        double z = center.getZ() + r * Math.sin(theta);
        return new Vector(x, center.getY(), z);
    }

}
