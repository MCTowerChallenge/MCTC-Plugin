package io.github.mystievous.towerchallenge.wands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class WandCommands implements CommandExecutor {

    private final WandGUI gui;

    public WandCommands(Plugin plugin) {
        this.gui = new WandGUI(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                this.gui.openInventory(player);
            } else {
                sender.sendMessage("You need to be a player to use this command.");
            }
        } else {
            if (args[0].equals("reload")) {
                this.gui.loadWands();
            }
        }
        return true;
    }
}
