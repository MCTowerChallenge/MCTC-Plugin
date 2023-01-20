package io.github.mystievous.towerchallenge.teleports;

import io.github.mystievous.towerchallenge.Palette;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BackCommand implements CommandExecutor {

    private final TeleportHistoryManager teleportHistoryManager;

    public BackCommand(TeleportHistoryManager teleportHistoryManager) {
        this.teleportHistoryManager = teleportHistoryManager;
        Bukkit.getPluginCommand("back").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
            return true;
        }
        TeleportLocation location = teleportHistoryManager.getLastLocation(player);
        if (location == null) {
            sender.sendMessage(CommandUtils.errorMessage("No previous location to teleport to."));
            return true;
        }
        player.teleport(location);
        player.sendMessage(Component.text("Teleported back to last location.").color(Palette.PRIMARY.toTextColor()));
        return true;
    }
}
