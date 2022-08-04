package io.github.idkahn.towerchallenge.towering;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TowerTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length == 1) {
                List<String> strings = new ArrayList<String>();

                strings.add("enable");
                strings.add("disable");
                strings.add("remove");
                strings.add("events");
                strings.add("time");

                return strings;

            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("events")) {
                    List<String> strings = new ArrayList<String>();

                    strings.add("enable");
                    strings.add("disable");

                    return strings;
                }
            }
        }

        return new ArrayList<String>();

    }
}
