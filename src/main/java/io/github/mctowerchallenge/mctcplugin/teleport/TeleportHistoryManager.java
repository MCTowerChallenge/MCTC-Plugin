package io.github.mctowerchallenge.mctcplugin.teleport;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.god.GodManager;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages teleport locations and history for players in the Tower Challenge plugin.
 */
public class TeleportHistoryManager implements Listener, Openable {

    private final Map<UUID, List<TeleportLocation>> teleports;

    private final JavaPlugin plugin;
    private final GodManager godManager;

    /**
     * Initializes a new instance of the TeleportHistoryManager class.
     *
     * @param plugin     The main plugin instance.
     * @param godManager The manager for God-related functionalities.
     */
    public TeleportHistoryManager(JavaPlugin plugin, GodManager godManager) {
        this.plugin = plugin;
        this.godManager = godManager;
        teleports = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, MCTCPlugin.getInstance());
        new BackCommand(this);
    }

    /**
     * Gets the map containing players' teleport history.
     *
     * @return The map of teleport history.
     */
    public Map<UUID, List<TeleportLocation>> getTeleports() {
        return teleports;
    }

    /**
     * Gets the teleport history for a specific player.
     *
     * @param player The player to retrieve teleport history for.
     * @return The list of teleport locations for the player.
     */
    public List<TeleportLocation> get(OfflinePlayer player) {
        return teleports.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    /**
     * Gets the last location a player teleported from.
     *
     * @param player The player to check.
     * @return The last teleport location.
     */
    @Nullable
    public TeleportLocation getLastLocation(OfflinePlayer player) {
        List<TeleportLocation> locations = get(player);
        return !locations.isEmpty() ? locations.get(locations.size() - 1) : null;
    }

    @Override
    public Gui getGui(Player player) {
        return new TeleportHistoryOverviewGui(plugin, this, godManager.getGodGui().getGui(player));
    }

    /**
     * Adds a teleport location to a player's teleport history.
     *
     * @param player   The player who teleported.
     * @param location The teleport location to add.
     */
    private void addTeleport(Player player, TeleportLocation location) {
        List<TeleportLocation> teleportLocations = teleports.getOrDefault(player.getUniqueId(), new ArrayList<>());
        teleportLocations.add(location);
        teleports.put(player.getUniqueId(), teleportLocations);
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
        addTeleport(player, new TeleportLocation(player.getLocation(), reason, event.getCause()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        addTeleport(player, new TeleportLocation(player.getLocation(), TeleportLocation.Reason.DEATH, PlayerTeleportEvent.TeleportCause.UNKNOWN));
    }

}
