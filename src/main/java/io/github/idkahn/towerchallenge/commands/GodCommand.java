package io.github.idkahn.towerchallenge.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GodCommand implements CommandExecutor {

    public void toggleGod(Entity entity, Audience audience) {
        entity.setInvulnerable(!entity.isInvulnerable());
        if (entity.isInvulnerable()) {
            audience.sendMessage(Component.text("God Mode Enabled for ").append(Component.text(entity.getName())).color(NamedTextColor.GREEN));
        } else {
            audience.sendMessage(Component.text("God Mode Disabled for ").append(Component.text(entity.getName())).color(NamedTextColor.RED));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Please insert a player to toggle god mode on."));
                sender.sendMessage(Component.text("eg. /god <Player Name>"));
                return true;
            }

            toggleGod(player, sender);
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                return true;
            }

            toggleGod(player, sender);
        }

        return true;
    }
}
