package io.github.mystievous.towerchallenge.towering;

import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.ConfirmationGUI;
import io.github.mystievous.towerchallenge.gui.page.TeamGui;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class WinnersGUI extends TeamGui {

    public WinnersGUI(TowerChallenge plugin, TeamManager teamManager, Element exitElement) {
        super(Component.text("Pick the winning team!"),
                participantTeam -> new ArrayList<>(),
                teamManager.getParticipantTeams().stream().map(TowerTeam.class::cast).toList(),
                (selectingPlayer, selectedTeam) -> {
                    new ConfirmationGUI(Component.text("Confirm ").append(selectedTeam.getDisplayName()).append(Component.text(" as the winning team.")),
                            player -> {
                                teamManager.updateWinningTeam(selectedTeam);
                                Title title = Title.title(selectedTeam.getDisplayName(), Component.text("is the winner of the event!").color(NamedTextColor.WHITE));
                                Bukkit.getServer().showTitle(title);
                                Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "ui.toast.challenge_complete"), Sound.Source.MASTER, 100, 1));
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::closeInventory, 1);
                            }, player -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::closeInventory, 1)).openInventory(selectingPlayer);
                },
                exitElement);
    }
}
