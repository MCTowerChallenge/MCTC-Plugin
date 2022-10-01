package io.github.idkahn.towerchallenge.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandHandler extends CommandArgument implements CommandExecutor {

    public CommandHandler(String name) {
        super(name);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {

        return false;
    }


}
