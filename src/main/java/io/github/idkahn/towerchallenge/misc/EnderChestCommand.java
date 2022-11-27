package io.github.idkahn.towerchallenge.misc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class EnderChestCommand implements CommandExecutor {

    public EnderChestCommand(JavaPlugin plugin) {
        plugin.getCommand("enderchest").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.openInventory(player.getEnderChest());
            } else {
                Player otherPlayer = Bukkit.getPlayer(args[0]);
                if (otherPlayer != null) {
                    player.openInventory(otherPlayer.getEnderChest());
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
