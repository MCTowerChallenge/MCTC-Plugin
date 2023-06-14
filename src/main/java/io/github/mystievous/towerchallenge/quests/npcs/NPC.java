package io.github.mystievous.towerchallenge.quests.npcs;

import com.destroystokyo.paper.event.entity.EndermanEscapeEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.function.Consumer;

public class NPC implements Listener {

    private final TeamManager teamManager;
    private final String name;
    private final String tag;
    private final Color nameColor;
    private final Color textColor;
    private final Map<String, Consumer<PlayerInteractAtEntityEvent>> questHandlers;
    private Consumer<PlayerInteractAtEntityEvent> defaultHandler = null;

    private final Set<String> allowedRegions = new HashSet<>();

    private final Set<String> disallowedRegions = new HashSet<>();

    /**
     * Initializes an NPC, takes effect for any entities with the matching TAG
     *
     * @param teamManager Current {@link TeamManager} instance
     * @param name        Name to show in chat messages
     * @param tag         Tag used to mark an entity as this NPC
     * @param nameColor   Color of the name in chat messages
     * @param textColor   Color of the speaking text in chat messages
     */
    public NPC(TeamManager teamManager, String name, String tag, Color nameColor, Color textColor) {
        this.teamManager = teamManager;
        this.name = name;
        this.tag = tag;
        this.nameColor = nameColor;
        this.textColor = textColor;
        this.questHandlers = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    /**
     * Initializes an NPC, takes effect for any entities with the matching TAG
     *
     * @param teamManager Current {@link TeamManager} instance
     * @param name        Name to show in chat messages
     * @param tag         Tag used to mark an entity as this NPC
     */
    public NPC(TeamManager teamManager, String name, String tag) {
        this(teamManager, name, tag, new Color(NamedTextColor.WHITE.value()), new Color(NamedTextColor.WHITE.value()));
    }

    /**
     * Add a click handler for when a player has a specific quest
     * <p></p>
     * These are run asynchronously, so if doing anything
     * in the handler that requires being synchronous,
     * make sure to explicitly run it with
     * {@link BukkitScheduler#runTask(Plugin, Runnable)}
     *
     * @param quest   The quest required
     * @param handler The consumer to apply when
     *                a player clicks with the
     *                quest active.
     */
    public void addQuestHandler(String quest, Consumer<PlayerInteractAtEntityEvent> handler) {
        questHandlers.put(quest, handler);
    }

    /**
     * Add a click handler to be applied by default.
     * Is overridden if a user has a
     * quest with a specific handler.
     * <p></p>
     * These are run asynchronously, so if doing anything
     * in the handler that requires being synchronous,
     * make sure to explicitly run it with
     * {@link BukkitScheduler#runTask(Plugin, Runnable)}
     *
     * @param defaultHandler The consumer to apply when a player clicks.
     */
    public void setDefaultHandler(Consumer<PlayerInteractAtEntityEvent> defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    /**
     * Add a region that the NPC is allowed to path within.
     * <p></p>
     * When none are set, the NPC is free to path wherever.
     * When one or more are set, it is constrained to those
     * regions.
     * <p>
     * Even within allowed regions, the NPC still cannot path into
     * regions added with {@link #addDisallowedRegion(String)}
     *
     * @param regex Regex pattern for region names to be matched with.
     */
    public void addAllowedRegion(String regex) {
        allowedRegions.add(regex);
    }

    public Component getName() {
        return Component.text(name).color(nameColor.toTextColor());
    }

    public String getTag() {
        return tag;
    }

    /**
     * Add a region that the NPC is disallowed from pathing within
     * <p></p>
     * This overrides regions allowed with {@link #addAllowedRegion(String)}
     *
     * @param regex Regex pattern for region names to be matched with.
     */
    public void addDisallowedRegion(String regex) {
        disallowedRegions.add(regex);
    }

    /**
     * Checks if an entity has a
     * matching TAG to this NPC
     *
     * @param entity The entity to check
     * @return True, if the entity matches this NPC
     */
    public boolean hasTag(Entity entity) {
        return entity.getScoreboardTags().contains(tag);
    }

    /**
     * Formats the given {@link Component} into
     * a chat message from this NPC.
     * <p></p>
     * Uses the colors specified in the constructor,
     * putting the name before the message similar
     * to a player's chat message.
     *
     * @param text The message to format.
     * @return The resulting component with the chat message.
     */
    public Component formatMessage(Component text) {
        return Component.text(String.format("<%s> ", name)).color(nameColor.toTextColor())
                .append(text.color(textColor.toTextColor()));
    }

    /**
     * Formats the given {@link String} into
     * a chat message from this NPC.
     * <p></p>
     * Uses the colors specified in the constructor,
     * putting the name before the message similar
     * to a player's chat message.
     *
     * @param text The message to format.
     * @return The resulting component with the chat message.
     */
    public Component formatMessage(String text) {
        return formatMessage(Component.text(text));
    }

    /**
     * Runs the default event handler of this NPC.
     *
     * @param event Click event to trigger handler with.
     */
    public void runDefaultHandler(PlayerInteractAtEntityEvent event) {
        if (defaultHandler != null) {
            defaultHandler.accept(event);
        }
    }

    /**
     * Checks if a player is interacting with this NPC,
     * then runs the proper handler for the context
     * asynchronously.
     *
     * @param event Click event to check
     */
    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractAtEntityEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(teamManager.getPlugin(), () -> {
            if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                return;
            }
            Entity entity = event.getRightClicked();
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (entity.getScoreboardTags().contains(tag)) {
                if (team != null) {
                    Consumer<PlayerInteractAtEntityEvent> consumer = questHandlers.get(team.getCurrentQuestTag());
                    if (consumer != null) {
                        consumer.accept(event);
                        return;
                    }
                    runDefaultHandler(event);
                }
            }
        });
    }

    /**
     * Checks if an entity trying to path
     * matches this NPC, then cancels the
     * event if it is outside of an allowed
     * region, or inside a disallowed region.
     *
     * @param event Event to check
     */
    @EventHandler
    public void onEntityPath(final EntityPathfindEvent event) {
        if (event.isCancelled())
            return;

        Entity entity = event.getEntity();
        Location location = event.getLoc();

        if (hasTag(entity)) {
            event.setCancelled(true);
            RegionManager worldContainer = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(location.getWorld()));
            if (worldContainer != null) {
                ApplicableRegionSet regionSet = worldContainer.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
                for (ProtectedRegion region : regionSet.getRegions()) {
                    for (String allowedRegion : allowedRegions) {
                        if (region.getId().matches(allowedRegion)) {
                            event.setCancelled(false);
                            break;
                        }
                    }
                    for (String disallowedRegion : disallowedRegions) {
                        if (region.getId().matches(disallowedRegion)) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Makes entities matching this
     * NPC passive towards players.
     *
     * @param event Event to check
     */
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
