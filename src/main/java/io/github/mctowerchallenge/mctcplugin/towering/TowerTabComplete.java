package io.github.mctowerchallenge.mctcplugin.towering;

import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TowerTabComplete implements TabCompleter {

    private final TeamManager teamManager;

    public TowerTabComplete(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length == 1) {
                List<String> strings = new ArrayList<>();

                strings.add("addPlayer");
                strings.add("addFullBlock");
                strings.add("removeFullBlock");
                strings.add("reloadTeams");
                strings.add("resetEndPortal");
                strings.add("showTowerScores");
                strings.add("dealItems");
                strings.add("resetTeams");
                strings.add("pickWinner");
                strings.add("toggleTower");
                strings.add("tpAllSpawn");
                strings.add("tpToSpawn");

                Predicate<String> compare = cmd -> cmd.toLowerCase().contains(args[0].toLowerCase());

                return strings.stream().filter(compare).collect(Collectors.toList());

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("tpToSpawn") ||
                        args[0].equalsIgnoreCase("addplayer") ||
                        args[0].equalsIgnoreCase("dealItems")) {
                    List<String> strings = new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList());

                    Predicate<String> compare = cmd -> cmd.toLowerCase().contains(args[1].toLowerCase());

                    return strings.stream().filter(compare).collect(Collectors.toList());
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("addplayer")) {
                    return teamManager.getParticipantTeams().stream().map(TowerTeam::getTextName).collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();

    }
}
