package io.github.mystievous.towerchallenge.portal;

import io.github.mystievous.towerchallenge.Worlds;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;
import org.bukkit.util.Vector;

/**
 * Manages the logic for the Nether portal in the game.
 */
public class NetherPortal {

    /*
        Portal layers:
        Negative to Positive for all axes
     */
    private final Vector[][] portalBlocks;

    /**
     * Creates a NetherPortal instance and initializes the portal block locations.
     */
    public NetherPortal() {
        portalBlocks = new Vector[][]{
                {new Vector(141, 69, -2222), new Vector(141, 71, -2222)}
        };
    }

    /**
     * Opens the Nether portal by setting the portal blocks in the specified bounds
     * and configuring their orientation.
     */
    public void openPortal() {
        // Loop through portal layers and block positions to place and orient portal blocks
        for (Vector[] layer : portalBlocks) {
            for (int x = layer[0].getBlockX(); x <= layer[1].getBlockX(); x++) {
                for (int y = layer[0].getBlockY(); y <= layer[1].getBlockY(); y++) {
                    for (int z = layer[0].getBlockZ(); z <= layer[1].getBlockZ(); z++) {
                        Location location = new Location(Worlds.Jun2023(), x, y, z);
                        Block block = location.getBlock();
                        block.setType(Material.NETHER_PORTAL);
                        Orientable blockData = (Orientable) block.getBlockData();
                        blockData.setAxis(Axis.Z);
                        block.setBlockData(blockData);
                    }
                }
            }
        }
    }

    /**
     * Resets the Nether portal by clearing the portal blocks.
     */
    public void resetPortal() {
        // Loop through portal layers and block positions to remove portal blocks
        for (Vector[] layer : portalBlocks) {
            for (int x = layer[0].getBlockX(); x <= layer[1].getBlockX(); x++) {
                for (int y = layer[0].getBlockY(); y <= layer[1].getBlockY(); y++) {
                    for (int z = layer[0].getBlockZ(); z <= layer[1].getBlockZ(); z++) {
                        Location location = new Location(Worlds.Jun2023(), x, y, z);
                        Block block = location.getBlock();
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

}
