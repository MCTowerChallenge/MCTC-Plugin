package io.github.idkahn.towerchallenge.Commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CommandHandler extends CommandArgument implements CommandExecutor {

    public CommandHandler(String name) {
        super(name);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {

        return false;
    }


}
