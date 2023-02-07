package io.github.mystievous.towerchallenge.quests.entities;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class NPC implements Listener {

    private final TeamManager teamManager;
    private final String tag;
    private final Map<String, Consumer<PlayerInteractAtEntityEvent>> questHandlers;
    private Consumer<PlayerInteractAtEntityEvent> defaultHandler = null;
    private final Set<String> allowedRegions = new HashSet<>();
    private final Set<String> disallowedRegions = new HashSet<>();

    public NPC(TeamManager teamManager, String tag) {
        this.teamManager = teamManager;
        this.tag = tag;
        this.questHandlers = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    public void addQuestHandler(String quest, Consumer<PlayerInteractAtEntityEvent> handler) {
        questHandlers.put(quest, handler);
    }

    public void setDefaultHandler(Consumer<PlayerInteractAtEntityEvent> defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    public void addAllowedRegion(String regionName) {
        allowedRegions.add(regionName);
    }

    public String getTag() {
        return tag;
    }

    public void addDisallowedRegion(String regionName) {
        disallowedRegions.add(regionName);
    }

    public boolean hasTag(Entity entity) {
        return entity.getScoreboardTags().contains(tag);
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractAtEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (entity.getScoreboardTags().contains(tag)) {
            if (team == null) {
                player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
                return;
            }
            Consumer<PlayerInteractAtEntityEvent> consumer = questHandlers.get(team.getCurrentQuestId());
            if (consumer != null) {
                consumer.accept(event);
            } else if (defaultHandler != null) {
                defaultHandler.accept(event);
            }
        }
    }

    @EventHandler
    public void onEntityPath(final EntityPathfindEvent event) {
        if (event.isCancelled())
            return;

        Entity entity = event.getEntity();
        Location location = event.getLoc();

        if (hasTag(entity) && !allowedRegions.isEmpty()) {
            event.setCancelled(true);
            RegionManager worldContainer = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.WORLD()));
            if (worldContainer != null) {
                ApplicableRegionSet regionSet = worldContainer.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
                for (ProtectedRegion region : regionSet.getRegions()) {
                    for (String disallowedRegion : disallowedRegions) {
                        if (region.getId().matches(disallowedRegion)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                for (ProtectedRegion region : regionSet.getRegions()) {
                    for (String allowedRegion : allowedRegions) {
                        if (region.getId().matches(allowedRegion)) {
                            event.setCancelled(false);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(final EntityTargetEvent event) {
        if (event.isCancelled())
            return;

        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (hasTag(entity) && target instanceof HumanEntity) {
            event.setCancelled(true);
        }
    }

}
