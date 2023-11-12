package io.github.mctowerchallenge.towerchallenge.misc;

import io.github.mctowerchallenge.towerchallenge.utility.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class InvseeCommand implements CommandExecutor {

    public InvseeCommand(JavaPlugin plugin) {
        plugin.getCommand("inventorysee").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length == 0) {
                sender.sendMessage(CommandUtils.errorMessage("You must select a player to view!"));
            } else {
                Player otherPlayer = Bukkit.getPlayer(args[0]);
                if (otherPlayer != null) {
                    player.openInventory(otherPlayer.getInventory());
                } else {
                    sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                }
            }
        } else {
            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
        }

        return true;
    }
}
