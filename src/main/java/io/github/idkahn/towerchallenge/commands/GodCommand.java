package io.github.idkahn.towerchallenge.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GodCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) {
            if (sender instanceof Player) {

                Player player = (Player) sender;
                player.setInvulnerable(!player.isInvulnerable());
                if (player.isInvulnerable()) {
                    player.sendMessage(Component.text("God Mode Enabled").color(NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("God Mode Disabled").color(NamedTextColor.RED));
                }

            } else {
                sender.sendMessage(Component.text("Please insert a player to toggle god mode on."));
                sender.sendMessage(Component.text("eg. /god <Player>"));
            }
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            player.setInvulnerable(player.isInvulnerable());
            if (player.isInvulnerable()) {
                sender.sendMessage(Component.text("God Mode Enabled for ").append(Component.text(player.getName())).color(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("God Mode Disabled for ").append(Component.text(player.getName())).color(NamedTextColor.RED));
            }
        }

        return true;
    }
}
