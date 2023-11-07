package io.github.mystievous.towerchallenge.misc;

import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {

    public FlyCommand(JavaPlugin plugin) {
        plugin.getCommand("fly").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
            return true;
        }

        player.setAllowFlight(!player.getAllowFlight());
        if (player.getAllowFlight()) {
            player.sendMessage(Component.text("Flight On", Palette.PRIMARY.toTextColor()));
        } else {
            player.sendMessage(Component.text("Flight Off", Palette.NEGATIVE_COLOR.toTextColor()));
        }

        return true;
    }
}
