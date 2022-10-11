package io.github.idkahn.towerchallenge.hats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HatTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("timer")) {
            if (args.length == 1) {
                List<String> strings = new ArrayList<>();

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("towerchallenge.hat.hand")) {
                        strings.add("hand");
                    }
                    if (player.hasPermission("towerchallenge.hat.getitem")) {
                        strings.add("getItem");
                    }
                    if (player.hasPermission("towerchallenge.hat.color")) {
                        strings.add("color");
                    }
                }

                Predicate<String> compare = cmd -> cmd.toLowerCase().contains(args[0].toLowerCase());

                return strings.stream().filter(compare).collect(Collectors.toList());

            }
        }

        return new ArrayList<>();

    }
}
