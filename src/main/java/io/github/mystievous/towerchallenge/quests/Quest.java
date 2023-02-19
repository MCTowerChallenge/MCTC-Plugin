package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.Openable;
import io.github.mystievous.towerchallenge.gui.page.PresetGui;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Quest implements Openable {

    private final TeamManager teamManager;
    private final String id;
    private final String friendlyName;
    private final List<QuestRequirement> requirements;
    private final List<QuestReward> rewards;
    private @Nullable String description;

    private Quest next;

    public Quest(TeamManager teamManager, String id, String friendlyName, List<QuestRequirement> requirements, List<QuestReward> rewards) {
        this.teamManager = teamManager;
        this.id = id;
        this.friendlyName = friendlyName;
        this.requirements = requirements;
        this.rewards = rewards;
        this.description = null;
    }

    public Quest(TeamManager teamManager, String id, String friendlyName) {
        this(teamManager, id, friendlyName, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Searches this quest for a requirement matching the given item
     *
     * @param itemStack the item to search for
     * @return the corresponding requirement, or null if there is none
     */
    public @Nullable QuestRequirement getRequirement(ItemStack itemStack) {
        return requirements.stream().filter(questRequirement -> questRequirement.getType().equals(itemStack.getType())).findFirst().orElse(null);
    }

    public String getId() {
        return id;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Quest getNext() {
        return next;
    }

    public void setNext(Quest next) {
        this.next = next;
    }

    public List<QuestRequirement> getRequirements() {
        return requirements;
    }

    public List<QuestReward> getRewards() {
        return rewards;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Checks the entire quest for completion
     *
     * @return true, if all requirements are fulfilled
     */
    public boolean isFulfilled() {
        for (QuestRequirement requirement : requirements) {
            if (!requirement.isFulfilled()) {
                return false;
            }
        }
        return true;
    }

    public Gui getGui(@NotNull TowerTeam team) {
        try {
            Map<String, Integer> objectiveScores = teamManager.getDatabase().getObjectives(team, id);
            for (QuestRequirement requirement : requirements) {
                requirement.setCurrentAmount(objectiveScores.getOrDefault(requirement.getType().name(), 0));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error reading database: " + e.getMessage());
        }

        QuestGui gui = new QuestGui(friendlyName, description);

//        gui.placeElement(1, 9, new ButtonElement(ButtonElement.exitItem(), player -> Bukkit.getScheduler().scheduleSyncDelayedTask(team.getPlugin(), player::closeInventory, 1)));

//        for (int i = 0; i < requirements.size(); i++) {
//            QuestRequirement requirement = requirements.get(i);
//            Element element = requirement.getRepresentation();
//            if (i < 7) {
//                gui.placeElement(2, i + 2, element);
//            } else if (i < 14) {
//                gui.placeElement(3, i - 7 + 2, element);
//            } else {
//                TowerChallenge.log("Quest has too many requirements");
//            }
//        }

//        for (int i = 0; i < rewards.size(); i++) {
//            QuestReward reward = rewards.get(i);
//            Element element = reward.getRepresentation();
//            if (i < 7) {
//                gui.placeElement(5, i + 2, element);
//            } else {
//                TowerChallenge.log("Quest has too many rewards");
//            }
//        }

        return gui;
    }

    @Override
    public Gui getGui(Player player) {
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (team == null) {
            return QuestManager.NO_QUEST_GUI;
        }
        return getGui(team);
    }
}
