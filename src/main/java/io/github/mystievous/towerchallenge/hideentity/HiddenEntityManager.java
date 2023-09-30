package io.github.mystievous.towerchallenge.hideentity;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
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

    public static final Map<UUID, Location> hiddenEntities = new HashMap<>();

    public static void register(Entity entity) {
        hiddenEntities.put(entity.getUniqueId(), entity.getLocation());
        refreshHiddenState(entity);
    }

    public static void showToPlayer(Entity entity, Player player) {
        Location location = hiddenEntities.get(entity.getUniqueId());
        if (location == null) {
            return;
        }
        location.getChunk().load();
        NamespacedKey key = new NamespacedKey(TowerChallenge.getInstance(), PLAYERS_TO_SHOW);
        Set<UUID> shownPlayers = NBTUtils.getUUIDSet(key, entity);
        shownPlayers.add(player.getUniqueId());
        NBTUtils.setUUIDSet(key, entity, shownPlayers);
        player.showEntity(TowerChallenge.getInstance(), entity);
    }

    public static void hideFromPlayer(Entity entity, Player player) {
        Location location = hiddenEntities.get(entity.getUniqueId());
        if (location == null) {
            return;
        }
        location.getChunk().load();
        NamespacedKey key = new NamespacedKey(TowerChallenge.getInstance(), PLAYERS_TO_SHOW);
        Set<UUID> shownPlayers = NBTUtils.getUUIDSet(key, entity);
        shownPlayers.remove(player.getUniqueId());
        NBTUtils.setUUIDSet(key, entity, shownPlayers);
        player.hideEntity(TowerChallenge.getInstance(), entity);
    }

    public static void refreshPlayerShown(Player player) {
        for (Map.Entry<UUID, Location> entry : hiddenEntities.entrySet()) {
            entry.getValue().getChunk().load();
            Entity entity = Bukkit.getEntity(entry.getKey());
            if (entity == null) {
                continue;
            }
            NamespacedKey key = new NamespacedKey(TowerChallenge.getInstance(), PLAYERS_TO_SHOW);
            Set<UUID> shownPlayers = NBTUtils.getUUIDSet(key, entity);
            if (shownPlayers.contains(player.getUniqueId())) {
                player.showEntity(TowerChallenge.getInstance(), entity);
            } else {
                player.hideEntity(TowerChallenge.getInstance(), entity);
            }
        }
    }

    public static void refreshHiddenState(Entity entity) {
        Location location = hiddenEntities.get(entity.getUniqueId());
        if (location == null) {
            return;
        }
        location.getChunk().load();
        NamespacedKey key = new NamespacedKey(TowerChallenge.getInstance(), PLAYERS_TO_SHOW);
        Set<UUID> shownPlayers = NBTUtils.getUUIDSet(key, entity);
        if (shownPlayers == null) {
            shownPlayers = new HashSet<>();
            NBTUtils.setUUIDSet(key, entity, shownPlayers);
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (shownPlayers.contains(player.getUniqueId())) {
                player.showEntity(TowerChallenge.getInstance(), entity);
            } else {
                player.hideEntity(TowerChallenge.getInstance(), entity);
            }
        }
    }

    public HiddenEntityManager() {
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
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
