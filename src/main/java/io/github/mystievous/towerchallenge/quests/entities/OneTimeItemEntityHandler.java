package io.github.mystievous.towerchallenge.quests.entities;

import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class OneTimeItemEntityHandler extends ItemEntityHandler {

    private String mainTag;

    public OneTimeItemEntityHandler(TeamManager teamManager, String mainTag, String entityTag, @Nullable String requiredQuest, ItemStack itemStack) {
        super(teamManager, entityTag, requiredQuest, itemStack);
        this.mainTag = mainTag;
    }

    @Override
    public boolean hasCollected(TowerTeam team, String check) throws SQLException {
        return getTeamManager().getDatabase().getObjective(team, mainTag, getTag()) != 0;
    }

    @Override
    public void setItemCollected(TowerTeam team, String check) throws SQLException {
        getTeamManager().getDatabase().setObjectiveScore(team, mainTag, getTag(), 1);
    }
}
