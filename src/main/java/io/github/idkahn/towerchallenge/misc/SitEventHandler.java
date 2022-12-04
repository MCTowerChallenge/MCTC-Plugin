package io.github.idkahn.towerchallenge.misc;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.geco.gsit.api.event.PreEntitySitEvent;
import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SitEventHandler implements Listener {
    public static String REGION_NAME = "interview-stage";

    private ProtectedRegion region;

    public SitEventHandler() {
        try {
            RegionManager regionManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(TowerChallenge.WORLD()));
            if (regionManager != null && regionManager.hasRegion(REGION_NAME)) {
                region = regionManager.getRegion(REGION_NAME);
            }
        } catch (NullPointerException e) {
            Bukkit.getLogger().warning("Unable to get Sitting World Guard Region");
        }
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
    }

    @EventHandler
    public void onSit(PreEntitySitEvent event) {
        if (event.isCancelled())
            return;
        if (region == null) {
            try {
                RegionManager regionManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(TowerChallenge.WORLD()));
                if (regionManager != null && regionManager.hasRegion(REGION_NAME)) {
                    region = regionManager.getRegion(REGION_NAME);
                }
            } catch (NullPointerException e) {
                Bukkit.getLogger().warning("Unable to get Sitting World Guard Region");
            }
        }
        if (region == null || !(region.contains(BukkitAdapter.adapt(event.getBlock().getLocation()).toVector().toBlockPoint()))) {
            event.setCancelled(true);
        }
    }

}
