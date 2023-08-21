package io.github.mystievous.towerchallenge.god.godgui;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.PlayerGui;
import io.github.mystievous.towerchallenge.gui.Icons;
import io.github.mystievous.towerchallenge.gui.page.TeamGui;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class SetTeamGui extends PlayerGui {

    public SetTeamGui(Plugin plugin, TeamManager teamManager, List<OfflinePlayer> playerList, Gui lastPage) {
        super(plugin, Component.text("Select a player:"), iconPlayer -> {
            TowerTeam team = teamManager.getPlayerTeam(iconPlayer);
            Component currentTeamName;
            if (team != null) {
                currentTeamName = team.getDisplayName();
            } else {
                currentTeamName = Component.text("No Current Team");
            }
            return TextUtil.formatTexts(Component.text("Current Team: ").append(currentTeamName));
        }, playerList, (playerClicking, playerSelected) -> {
            new TeamGui(plugin, Component.text("Team to Assign:"), team -> new ArrayList<>(), teamManager.getAllTeams(), (playerClicking1, team) -> {
                String playerIdentifier;
                if (playerSelected.getName() != null) {
                    playerIdentifier = playerSelected.getName();
                } else {
                    playerIdentifier = playerSelected.getUniqueId().toString();
                }
                if (teamManager.setPlayerTeam(playerSelected, team)) {
                    playerClicking.sendMessage(Component.text(playerIdentifier).append(Component.text(" set to team ")).append(team.getDisplayName()));
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, teamManager::loadPlayers);
                } else {
                    playerClicking.sendMessage(CommandUtils.errorMessage(Component.text("Could not set ")
                            .append(Component.text(playerIdentifier)).append(Component.text(" to team "))
                            .append(team.getDisplayName())));
                }
                lastPage.openInventory(playerClicking);
            }, new ButtonElement(Icons.backItem(), lastPage::openInventory)).openInventory(playerClicking);
        }, new ButtonElement(Icons.backItem(), lastPage::openInventory));
    }

}
