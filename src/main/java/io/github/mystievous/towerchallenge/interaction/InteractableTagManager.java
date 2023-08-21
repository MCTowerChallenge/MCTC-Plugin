package io.github.mystievous.towerchallenge.interaction;

import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages interactable entity tags and handles player interactions with them.
 */
public class InteractableTagManager implements Listener {

    /** Holds the registered interactable entity tags. */
    private static final Map<String, InteractableTaggedEntity> registeredTags = new HashMap<>();

    /**
     * Registers an interactable entity tag.
     *
     * @param entityTag The interactable entity tag to register.
     */
    public static void registerTag(InteractableTaggedEntity entityTag) {
        registeredTags.put(entityTag.getTag(), entityTag);
    }

    /**
     * Retrieves the InteractableEntityTag associated with the provided entity.
     *
     * @param entity The entity to check for associated tags.
     * @return The associated InteractableEntityTag, or null if not found.
     */
    public static @Nullable InteractableTaggedEntity getEntityTag(Entity entity) {
        InteractableTaggedEntity eventEntityTag = null;
        for (String tag : entity.getScoreboardTags()) {
            InteractableTaggedEntity entityTag = registeredTags.get(tag);
            if (entityTag != null) {
                eventEntityTag = entityTag;
                break;
            }
        }
        return eventEntityTag;
    }

    private final Plugin plugin;
    private final TeamManager teamManager;

    /**
     * Constructs an InteractableTagManager instance.
     *
     * @param plugin      The plugin instance.
     * @param teamManager The team manager instance.
     */
    public InteractableTagManager(Plugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Checks if a player is interacting with an NPC and runs the appropriate handler asynchronously.
     *
     * @param event The player interaction event to check.
     */
    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
                return;
            }

            Entity entity = event.getRightClicked();
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);

            InteractableTaggedEntity entityTag = getEntityTag(entity);

            if (entityTag == null || team == null) {
                return;
            }

            entityTag.runInteractionHandler(team, event);
        });
    }

}
