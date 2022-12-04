package io.github.idkahn.towerchallenge.gods;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GodMenuCommand implements CommandExecutor {

    private GodManager godManager;

    public GodMenuCommand(GodManager godManager) {
        this.godManager = godManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            player.sendMessage(Component.text("Given god book!").color(NamedTextColor.DARK_GREEN));
            player.getInventory().addItem(godManager.getGodGui().getGuiItem());
            return true;
        }
        return false;
    }

}
