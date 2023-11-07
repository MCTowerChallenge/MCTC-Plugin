package io.github.mystievous.towerchallenge.quest.requirement;

import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RequirementsQuest extends Quest {

    private final List<Requirement> requirements;

    public RequirementsQuest(TowerChallenge plugin, String id, String friendlyName) {
        super(plugin, id, friendlyName);
        requirements = new ArrayList<>();
    }

    @Override
    public Quest copy() {
        RequirementsQuest quest = new RequirementsQuest(plugin, getTag(), getFriendlyName());
        quest.setDescription(getDescription());
        for (Requirement requirement : requirements) {
            quest.addRequirement(requirement.copy());
        }
        return quest;
    }

    public void addRequirement(Requirement requirement) {
        requirements.add(requirement);
    }

    public void turnIn(ItemStack item) {
        for (Requirement requirement : requirements) {
            requirement.turnIn(item);
        }
    }

    /**
     * Checks whether all {@link Requirement}'s
     * for this quest have nothing remaining.
     *
     * @return True, if all requirements
     *         are complete
     */
    public boolean isComplete() {
        for (Requirement requirement : requirements) {
            if (requirement.getRemaining() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Gui getGui(Player player) {
        return new RequirementQuestGui(plugin, getFriendlyName(), getDescription(), requirements);
    }

}
