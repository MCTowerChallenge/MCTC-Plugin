package io.github.mctowerchallenge.mctcplugin.quest;

import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.gui.Icons;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class QuestTableOfContents extends QuestGui {

    public QuestTableOfContents(MCTCPlugin plugin, String name, @Nullable String body, TowerTeam team) {
        super(plugin, name, body);
        Map<String, Quest> quests = team.getQuests();
        Quest trivia = quests.get(QuestManager.TRIVIA);
        if (trivia != null) {
            ItemStack representation = GuiUtil.formatItem(trivia.getFriendlyName(), Material.WRITABLE_BOOK, 4);
            if (trivia.isCompleted()) {
                representation = Icons.completedQuest(Component.text(trivia.getFriendlyName()));
            }
            Element element = new ButtonElement(representation, player -> {
                trivia.getGui(player).openInventory(player);
            });
            placeElement(1, 6, element);
        }

        Quest parkour = quests.get(QuestManager.PARKOUR);
        if (parkour != null) {
            ItemStack representation = GuiUtil.formatItem(parkour.getFriendlyName(), Material.FEATHER, 0);
            if (parkour.isCompleted()) {
                representation = Icons.completedQuest(Component.text(parkour.getFriendlyName()));
            }
            Element element = new ButtonElement(representation, player -> {
                parkour.getGui(player).openInventory(player);
            });
            placeElement(1, 7, element);
        }

    }

}
