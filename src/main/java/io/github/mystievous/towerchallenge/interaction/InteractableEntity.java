package io.github.mystievous.towerchallenge.interaction;

import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents an interactable entity with associated interaction handlers
 */
public abstract class InteractableEntity {

    private final Map<String, BiConsumer<TowerTeam, PlayerInteractEntityEvent>> questInteractionHandlers;
    private BiConsumer<TowerTeam, PlayerInteractEntityEvent> defaultInteractionHandler;

    /**
     * Constructs a new InteractableEntity instance.
     */
    public InteractableEntity() {
        questInteractionHandlers = new HashMap<>();
        defaultInteractionHandler = null;
    }

    /**
     * Adds a quest-specific interaction handler for this interactable entity.
     * <p>
     * These handlers are executed asynchronously. If the handler requires
     * synchronous operations, ensure to use {@link BukkitScheduler#runTask(Plugin, Runnable)} explicitly.
     *
     * @param quest   The quest required for the handler to be triggered.
     * @param handler The consumer to execute when a player interacts
     *                with the entity while the quest is active.
     */
    public void addQuestInteractionHandler(@NotNull String quest, BiConsumer<TowerTeam, PlayerInteractEntityEvent> handler) {
        questInteractionHandlers.put(quest, handler);
    }

    /**
     * Adds a default interaction handler for this interactable entity.
     * <p>
     * This handler is executed asynchronously. If the handler requires
     * synchronous operations, ensure to use {@link BukkitScheduler#runTask(Plugin, Runnable)} explicitly.
     *
     * @param defaultInteractionHandler The consumer to execute when a player interacts
     *                                  with the entity while no matching quest is active.
     */
    public void setDefaultInteractionHandler(BiConsumer<TowerTeam, PlayerInteractEntityEvent> defaultInteractionHandler) {
        this.defaultInteractionHandler = defaultInteractionHandler;
    }

    /**
     * Executes the default handler for the provided event.
     *
     * @param event The PlayerInteractEntityEvent triggering the handler.
     */
    public void runInteractionHandler(PlayerInteractEntityEvent event) {
        runInteractionHandler(null, event);
    }

    /**
     * Executes the appropriate handler for the provided event and quest.
     * If a quest-specific handler is found for the given quest, it will be executed.
     * Otherwise, the default handler will be executed.
     *
     * @param team  The team that triggered the handler.
     * @param event The PlayerInteractEntityEvent triggering the handler.
     */
    public void runInteractionHandler(@Nullable TowerTeam team, PlayerInteractEntityEvent event) {
        String quest = null;
        if (team != null) {
            quest = team.getCurrentQuestTag();
        }
        BiConsumer<TowerTeam, PlayerInteractEntityEvent> handler = questInteractionHandlers.getOrDefault(quest, defaultInteractionHandler);
        if (handler != null) {
            handler.accept(team, event);
        }
    }
}
