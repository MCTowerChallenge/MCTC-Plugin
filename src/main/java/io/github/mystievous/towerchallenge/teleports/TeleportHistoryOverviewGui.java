package io.github.mystievous.towerchallenge.teleports;

import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.PlayerGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TeleportHistoryOverviewGui extends PlayerGui {

    public static final String TITLE = "Teleport Histories:";

    public TeleportHistoryOverviewGui(TeleportHistoryManager historyManager, Gui exitGui) {
        super(Component.text(TITLE),
                player -> new ArrayList<>(){{
                    add(Component.text(historyManager.get(player).size())
                            .append(Component.text(" teleports"))
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.WHITE)
                    );
                }},
                historyManager.getTeleports().keySet().stream().map(Bukkit::getPlayer).collect(Collectors.toList()),
                (player, player2) -> (new TeleportHistoryIndividualGui(historyManager, player2, historyManager.getGui(player))).getGui(player).openInventory(player),
                new ButtonElement(ButtonElement.backItem(), exitGui::openInventory));
    }

}
