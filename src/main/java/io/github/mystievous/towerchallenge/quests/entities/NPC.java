package io.github.mystievous.towerchallenge.quests.entities;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
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

    private String tag;
    private Map<String, Consumer<PlayerInteractAtEntityEvent>> questHandlers;
    private Set<String> allowedRegions = new HashSet<>();
    private Set<String> disallowedRegions = new HashSet<>();

    private boolean isPassive;

    public NPC(String tag) {
        this.tag = tag;
        this.isPassive = true;
        this.questHandlers = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
    }

    public void addQuestHandler(String quest, Consumer<PlayerInteractAtEntityEvent> handler) {
        questHandlers.put(quest, handler);
    }

    public void setPassive(boolean passive) {
        isPassive = passive;
    }

    public Set<String> getAllowedRegions() {
        return allowedRegions;
    }

    public void setAllowedRegions(Set<String> pathRegions) {
        this.allowedRegions = pathRegions;
    }

    public void addAllowedRegion(String regionName) {
        allowedRegions.add(regionName);
    }

    public Set<String> getDisallowedRegions() {
        return disallowedRegions;
    }

    public String getTag() {
        return tag;
    }

    public void setDisallowedRegions(Set<String> disallowedRegions) {
        this.disallowedRegions = disallowedRegions;
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
        TowerTeam team = TowerChallenge.me.getChallengeManager().getPlayerTeam(player);
        if (entity.getScoreboardTags().contains(tag)) {
            if (team == null) {
                player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
                return;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamDataConfigFile);
            Consumer<PlayerInteractAtEntityEvent> consumer = questHandlers.get(config.getString(team.getTextName()+".CurrentQuest"));
            if (consumer != null) {
                consumer.accept(event);
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
            RegionManager worldContainer = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(TowerChallenge.WORLD()));
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

        if (isPassive && hasTag(entity) && target instanceof HumanEntity) {
            event.setCancelled(true);
        }
    }

}
