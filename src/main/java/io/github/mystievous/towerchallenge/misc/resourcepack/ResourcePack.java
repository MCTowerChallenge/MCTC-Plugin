package io.github.mystievous.towerchallenge.misc.resourcepack;

import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.towerchallenge.towering.TowerCommands;
import io.github.mystievous.mysticore.Palette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResourcePack implements CommandExecutor {

    public static void sendResourcePack(Player player) {
        player.setResourcePack(Bukkit.getResourcePack(), Bukkit.getResourcePackHash());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Component.text("Sending resource pack...").color(Palette.PRIMARY.toTextColor()));
            sendResourcePack(player);
            return true;
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (args.length > 1) {
                    if (!sender.hasPermission("towerchallenge.resourcepack.other.reload")) {
                        sender.sendMessage(TowerCommands.PERMISSION_WARN);
                        return true;
                    }
                    Player targetPlayer = Bukkit.getPlayer(args[1]);
                    if (targetPlayer == null) {
                        sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                        return true;
                    }
                    sender.sendMessage(Component.text("Sending resource pack to "+targetPlayer.getName()+"...").color(Palette.PRIMARY.toTextColor()));
                    sendResourcePack(targetPlayer);
                } else {
                    sender.sendMessage(CommandUtils.errorMessage("Please specify a player to send the resource pack"));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("status") && args.length == 1) {
                if (!sender.hasPermission("towerchallenge.resourcepack.other.status")) {
                    sender.sendMessage(TowerCommands.PERMISSION_WARN);
                    return true;
                }
                for (Player targetPlayer : Bukkit.getServer().getOnlinePlayers()) {
                    sender.sendMessage(Component.text(String.format("[RP] %s: %s", targetPlayer.getName(), targetPlayer.getResourcePackStatus()))
                            .append(Component.text(" [Send Reload]")
                                    .color(Palette.PRIMARY.toTextColor())
                                    .clickEvent(ClickEvent.runCommand("/resourcepack reload "+targetPlayer.getName()))
                                    .hoverEvent(Component.text("Click to Send"))));
                }
            } else {
                if (!sender.hasPermission("towerchallenge.resourcepack.other.status")) {
                    sender.sendMessage(TowerCommands.PERMISSION_WARN);
                    return true;
                }
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null) {
                    sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                    return true;
                }

                sender.sendMessage(Component.text(String.format("%s's resource pack status is: %s;", targetPlayer.getName(), targetPlayer.getResourcePackStatus()))
                        .append(Component.text(" [Send Reload]")
                                .color(Palette.PRIMARY.toTextColor())
                                .clickEvent(ClickEvent.runCommand("/resourcepack reload "+targetPlayer.getName()))
                                .hoverEvent(Component.text("Click to Send"))));
            }
        }
        return true;
    }
}
