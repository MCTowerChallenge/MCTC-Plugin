package io.github.mystievous.towerchallenge.towering;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.quests.QuestGui;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.towerchallenge.quests.legacy.BlockVoucher;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

import java.util.Arrays;
import java.util.Base64;

public class TowerCommands implements CommandExecutor {

    public static final TextComponent PERMISSION_WARN = Component.text("You do not have permission to use this command!").color(NamedTextColor.DARK_RED);

    private final TowerChallenge plugin;
    private final ChallengeManager challengeManager;
    private final TeamManager teamManager;

    public TowerCommands(TowerChallenge plugin, ChallengeManager challengeManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.challengeManager = challengeManager;
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case ("deserialize") -> {
                        if (sender instanceof Player player) {
                            ItemStack item = player.getInventory().getItemInMainHand();
                            byte[] bytes = item.serializeAsBytes();
                            String output = Base64.getEncoder().encodeToString(bytes);
                            player.sendMessage(output);
                            Bukkit.getServer().getLogger().info("Map: " + output);
                        } else {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                        }
                    }
                    case ("text") -> {
                        if (sender instanceof Player player) {
                            String[] words = Arrays.copyOfRange(args, 1, args.length);
                            StringBuilder builder = new StringBuilder();
                            for (String word : words) {
                                builder.append(word).append(' ');
                            }
                            QuestGui questGui = new QuestGui(plugin, "Test a Quest!", builder.toString());
                            questGui.openInventory(player);
                        }
                    }
                    case ("addplayer") -> {
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
                    case ("addfullblock") -> {
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
                    case ("removefullblock") -> {
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
                    case ("reloadteams") -> {
                        sender.sendMessage(Component.text("Reloading Config"));
                        teamManager.loadTeams();
                        teamManager.loadPlayers();
                    }
                    case ("resetendportal") -> teamManager.resetEndPortal();
                    case ("showtowerscores") -> teamManager.showTowerScores(sender);
                    case ("dealitems") -> {
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
                    case ("resetteams") -> teamManager.resetTeams();
                    case ("shulker") -> {
                        if (args.length < 2) {
                            sender.sendMessage(CommandUtils.errorMessage("Please enter a player to give a shulker"));
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                if (args.length < 3) {
                                    teamManager.getPlayerTeam(player).giveShulker(player, 1);
                                } else {
                                    try {
                                        int number = Integer.parseInt(args[2]);
                                        teamManager.getPlayerTeam(player).giveShulker(player, number);
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(CommandUtils.errorMessage("Please enter a valid number of shulkers."));
                                    }
                                }
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                    }
                    case ("pickwinner") -> {
                        if (sender instanceof Player player) {
                            challengeManager.getWinnersGUI().openInventory(player);
                        } else {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                        }
                    }
                    case ("voucher") -> {
                        if (sender instanceof Player player) {
                            int number = 1;
                            if (args.length > 1) {
                                try {
                                    number = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(CommandUtils.errorMessage("Invalid number for amount of vouchers."));
                                    return true;
                                }
                            }
                            player.getInventory().addItem(BlockVoucher.getVouchers(number));
                        } else {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                        }
                    }
                    case ("toggletower") -> {
                        if (challengeManager.getChallengePhase().equals(ChallengeManager.ChallengePhase.TOWERING)) {
                            challengeManager.setChallengePhase(ChallengeManager.ChallengePhase.IN_PROGRESS);
                            sender.sendMessage(Component.text("Tower Phase Disabled").color(NamedTextColor.RED));
                        } else if (challengeManager.getChallengePhase().equals(ChallengeManager.ChallengePhase.IN_PROGRESS)) {
                            challengeManager.setChallengePhase(ChallengeManager.ChallengePhase.TOWERING);
                            sender.sendMessage(Component.text("Tower Phase Enabled").color(NamedTextColor.GREEN));
                        }
                    }
                    default -> sender.sendMessage(Component.text("Invalid command usage.").color(NamedTextColor.RED));
                }
            }
        }

        return true;
    }
}
