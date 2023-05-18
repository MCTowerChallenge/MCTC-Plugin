package io.github.mystievous.towerchallenge.misc;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpectateTPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String name, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length > 0) {
                player.setGameMode(GameMode.SPECTATOR);
                try {
                    Entity entity = Bukkit.getEntity(UUID.fromString(args[0]));
                    if (entity != null) {
                        player.teleport(entity);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        return true;
    }
}
