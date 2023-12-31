package io.github.mctowerchallenge.mctcplugin.portal;

import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.quest.QuestUtil;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Orientable;

/**
 * Manages the logic for the Nether portal in the game.
 */
public class NetherPortal {

    /*
        Portal layers:
        Negative to Positive for all axes
     */
    private final Location[][] portalLayers;

    /**
     * Creates a NetherPortal instance and initializes the portal block locations.
     */
    public NetherPortal() {
        portalLayers = new Location[][]{{
                new Location(Worlds.Jan2024(), -1379, 69, -478),
                new Location(Worlds.Jan2024(), -1379, 71, -478)
        }};
    }

    /**
     * Opens the Nether portal by setting the portal blocks in the specified bounds
     * and configuring their orientation.
     */
    public void openPortal() {
        Orientable portalData = (Orientable) Bukkit.createBlockData(Material.NETHER_PORTAL);
        portalData.setAxis(Axis.Z);
        for (Location[] layer : portalLayers) {
            QuestUtil.fillArea(layer, portalData);
        }
    }

    /**
     * Resets the Nether portal by clearing the portal blocks.
     */
    public void resetPortal() {
        for (Location[] layer : portalLayers) {
            QuestUtil.fillArea(layer, Material.AIR);
        }
    }

}
