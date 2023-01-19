package io.github.mystievous.towerchallenge.eventspecific.valentines;


import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.event.MVPortalEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Manages the Evil Tower logic
 * for the Feb2023 event.
 * <p>
 * This mainly handles teleporting
 * players to their corresponding towers.
 */
public class EvilTower implements Listener {

    public EvilTower() {
        MultiversePortals portals = (MultiversePortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Portals");

    }

    @EventHandler
    public void onPortal(final MVPortalEvent event) {

    }

}
