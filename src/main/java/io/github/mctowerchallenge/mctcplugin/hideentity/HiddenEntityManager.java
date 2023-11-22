package io.github.mctowerchallenge.mctcplugin.hideentity;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class HiddenEntityManager implements Listener {

    public static final String PLAYERS_TO_SHOW = "players_to_show";

    public static final NamespacedKey PLAYERS_KEY = new NamespacedKey(MCTCPlugin.getInstance(), PLAYERS_TO_SHOW);

    public static final Map<UUID, Location> hiddenEntities = new HashMap<>();

    public static void register(Entity entity) {
        hiddenEntities.put(entity.getUniqueId(), entity.getLocation());
        refreshHiddenState(entity);
    }

    public static void unregister(Entity entity) {
        hiddenEntities.remove(entity.getUniqueId());
        resetShownPlayers(entity);
    }

    public static void resetShownPlayers(Entity entity) {
        NBTUtils.setUUIDSet(PLAYERS_KEY, entity, null);
        refreshHiddenState(entity);
    }

    public static Set<UUID> loadAndGetPlayers(Entity entity) {
        Location location = hiddenEntities.get(entity.getUniqueId());
        if (location != null) {
            location.getChunk().load();
        }
        return NBTUtils.getUUIDSet(PLAYERS_KEY, entity);
    }

    public static void showToPlayer(Entity entity, Player player) {
        Set<UUID> shownPlayers = loadAndGetPlayers(entity);
        shownPlayers.add(player.getUniqueId());
        NBTUtils.setUUIDSet(PLAYERS_KEY, entity, shownPlayers);
        player.showEntity(MCTCPlugin.getInstance(), entity);
    }

    public static void hideFromPlayer(Entity entity, Player player) {
        Set<UUID> shownPlayers = loadAndGetPlayers(entity);
        shownPlayers.remove(player.getUniqueId());
        NBTUtils.setUUIDSet(PLAYERS_KEY, entity, shownPlayers);
        player.hideEntity(MCTCPlugin.getInstance(), entity);
    }

    public static void refreshPlayerShown(Player player) {
        for (Map.Entry<UUID, Location> entry : hiddenEntities.entrySet()) {
            entry.getValue().getChunk().load();
            Entity entity = Bukkit.getEntity(entry.getKey());
            if (entity == null) {
                continue;
            }
            Set<UUID> shownPlayers = NBTUtils.getUUIDSet(PLAYERS_KEY, entity);
            if (shownPlayers.contains(player.getUniqueId())) {
                player.showEntity(MCTCPlugin.getInstance(), entity);
            } else {
                player.hideEntity(MCTCPlugin.getInstance(), entity);
            }
        }
    }

    public static void refreshHiddenState(Entity entity) {
        Set<UUID> shownPlayers = loadAndGetPlayers(entity);
        if (shownPlayers == null) {
            shownPlayers = new HashSet<>();
            NBTUtils.setUUIDSet(PLAYERS_KEY, entity, shownPlayers);
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (shownPlayers.contains(player.getUniqueId())) {
                player.showEntity(MCTCPlugin.getInstance(), entity);
            } else {
                player.hideEntity(MCTCPlugin.getInstance(), entity);
            }
        }
    }

    public static void refreshAllEntities() {
        for (Map.Entry<UUID, Location> entry : hiddenEntities.entrySet()) {
            entry.getValue().getChunk().load();
            Entity entity = Bukkit.getEntity(entry.getKey());
            if (entity != null) {
                refreshHiddenState(entity);
            }
        }
    }

    public HiddenEntityManager() {
        Bukkit.getPluginManager().registerEvents(this, MCTCPlugin.getInstance());
    }

    /**
     * Handle player join event.
     *
     * @param event The PlayerJoinEvent.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        refreshPlayerShown(event.getPlayer());
    }
}
