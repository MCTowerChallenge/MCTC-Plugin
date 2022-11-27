package io.github.idkahn.towerchallenge.halloween.steve;

import io.github.idkahn.towerchallenge.misc.CommandUtils;
import io.github.idkahn.towerchallenge.towering.TowerCommands;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SteveCommands implements CommandExecutor {

    private final SteveManager steveManager;

    public SteveCommands(SteveManager steveManager) {
        this.steveManager = steveManager;
        steveManager.getEventManager().getPlugin().getCommand("steve").setTabCompleter(new SteveTabComplete());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("dialogue")) {
                if (sender.hasPermission("towerchallenge.steve.dialog")) {
                    if (sender instanceof Player player) {
                        TowerTeam team = steveManager.getEventManager().getTowerListener().getPlayerTeam(player);
                        if (team != null) {
                            steveManager.playDialogue(player);
                        } else {
                            sender.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
                        }
                    } else {
                        sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                    }
                } else {
                    sender.sendMessage(TowerCommands.PERMISSION_WARN);
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("towerchallenge.steve.reload")) {
                    steveManager.loadSteve();
                    sender.sendMessage("Reloading Steve's Config.");
                } else {
                    sender.sendMessage(TowerCommands.PERMISSION_WARN);
                }
            } else if (args[0].equalsIgnoreCase("stage")) {
                if (sender.hasPermission("towerchallenge.steve.stage")) {
                    if (args.length > 1) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            TowerTeam team = steveManager.getEventManager().getTowerListener().getPlayerTeam(target);
                            if (team != null) {
                                if (args.length > 2) {
                                    try {
                                        int value = Integer.parseInt(args[2]);
                                        steveManager.setTeamStage(team, value);
                                        sender.sendMessage(Component.text("Set Stage of ")
                                                .append(team.getDisplayName().append(Component.text(" to ")))
                                                .append(Component.text(steveManager.getTeamStage(team))));
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(CommandUtils.errorMessage("Invalid Stage ID given"));
                                    }
                                } else {
                                    sender.sendMessage(Component.text("Current Stage of ")
                                            .append(team.getDisplayName().append(Component.text(": ")))
                                            .append(Component.text(steveManager.getTeamStage(team))));
                                }
                            } else {
                                sender.sendMessage(CommandUtils.errorMessage("Target player is not on a team!"));
                            }
                        } else {
                            sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                        }
                    } else {
                        sender.sendMessage(CommandUtils.errorMessage("Please enter a player's name."));
                    }
                } else {
                    sender.sendMessage(TowerCommands.PERMISSION_WARN);
                }
            }
        }

        return true;
    }
}
