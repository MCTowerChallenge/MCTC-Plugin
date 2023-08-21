package io.github.mystievous.towerchallenge.quest;

import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a quest in the Tower Challenge plugin.
 */
public class Quest implements Openable {

    protected final TowerChallenge plugin;
    private final String id;
    private final String friendlyName;
    private @Nullable String description;

    /**
     * Creates a new quest.
     *
     * @param plugin       The current plugin instance.
     * @param id           The unique ID for this quest, matching a database entry.
     * @param friendlyName The user-friendly name of the quest for display.
     */
    public Quest(TowerChallenge plugin, String id, String friendlyName) {
        this.plugin = plugin;
        this.id = id;
        this.friendlyName = friendlyName;
        this.description = null;
    }

    /**
     * Creates a copy of this quest instance.
     *
     * @return The copied quest.
     */
    public Quest copy() {
        Quest quest = new Quest(plugin, id, friendlyName);
        quest.setDescription(description);
        return quest;
    }

    /**
     * Gets the ID of the quest.
     *
     * @return The quest ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the user-friendly name of the quest.
     *
     * @return The friendly name of the quest.
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Sets the description of the quest.
     * This description appears in the body of the quest book.
     *
     * @param description The description to set.
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Gets the description of the quest.
     *
     * @return The description of the quest, or null if not set.
     */
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public Gui getGui(Player player) {
        return new QuestGui(plugin, friendlyName, description);
    }
}
