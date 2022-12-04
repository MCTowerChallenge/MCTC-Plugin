package io.github.idkahn.towerchallenge.teleports;

import io.github.idkahn.towerchallenge.gui.element.ButtonElement;
import io.github.idkahn.towerchallenge.gui.page.Gui;
import io.github.idkahn.towerchallenge.gui.page.PlayerGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TeleportHistoryOverviewGui extends PlayerGui {

    public static final String TITLE = "Teleport Histories:";

    private TeleportHistoryManager historyManager;

    public TeleportHistoryOverviewGui(TeleportHistoryManager historyManager, Gui exitGui) {
        super(Component.text(TITLE),
                new ItemStack(Material.PLAYER_HEAD),
                player -> new ArrayList<>(){{
                    add(Component.text(historyManager.get(player).size())
                            .append(Component.text(" teleports"))
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.WHITE)
                    );
                }},
                historyManager.getTeleports().keySet().stream().map(Bukkit::getPlayer).collect(Collectors.toList()),
                (player, player2) -> (new TeleportHistoryIndividualGui(historyManager, player2, historyManager.getGui())).getGui().openInventory(player),
                new ButtonElement(ButtonElement.backItem(), exitGui::openInventory));
    }

    public TeleportHistoryOverviewGui(TeleportHistoryManager historyManager) {
        this(historyManager, null);
    }

}
