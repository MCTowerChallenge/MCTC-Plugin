package io.github.idkahn.towerchallenge.halloween.candy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CandyTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("candy")) {
            if (args.length == 1) {
                List<String> strings = new ArrayList<>();

                strings.add("reset");
                strings.add("spawn");
                strings.add("bundle");

                Predicate<String> compare = cmd -> cmd.toLowerCase().contains(args[0].toLowerCase());

                return strings.stream().filter(compare).collect(Collectors.toList());

            } else if (args.length == 2) {
                return null;
            } else if (args.length == 3) {
                return new ArrayList<>();
            }
        }

        return null;

    }
}
