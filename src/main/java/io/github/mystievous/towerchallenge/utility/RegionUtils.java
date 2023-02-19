package io.github.mystievous.towerchallenge.utility;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class RegionUtils {

    public static ProtectedRegion upsertRegion(String regionName, Location pos1, Location pos2, String parentName) {
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

}
