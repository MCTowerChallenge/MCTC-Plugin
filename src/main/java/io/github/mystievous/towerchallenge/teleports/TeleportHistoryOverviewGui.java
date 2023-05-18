package io.github.mystievous.towerchallenge.teleports;

import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.PlayerGui;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.Icons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A Gui which lists all players,
 * linking to their individual
 * teleport histories.
 */
public class TeleportHistoryOverviewGui extends PlayerGui {

    public static final String TITLE = "Teleport Histories:";

    public TeleportHistoryOverviewGui(TowerChallenge plugin, TeleportHistoryManager historyManager, Gui exitGui) {
        super(plugin, Component.text(TITLE),
                player -> new ArrayList<>() {{
                    add(Component.text(historyManager.get(player).size())
                            .append(Component.text(" teleports"))
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.WHITE)
                    );
                }},
                historyManager.getTeleports().keySet().stream().map(Bukkit::getPlayer).collect(Collectors.toList()),
                (player, player2) -> (new TeleportHistoryIndividualGui(plugin, historyManager, player2, historyManager.getGui(player))).getGui(player).openInventory(player),
                new ButtonElement(Icons.backItem(), exitGui::openInventory));
    }

}
