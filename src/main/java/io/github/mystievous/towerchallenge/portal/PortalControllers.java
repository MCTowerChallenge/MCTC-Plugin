package io.github.mystievous.towerchallenge.portal;

import io.github.mystievous.towerchallenge.team.TeamManager;
import org.bukkit.plugin.Plugin;

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
}
