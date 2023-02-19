package io.github.mystievous.towerchallenge.misc;

import io.github.mystievous.towerchallenge.utility.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CraftCommand implements CommandExecutor {

    public CraftCommand(JavaPlugin plugin) {
        plugin.getCommand("craft").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
            return true;
        }
        player.openWorkbench(null, true);

        return true;
    }

}
