package io.github.mctowerchallenge.mctcplugin.god.godgui;

import io.github.mctowerchallenge.mctcplugin.gui.Icons;
import io.github.mctowerchallenge.mctcplugin.gui.page.TeamGui;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.PlayerGui;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SetTeamGui {

    public static PlayerGui createPlayerGui(Plugin plugin, TeamManager teamManager, List<OfflinePlayer> playerList, Gui lastPage) {
        ButtonElement backButton = new ButtonElement(Icons.backItem(), lastPage::openInventory);

        Component title = Component.text("Select a player:");

        Function<OfflinePlayer, List<Component>> loreBuilder = offlinePlayer -> {
            TowerTeam team = teamManager.getPlayerTeam(offlinePlayer);
            Component currentTeamName;
            if (team != null) {
                currentTeamName = team.getDisplayName();
            } else {
                currentTeamName = Component.text("No Current Team");
            }
            return TextUtil.formatTexts(Component.text("Current Team: ").append(currentTeamName));
        };

        BiConsumer<Player, OfflinePlayer> onClick = (playerClicking, playerSelected) -> {
            Component teamTitle = Component.text("Team to Assign:");

            BiConsumer<Player, TowerTeam> onTeamClick = (playerClicking1, team) -> {
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
            };

            new TeamGui(plugin, teamTitle, team -> new ArrayList<>(), teamManager.getAllTeams(), onTeamClick, backButton).openInventory(playerClicking);
        };

        return new PlayerGui(plugin, title, loreBuilder, playerList, onClick, backButton);
    }

}
