package io.github.mystievous.towerchallenge.interaction;

import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents an interactable entity tag with associated handlers.
 */
public class InteractableTaggedEntity extends InteractableEntity {

    private final String tag;

    /**
     * Constructs a new InteractableEntityTag instance with the given tag.
     *
     * @param tag The tag associated with this interactable entity.
     */
    public InteractableTaggedEntity(String tag) {
        this.tag = tag;
    }

    /**
     * Gets the tag associated with this InteractableTaggedEntity instance.
     *
     * @return The tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Checks if the provided entity has a matching tag.
     *
     * @param entity The entity to check.
     * @return True if the entity's tags contain the associated tag, false otherwise.
     */
    public boolean hasMatchingTag(Entity entity) {
        return entity.getScoreboardTags().contains(getTag());
    }

}
