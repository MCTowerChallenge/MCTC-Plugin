package io.github.mctowerchallenge.mctcplugin.utility;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RegionUtils {

    /**
     * Creates a cuboid protected region in the world.
     * <p></p>
     * The world used is grabbed from {@code pos1}.
     *
     * @param regionName The name of the region.
     * @param pos1       The first boundary corner.
     * @param pos2       The second boundary corner.
     * @param parentName Name of the parent region,
     *                   or null if it has none.
     * @return The created region.
     */
    public static ProtectedRegion upsertRegion(String regionName, Location pos1, Location pos2, @Nullable String parentName) {
        WorldGuard worldGuard = WorldGuard.getInstance();
        World world = BukkitAdapter.adapt(pos1.getWorld());
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(world);

        if (regionManager != null) {

            ProtectedRegion region = new ProtectedCuboidRegion(regionName, BukkitAdapter.asBlockVector(pos1), BukkitAdapter.asBlockVector(pos2));
            if (regionManager.hasRegion(regionName)) {
                region.copyFrom(regionManager.getRegion(regionName));
            }

            if (parentName != null && regionManager.hasRegion(parentName)) {
                try {
                    region.setParent(regionManager.getRegion(parentName));
                } catch (ProtectedRegion.CircularInheritanceException e) {
                    Bukkit.getLogger().warning("Region Upsert Circular Inheritance: " + e.getMessage());
                }
            }

            regionManager.addRegion(region);
            return region;
        } else {
            throw new NullPointerException("Region Manager for " + world.getName() + " does not exist!");
        }
    }

    public static void loopThroughChunks(Location corner1, Location corner2, Consumer<Chunk> chunkConsumer) {
        org.bukkit.World world = corner1.getWorld();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; x += 16) {
            for (int z = minZ; z <= maxZ; z += 16) {
                Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
                chunkConsumer.accept(chunk);
            }
        }
    }

}
