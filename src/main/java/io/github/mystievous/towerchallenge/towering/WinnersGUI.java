package io.github.mystievous.towerchallenge.towering;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.ConfirmationGUI;
import io.github.mystievous.towerchallenge.gui.page.TeamGui;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.sql.SQLException;
import java.util.ArrayList;

public class WinnersGUI extends TeamGui {

    public WinnersGUI(ChallengeManager challengeManager, Element exitElement) {
        super(Component.text("Pick the winning team!"),
                participantTeam -> new ArrayList<>(),
                challengeManager.getTowerListener().getTeams().values(),
                (selectingPlayer, selectedTeam) -> {
                    new ConfirmationGUI(Component.text("Confirm ").append(selectedTeam.getDisplayName()).append(Component.text(" as the winning team.")),
                            player -> {
                                try {
                                    challengeManager.getPlugin().getDatabase().updateWinningTeam(selectedTeam);
                                    Title title = Title.title(selectedTeam.getDisplayName(), Component.text("is the winner of the event!").color(NamedTextColor.WHITE));
                                    Bukkit.getServer().showTitle(title);
                                    Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "ui.toast.challenge_complete"), Sound.Source.MASTER, 100, 1));
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(challengeManager.getPlugin(), player::closeInventory, 1);
                                } catch (SQLException e) {
                                    player.sendMessage(CommandUtils.errorMessage("Error updating the database with the winners!"));
                                }
                            }, player -> Bukkit.getScheduler().scheduleSyncDelayedTask(challengeManager.getPlugin(), player::closeInventory, 1)).openInventory(selectingPlayer);
                },
                exitElement);
    }
}
