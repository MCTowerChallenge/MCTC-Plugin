package io.github.mystievous.towerchallenge.hats;

import io.github.mystievous.towerchallenge.Database;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.ListGui;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.towering.TowerCommands;
import io.github.mystievous.towerchallenge.utility.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class HatCommands implements CommandExecutor {

    private final Database database;

    public HatCommands(Database database) {
        this.database = database;
    }

    public void setPlayerColor(Player player, Color color) throws SQLException {
        database.updatePlayerColor(player.getUniqueId(), color);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!"));
        }
        assert sender instanceof Player;
        Player player = (Player) sender;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("hand")) {
                if (player.hasPermission("towerchallenge.hat.hand")) {
                    PlayerInventory inventory = player.getInventory();
                    inventory.setHelmet(inventory.getItemInMainHand());
                } else {
                    player.sendMessage(TowerCommands.PERMISSION_WARN);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("color")) {
                if (player.hasPermission("towerchallenge.hat.color")) {
                    Color color = null;
                    try {
                        color = new Color(args[1]);
                        sender.sendMessage(Component.text("Setting hat color to ")
                                .append(Component.text(color.toHexString()).color(color.toTextColor()))
                                .append(Component.text("...").color(NamedTextColor.WHITE)));
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(CommandUtils.errorMessage("Hex color is invalid! Setting to default color"));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        sender.sendMessage("No color given, setting default...");
                    }
                    try {
                        setPlayerColor(player, color);
                    } catch (SQLException e) {
                        sender.sendMessage(CommandUtils.errorMessage("Error updating the database with your color!"));
                    }
                } else {
                    player.sendMessage(TowerCommands.PERMISSION_WARN);
                    return true;
                }
            }
        }
        try {
            ListGui hatGui = new ListGui(Component.text("Select a Hat:"), database.getPlayerHats(player.getUniqueId()), Element.empty());
            hatGui.openInventory(player);
        } catch (SQLException e) {
//            e.printStackTrace();
            player.sendMessage(CommandUtils.errorMessage("Error getting hats."));
        }
        return true;
    }

}
