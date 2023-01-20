package io.github.mystievous.towerchallenge.wands;

import io.github.mystievous.towerchallenge.magic.MagicItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class WandCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                MagicItems.getGui().openInventory(player);
            } else {
                sender.sendMessage("You need to be a player to use this command.");
            }
        }
        return true;
    }
}
