package io.github.mystievous.towerchallenge.magic;

import io.github.mystievous.towerchallenge.magic.MagicItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WandCommands implements CommandExecutor {

    private final MagicItems magicItems;

    public WandCommands(MagicItems magicItems) {
        this.magicItems = magicItems;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                magicItems.getGui(player).openInventory(player);
            } else {
                sender.sendMessage("You need to be a player to use this command.");
            }
        }
        return true;
    }
}
