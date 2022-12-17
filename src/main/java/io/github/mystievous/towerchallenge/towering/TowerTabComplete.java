package io.github.mystievous.towerchallenge.towering;

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
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length == 1) {
                List<String> strings = new ArrayList<>();

                strings.add("reloadConfig");
                strings.add("toggleTower");
                strings.add("addFullBlock");
                strings.add("removeFullBlock");
                strings.add("dealItems");
                strings.add("shulker");
                strings.add("voucher");
                strings.add("showTowerScores");
                strings.add("resetEndPortal");
                strings.add("resetTeams");
                strings.add("addScore");
                strings.add("removeScore");
                strings.add("pickWinner");

                Predicate<String> compare = cmd -> cmd.toLowerCase().contains(args[0].toLowerCase());

                return strings.stream().filter(compare).collect(Collectors.toList());

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("shulker") || args[0].equalsIgnoreCase("dealItems") || args[0].equalsIgnoreCase("addScore") || args[0].equalsIgnoreCase("removeScore")) {
                    List<String> strings = new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList());

                    Predicate<String> compare = cmd -> cmd.toLowerCase().contains(args[1].toLowerCase());

                    return strings.stream().filter(compare).collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();

    }
}
