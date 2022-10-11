package io.github.idkahn.towerchallenge.towering;

import io.github.idkahn.towerchallenge.EventManager;
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
//                    case ("wand"):
//                        if (!(sender instanceof Player)) {
//                            sender.sendMessage(Component.text("You must be a player to run this command.").color(NamedTextColor.DARK_RED));
//                            break;
//                        }
//                        if (!(((Player) sender).hasPermission("towerchallenge.tower.wand"))) {
//                            sender.sendMessage(PERMISSION_WARN);
//                            break;
//                        }
//                        if (args[1] == null) {
//                            sender.sendMessage(Component.text("Please enter a wand ID!").color(NamedTextColor.DARK_RED));
//                            break;
//                        }
//                        try {
//                            int id = Integer.parseInt(args[1]);
//                            WandGUI.getWand((Player) sender, id);
//                        } catch (NumberFormatException e) {
//                            sender.sendMessage(Component.text("Please enter a valid ID!").color(NamedTextColor.DARK_RED));
//                        }
//                        break;
                    case ("reloadconfig"):
                        sender.sendMessage(Component.text("Reloading Config"));
                        towerListener.loadConfig();
                        break;
                    case ("openendportal"):
                        manager.openEndPortal();
                    case ("resetendportal"):
                        manager.resetEndPortal();
                        break;
                    case ("showtowerscores"):
                        manager.showTowerScores();
                        break;
                    case ("dealitems"):
                        if (args.length < 2) {
                            manager.dealItems();
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                manager.dealPlayerItems(player);
                            }
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
