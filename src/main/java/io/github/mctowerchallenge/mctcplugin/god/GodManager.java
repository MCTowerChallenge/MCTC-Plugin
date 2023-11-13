package io.github.mctowerchallenge.mctcplugin.god;

import io.github.mctowerchallenge.mctcplugin.god.godgui.GodGui;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.decoration.customblock.CustomBlockManager;
import io.github.mctowerchallenge.mctcplugin.portal.PortalControllers;
import io.github.mctowerchallenge.mctcplugin.teleport.TeleportHistoryManager;
import org.bukkit.Bukkit;

/**
 * Manages various functionalities related to Gods in the Tower Challenge plugin.
 */
public class GodManager {

    private final GodGui godGui;

    /**
     * Initializes a new instance of the GodManager class.
     *
     * @param plugin              The main plugin instance.
     * @param teamManager         The manager for teams.
     * @param portalControllers   The manager for portal controllers.
     */
    public GodManager(MCTCPlugin plugin, CustomBlockManager customBlockManager, TeamManager teamManager, PortalControllers portalControllers) {
        TeleportHistoryManager teleportHistoryManager = new TeleportHistoryManager(plugin, this);
        godGui = new GodGui(plugin, this, customBlockManager, teleportHistoryManager, teamManager, portalControllers);

        GodMenuCommand command = new GodMenuCommand(this);
        Bukkit.getPluginCommand("godmenu").setExecutor(command);
    }

    /**
     * Gets the God Utility GUI.
     *
     * @return The God Utility GUI.
     */
    public GodGui getGodGui() {
        return godGui;
    }
}
