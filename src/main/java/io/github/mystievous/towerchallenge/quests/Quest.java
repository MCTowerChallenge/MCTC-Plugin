package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Quest implements Openable {

    protected final TowerChallenge plugin;
    protected final TeamManager teamManager;
    private final String id;
    private final String friendlyName;
    private @Nullable String description;

    /**
     * Creates a new quest.
     *
     * @param plugin       The current plugin instance.
     * @param teamManager  The current team manager instance.
     * @param id           The {@link String} ID for this quest, must match database.
     * @param friendlyName The friendly name for this quest,
     *                     to show up in the questbook.
     */
    public Quest(TowerChallenge plugin, TeamManager teamManager, String id, String friendlyName) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.id = id;
        this.friendlyName = friendlyName;
        this.description = null;
    }

    /**
     * Copies this quest instance
     *
     * @return The new quest
     */
    public Quest copy() {
        Quest quest = new Quest(plugin, teamManager, id, friendlyName);
        quest.setDescription(description);
        return quest;
    }

    public String getId() {
        return id;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Sets the description,
     * shows up in the body
     * of the quest book
     *
     * @param description Description to set.
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public Gui getGui(Player player) {
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (team == null) {
            return QuestManager.NO_QUEST_GUI;
        }
        return new QuestGui(plugin, friendlyName, description);
    }
}
