package io.github.mctowerchallenge.mctcplugin.towering;

import io.github.mctowerchallenge.mctcplugin.ChallengeManager;
import io.github.mctowerchallenge.mctcplugin.Database;
import io.github.mctowerchallenge.mctcplugin.portal.EndPortal;
import io.github.mctowerchallenge.mctcplugin.portal.PortalControllers;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TowerCommands implements CommandExecutor {

    private final ChallengeManager challengeManager;
    private final TeamManager teamManager;
    private final EndPortal endPortal;
    private final Database database;

    public TowerCommands(ChallengeManager challengeManager, TeamManager teamManager, PortalControllers portalControllers, Database database) {
        this.challengeManager = challengeManager;
        this.teamManager = teamManager;
        this.endPortal = portalControllers.getEndPortal();
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case ("addplayer") -> { // Adds a player to a certain team.
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        StringBuilder teamNameBuilder = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            teamNameBuilder.append(args[i]).append(" ");
                        }
                        String teamName = teamNameBuilder.toString().strip();
                        TowerTeam team = teamManager.getTeam(teamName);
                        if (team != null) {
                            if (teamManager.setPlayerTeam(player, team)) {
                                sender.sendMessage(Component.text(player.getName()).append(Component.text(" set to team ")).append(team.getDisplayName()));
                                teamManager.loadPlayers();
                            } else {
                                sender.sendMessage(CommandUtils.errorMessage(Component.text("Could not set ")
                                        .append(Component.text(player.getName())).append(Component.text(" to team "))
                                        .append(team.getDisplayName())));
                            }
                        } else {
                            sender.sendMessage(CommandUtils.errorMessage(String.format("Team %s does not exist", teamName)));
                        }
                    }
                    case ("addfullblock") -> { // Adds the block in the player's hand to the full block list
                        if (args.length < 2) {
                            if (sender instanceof Player player) {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                if (item.getType().isAir()) {
                                    player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                    player.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                } else {
                                    try {
                                        challengeManager.addFullBlock(item.getType());
                                        player.sendMessage(Component.text(TextUtil.formatBlockType(item.getType())).append(Component.text("added as a full block.")));
                                    } catch (IllegalArgumentException e) {
                                        player.sendMessage(Component.text("Held Item is not a block!").color(NamedTextColor.DARK_RED));
                                        player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                        player.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                    }
                                }
                            } else {
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        } else {
                            try {
                                Material type = Material.valueOf(args[1].toUpperCase());
                                challengeManager.addFullBlock(type);
                                sender.sendMessage(Component.text(TextUtil.formatBlockType(type)).append(Component.text("added as a full block.")));
                            } catch (IllegalArgumentException e) {
                                sender.sendMessage(Component.text("Selected material is not a block! Make sure you entered it correctly.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        }
                    }
                    case ("removefullblock") -> { // Removes the block in the player's hand to the full block list.
                        if (args.length < 2) {
                            if (sender instanceof Player player) {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                if (item.getType().isAir()) {
                                    player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                    player.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                } else {
                                    try {
                                        challengeManager.removeFullBlock(item.getType());
                                        player.sendMessage(Component.text(TextUtil.formatBlockType(item.getType())).append(Component.text("is no longer a full block.")));
                                    } catch (IllegalArgumentException e) {
                                        player.sendMessage(Component.text("Held Item is not a block!").color(NamedTextColor.DARK_RED));
                                        player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                        player.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                    }
                                }
                            } else {
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        } else {
                            try {
                                Material type = Material.valueOf(args[1].toUpperCase());
                                challengeManager.removeFullBlock(type);
                                sender.sendMessage(Component.text(TextUtil.formatBlockType(type)).append(Component.text("is no longer a full block.")));
                            } catch (IllegalArgumentException e) {
                                sender.sendMessage(Component.text("Selected material is not a block! Make sure you entered it correctly.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        }
                    }
                    case ("reloadteams") -> { // Reloads the teams and players
                        sender.sendMessage(Component.text("Reloading Config"));
                        teamManager.loadTeams();
                        teamManager.loadPlayers();
                    }
                    case ("resetendportal") -> endPortal.resetPortal(); // resets the end portal to empty
                    case ("showtowerscores") ->
                            teamManager.showTowerScores(sender); // displays the tower scores to the command sender
                    case ("dealitems") -> { // deals items to all players, or the specified player.
                        if (args.length < 2) {
                            teamManager.dealAllItems();
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                teamManager.dealPlayerItems(player);
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                    }
                    case ("resetteams") -> teamManager.resetTeams(); // resets teams and re-adds players.
                    case ("pickwinner") -> { // Picks the winning team of the event; announces and gives them the crown.
                        if (sender instanceof Player player) {
                            challengeManager.getWinnersGUI().openInventory(player);
                        } else {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                        }
                    }
                    case ("toggletower") -> { // Enables/disables the towering phase of the event so players can place blocks.
                        if (challengeManager.getChallengePhase().equals(ChallengeManager.ChallengePhase.TOWERING)) {
                            challengeManager.setChallengePhase(ChallengeManager.ChallengePhase.IN_PROGRESS);
                            sender.sendMessage(Component.text("Tower Phase Disabled").color(NamedTextColor.RED));
                        } else if (challengeManager.getChallengePhase().equals(ChallengeManager.ChallengePhase.IN_PROGRESS)) {
                            challengeManager.setChallengePhase(ChallengeManager.ChallengePhase.TOWERING);
                            sender.sendMessage(Component.text("Tower Phase Enabled").color(NamedTextColor.GREEN));
                        }
                    }
                    case ("tpallspawn") -> {
                        teamManager.teleportAllSpawn();
                    }
                    case ("tptospawn") -> { // deals items to all players, or the specified player.
                        if (args.length >= 2) {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                if (teamManager.getPlayerTeam(player) instanceof ParticipantTeam team) {
                                    team.teleportToSpawn(player);
                                }
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                    }
                    default -> sender.sendMessage(Component.text("Invalid command usage.").color(NamedTextColor.RED));
                }
            }
        }

        return true;
    }
}
