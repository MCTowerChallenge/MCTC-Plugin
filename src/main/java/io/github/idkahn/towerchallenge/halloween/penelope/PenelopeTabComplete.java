package io.github.idkahn.towerchallenge.halloween.penelope;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PenelopeTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> strings = new ArrayList<>();

            strings.add("create");
            strings.add("remove");
            strings.add("set");
            strings.add("reload");

            Predicate<String> compare = cmd -> cmd.toLowerCase().contains(args[0].toLowerCase());

            return strings.stream().filter(compare).collect(Collectors.toList());

        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("set")) {
                List<String> strings = new ArrayList<>();

                strings.add("alive");
                strings.add("zombie");
                strings.add("skeleton");

                Predicate<String> compare = cmd -> cmd.contains(args[1]);

                return strings.stream().filter(compare).collect(Collectors.toList());
            }
        }

        return new ArrayList<>();

    }
}
