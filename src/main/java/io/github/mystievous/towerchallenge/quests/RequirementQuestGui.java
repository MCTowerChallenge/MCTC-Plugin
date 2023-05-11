package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RequirementQuestGui extends QuestGui {

    public static final int COL_START = 6;

    public RequirementQuestGui(TowerChallenge plugin, String name, @Nullable String body, List<? extends Requirement> requirements) {
        super(plugin, name, body);

        for (int i = 0; i < requirements.size(); i++) {
            Requirement requirement = requirements.get(i);

            int row = Math.floorDiv(i, 4)+1;
            int col = i-(4*row)+(COL_START -1)+5;

            placeElement(row, col, new Element(requirement.getRepresentation()));
        }

    }
}
