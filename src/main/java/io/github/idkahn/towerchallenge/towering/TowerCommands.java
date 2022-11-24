package io.github.idkahn.towerchallenge.towering;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.commands.CommandUtils;
import io.github.idkahn.towerchallenge.quests.BlockVoucher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TowerCommands implements CommandExecutor {

    public static final TextComponent PERMISSION_WARN = Component.text("You do not have permission to use this command!").color(NamedTextColor.DARK_RED);
    private final TowerListener towerListener;
    private final EventManager manager;

    public TowerCommands(EventManager manager) {
        this.manager = manager;
        this.towerListener = manager.getTowerListener();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length > 0) {
                switch(args[0].toLowerCase()) {
                    case ("addfullblock"):
                        if (args.length < 2) {
                            if (sender instanceof Player player) {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                if (item.getType().isAir()) {
                                    player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                    player.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                } else {
                                    try {
                                        manager.addFullBlock(item.getType());
                                        player.sendMessage(Component.text(EventManager.formatBlockType(item.getType())).append(Component.text("added as a full block.")));
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
                                manager.addFullBlock(type);
                                sender.sendMessage(Component.text(EventManager.formatBlockType(type)).append(Component.text("added as a full block.")));
                            } catch (IllegalArgumentException e) {
                                sender.sendMessage(Component.text("Selected material is not a block! Make sure you entered it correctly.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        }
                        break;
                    case("removefullblock"):
                        if (args.length < 2) {
                            if (sender instanceof Player player) {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                if (item.getType().isAir()) {
                                    player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                    player.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                } else {
                                    try {
                                        manager.removeFullBlock(item.getType());
                                        player.sendMessage(Component.text(EventManager.formatBlockType(item.getType())).append(Component.text("is no longer a full block.")));
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
                                manager.removeFullBlock(type);
                                sender.sendMessage(Component.text(EventManager.formatBlockType(type)).append(Component.text("is no longer a full block.")));
                            } catch (IllegalArgumentException e) {
                                sender.sendMessage(Component.text("Selected material is not a block! Make sure you entered it correctly.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        }
                        break;
                    case ("reloadconfig"):
                        sender.sendMessage(Component.text("Reloading Config"));
                        towerListener.loadConfig();
                        break;
                    case ("resetendportal"):
                        manager.resetEndPortal();
                        break;
                    case ("showtowerscores"):
                        manager.showTowerScores(sender);
                        break;
                    case ("dealitems"):
                        if (args.length < 2) {
                            manager.dealItems();
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                manager.dealPlayerItems(player);
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                        break;
                    case ("resetteams"):
                        manager.resetTeams();
                        towerListener.loadTeams();
                        break;
                    case ("shulker"):
                        if (args.length < 2) {
                            sender.sendMessage(CommandUtils.errorMessage("Please enter a player to give a shulker"));
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                if (args.length < 3) {
                                    manager.getTowerListener().getPlayerTeam(player).giveShulker(player, 1);
                                } else {
                                    try {
                                        int number = Integer.parseInt(args[2]);
                                        manager.getTowerListener().getPlayerTeam(player).giveShulker(player, number);
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(CommandUtils.errorMessage("Please enter a valid number of shulkers."));
                                    }
                                }
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                        break;
                    case ("addscore"):
                        if (args.length < 2) {
                            sender.sendMessage(CommandUtils.errorMessage("Please enter a player to add score to"));
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                ParticipantTeam team = towerListener.getPlayerTeam(player);
                                if (team != null) {
                                    if (args.length < 3) {
                                        sender.sendMessage(CommandUtils.errorMessage("Please enter a valid score to add."));
                                    } else {
                                        try {
                                            int number = Integer.parseInt(args[2]);
                                            team.addExtraScore(number);
                                            sender.sendMessage(Component.text("Added ")
                                                    .append(Component.text(number))
                                                    .append(Component.text(", score for "))
                                                    .append(team.getDisplayName())
                                                    .append(Component.text(" is now "))
                                                    .append(Component.text(team.getScore())));
                                        } catch (NumberFormatException e) {
                                            sender.sendMessage(CommandUtils.errorMessage("Please enter a valid score to add."));
                                        }
                                    }
                                } else {
                                    sender.sendMessage(CommandUtils.errorMessage("Selected player is not on a team."));
                                }
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                        break;
                    case ("removescore"):
                        if (args.length < 2) {
                            sender.sendMessage(CommandUtils.errorMessage("Please enter a player to remove score from"));
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                ParticipantTeam team = towerListener.getPlayerTeam(player);
                                if (team != null) {
                                    if (args.length < 3) {
                                        sender.sendMessage(CommandUtils.errorMessage("Please enter a valid score to remove."));
                                    } else {
                                        try {
                                            int number = Integer.parseInt(args[2]);
                                            team.removeExtraScore(number);
                                            sender.sendMessage(Component.text("Removed ")
                                                    .append(Component.text(number))
                                                    .append(Component.text(", score for "))
                                                    .append(team.getDisplayName())
                                                    .append(Component.text(" is now "))
                                                    .append(Component.text(team.getScore())));
                                        } catch (NumberFormatException e) {
                                            sender.sendMessage(CommandUtils.errorMessage("Please enter a valid score to remove."));
                                        }
                                    }
                                } else {
                                    sender.sendMessage(CommandUtils.errorMessage("Selected player is not on a team."));
                                }
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                        break;
                    case ("pickwinner"):
                        if (sender instanceof Player player) {
                            manager.getWinnerGUI().openUI(player);
                        } else {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                        }
                        break;
                    case ("voucher"):
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
                        break;
                    case ("toggletower"):
                        if (manager.getEventPhase().equals(EventManager.Phase.TOWERING)) {
                            manager.setEventPhase(EventManager.Phase.SETUP);
                            sender.sendMessage(Component.text("Tower Phase Disabled").color(NamedTextColor.RED));
                        } else if (manager.getEventPhase().equals(EventManager.Phase.SETUP)) {
                            manager.setEventPhase(EventManager.Phase.TOWERING);
                            sender.sendMessage(Component.text("Tower Phase Enabled").color(NamedTextColor.GREEN));
                        }
                        break;
                    default:
                        sender.sendMessage(Component.text("Invalid command usage.").color(NamedTextColor.RED));
                }
            }
        }

        return true;
    }
}
