package io.github.mystievous.towerchallenge.misc;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.geco.gsit.api.event.PreEntitySitEvent;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SitEventHandler implements Listener {
    public static final String REGION_NAME = "interview-stage";
    public static final String HOT_TUB_NAME = "hot-tub";

    private ProtectedRegion region;
    private ProtectedRegion hotTub;

    public SitEventHandler() {
        try {
            RegionManager regionManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.Dec2022()));
            if (regionManager != null) {
                if (regionManager.hasRegion(REGION_NAME)) {
                    region = regionManager.getRegion(REGION_NAME);
                }
                if (regionManager.hasRegion(HOT_TUB_NAME)) {
                    hotTub = regionManager.getRegion(HOT_TUB_NAME);
                }
            }
        } catch (NullPointerException e) {
            Bukkit.getLogger().warning("Unable to get Sitting World Guard Region");
        }
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    @EventHandler
    public void onSit(PreEntitySitEvent event) {
        if (event.isCancelled())
            return;
        try {
            RegionManager regionManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.Dec2022()));
            if (regionManager != null) {
                if (regionManager.hasRegion(REGION_NAME)) {
                    region = regionManager.getRegion(REGION_NAME);
                }
                if (regionManager.hasRegion(HOT_TUB_NAME)) {
                    hotTub = regionManager.getRegion(HOT_TUB_NAME);
                }
            }
        } catch (NullPointerException e) {
            Bukkit.getLogger().warning("Unable to get Sitting World Guard Region");
        }
        if (region == null ||
                (!(region.contains(BukkitAdapter.adapt(event.getBlock().getLocation()).toVector().toBlockPoint())) &&
                        !(hotTub.contains(BukkitAdapter.adapt(event.getBlock().getLocation()).toVector().toBlockPoint())))) {
            event.setCancelled(true);
        }
    }

}
