package io.github.idkahn.towerchallenge.Towering;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public class TowerBlock {

    private Location location;
    private Material blockType;

    public TowerBlock(World world, double x, double y, double z, Material blockType) {
        this.location = new Location(world, x, y, z);
        this.blockType = blockType;
    }

    public TowerBlock(Location location, Material blockType) {
        this.location = location;
        this.blockType = blockType;
    }

    public TowerBlock(Block block) {
        this.location = block.getLocation();
        this.blockType = block.getType();
    }

    public Block getBlock() {
        return location.getBlock();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getBlockType() {
        return blockType;
    }

    public void setBlockType(Material blockType) {
        this.blockType = blockType;
    }
}
