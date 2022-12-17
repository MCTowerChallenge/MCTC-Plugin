package io.github.mystievous.towerchallenge.teleports;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gods.GodManager;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.Openable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TeleportHistoryManager implements Listener, Openable {

    private final Map<UUID, List<TeleportLocation>> teleports;

    private GodManager godManager;

    public TeleportHistoryManager(GodManager godManager) {
        this.godManager = godManager;
        teleports = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
        new BackCommand(this);
    }

    public Map<UUID, List<TeleportLocation>> getTeleports() {
        return teleports;
    }

    public List<TeleportLocation> get(OfflinePlayer player) {
        return teleports.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    @Nullable
    public TeleportLocation getLastLocation(OfflinePlayer player) {
        List<TeleportLocation> locations = get(player);
        return locations.size() > 0 ? locations.get(locations.size()-1) : null;
    }

    @Override
    public Gui getGui(Player player) {
        return new TeleportHistoryOverviewGui(this, godManager.getGodGui().getGui(player));
    }

    private void addTeleport(Player player, TeleportLocation location) {
        List<TeleportLocation> teleportLocations = teleports.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (teleportLocations.size() >= 56) {
            teleportLocations.remove(teleportLocations.size()-1);
        }
        teleportLocations.add(location);
        teleports.put(player.getUniqueId(), teleportLocations);
//        Bukkit.getLogger().info("Player " + player.getName() + " was teleported!");
//        Bukkit.getLogger().info("New teleports: ");
//        for (TeleportLocation location1 : get(player)) {
//            Bukkit.getLogger().info(location1.getX() + " " + location1.getY() + " " + location1.getZ());
//        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled())
            return;
        TeleportLocation.Reason reason;
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            reason = TeleportLocation.Reason.PEARL;
        } else if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL) ||
                event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_GATEWAY) ||
                event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            reason = TeleportLocation.Reason.PORTAL;
        } else {
            reason = TeleportLocation.Reason.TELEPORT;
        }
        Player player = event.getPlayer();
        addTeleport(player, new TeleportLocation(player.getLocation(), reason));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        addTeleport(player, new TeleportLocation(player.getLocation(), TeleportLocation.Reason.DEATH));
    }

}
