package io.github.mctowerchallenge.towerchallenge.interaction.npc;

import io.github.mctowerchallenge.towerchallenge.team.TeamManager;
import io.github.mctowerchallenge.towerchallenge.team.TowerTeam;
import io.github.mctowerchallenge.towerchallenge.utility.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DialogueCommands implements CommandExecutor, TabCompleter {

    private final TeamManager teamManager;

    public DialogueCommands(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            String teamName = String.join(" ", args);
            TowerTeam team = teamManager.getTeam(teamName);
            if (team != null) {
                team.setStopDialogue(true);
            } else {
                sender.sendMessage(CommandUtils.errorMessage("That team does not exist"));
            }
        } else {
            if (sender instanceof Player player) {
                TowerTeam team = teamManager.getPlayerTeam(player);
                if (team != null) {
                    team.setStopDialogue(true);
                }
            } else {
                sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length <= 1) {
            return teamManager.getAllTeams().stream().map(TowerTeam::getTextName).toList();
        }

        return null;
    }
}
