package io.github.idkahn.towerchallenge.hats;

import io.github.idkahn.towerchallenge.towering.GodTeam;
import io.github.idkahn.towerchallenge.towering.TowerCommands;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HatCommands implements CommandExecutor {

    private TowerListener towerListener;

    public HatCommands(TowerListener towerListener) {
        this.towerListener = towerListener;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!"));
        }
        Player player = (Player) sender;
        TowerTeam team = towerListener.getPlayerTeam(player);
        if (args.length > 0 && args[0].equalsIgnoreCase("color")) {
            if (PlainTextComponentSerializer.plainText().serialize(team.getDisplayName()).equals("God")) {
                String color = null;
                try {
                    color = args[1];
                    sender.sendMessage("Setting hat color to " + color + "...");
                } catch (ArrayIndexOutOfBoundsException e) {
                    sender.sendMessage("No color given, setting default...");
                }
//                Bukkit.getLogger().info("Setting player color to " + color);
                ((GodTeam) team).setPlayerHatColor((Player) sender, color);
            }
        }
        if (team != null) {
            team.openHatGUI(player);
        } else {
            player.sendMessage(Component.text("You are not assigned a team! Giving default hats.").color(NamedTextColor.DARK_RED));
            TowerListener.defaultHats.openInventory(player);
        }
        return true;
    }
}
