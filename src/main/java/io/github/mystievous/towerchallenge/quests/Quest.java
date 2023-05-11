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

    public Quest(TowerChallenge plugin, TeamManager teamManager, String id, String friendlyName) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.id = id;
        this.friendlyName = friendlyName;
        this.description = null;
    }

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
