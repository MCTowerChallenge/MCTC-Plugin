package io.github.idkahn.towerchallenge.towering;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TowerCommands implements CommandExecutor {

    private TowerListener towerListener;

    public TowerCommands(TowerListener towerListener) {
        this.towerListener = towerListener;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length > 0) {
                switch(args[0]) {
                    case ("enable"):
                        sender.sendMessage(Component.text("Enabling Tower Phase"));
                        towerListener.enableTower();
                        break;
                    case ("disable"):
                        sender.sendMessage(Component.text("Disabling Tower Phase"));
                        towerListener.disableTower();
                        break;
                    case ("remove"):
                        sender.sendMessage(Component.text("Removing Tower Blocks"));
                        towerListener.removeBlocks();
                        break;
                    case ("events"):
                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("enable")) {
                                sender.sendMessage(Component.text("Enabling Block Events"));
                                towerListener.enableEvents();
                            } else {
                                sender.sendMessage(Component.text("Disabling Block Events"));
                                towerListener.disableEvents();
                            }
                        }
                        break;
                    case ("time"):
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        sender.sendMessage(now.format(dtf));
                        break;
                    default:
                        sender.sendMessage(Component.text("Invalid Command").color(NamedTextColor.RED));
                }
            }
        }

        return true;
    }
}
