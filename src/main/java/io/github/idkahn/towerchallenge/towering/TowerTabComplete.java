package io.github.idkahn.towerchallenge.towering;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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

//                strings.add("book");
//                strings.add("enable");
//                strings.add("disable");
//                strings.add("remove");
//                strings.add("reloadTeams");
//                strings.add("reloadHats");
                strings.add("wand");
                strings.add("reloadConfig");
//                strings.add("gui");
                strings.add("hat");
//                strings.add("config");
//                strings.add("newTeam");
//                strings.add("getRegion");
//                strings.add("events");
//                strings.add("time");

                Predicate<String> compare = cmd -> cmd.toLowerCase().contains(args[0].toLowerCase());

                return strings.stream().filter(compare).collect(Collectors.toList());

            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("events")) {
                    List<String> strings = new ArrayList<String>();

                    strings.add("enable");
                    strings.add("disable");

                    Predicate<String> compare = cmd -> cmd.contains(args[1]);

                    return strings.stream().filter(compare).collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<String>();

    }
}
