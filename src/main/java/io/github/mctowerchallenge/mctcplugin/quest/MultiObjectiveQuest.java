package io.github.mctowerchallenge.mctcplugin.quest;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.PresetGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiObjectiveQuest extends Quest {

    private final Map<String, Quest> subQuests;

    /**
     * Creates a new quest.
     *
     * @param plugin       The current plugin instance.
     * @param tag          The unique ID for this quest, matching a database entry.
     * @param friendlyName The user-friendly name of the quest for display.
     */
    public MultiObjectiveQuest(MCTCPlugin plugin, String tag, String friendlyName) {
        super(plugin, tag, friendlyName);
        subQuests = new HashMap<>();
    }

    @Override
    public Quest copy() {
        MultiObjectiveQuest quest = new MultiObjectiveQuest(plugin, getTag(), getFriendlyName());
        quest.setCompleted(isCompleted());
        quest.setDescription(getDescription());
        for (Map.Entry<String, Quest> entry : subQuests.entrySet()) {
            quest.addSubQuest(entry.getValue().copy());
        }
        return quest;
    }

    public void addSubQuest(Quest quest) {
        subQuests.put(quest.getTag(), quest);
    }

    public @Nullable Quest getSubQuest(String tag) {
        return subQuests.get(tag);
    }

    public boolean tryComplete() {
        for (Quest quest : subQuests.values()) {
            if (!quest.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Gui getGui(Player player) {
        QuestGui gui = (QuestGui) super.getGui(player);
        Quest[] subQuestList = subQuests.values().toArray(Quest[]::new);
        for (int i = 0; i < subQuestList.length; i++) {
            Quest quest = subQuestList[i];
            ItemStack representation = quest.getRepresentation();
            if (quest.isCompleted()) {
                representation = GuiUtil.formatItem(TextUtil.getItemName(representation), Material.PAPER, 1);
            }
            ButtonElement element = new ButtonElement(representation, player1 -> {
                quest.getGui(player1).openInventory(player1);
            });
            gui.placeElement((int) (Math.floor((float) i / 4)+1), ((i % 4) + 5) + 1, element);
        }
        return gui;
    }

}
