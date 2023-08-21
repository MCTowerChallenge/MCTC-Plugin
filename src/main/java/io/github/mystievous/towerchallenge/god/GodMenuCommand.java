package io.github.mystievous.towerchallenge.god;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command executor to get the god menu book
 */
public class GodMenuCommand implements CommandExecutor {

    private final GodManager godManager;

    public GodMenuCommand(GodManager godManager) {
        this.godManager = godManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0 && args[0].equalsIgnoreCase("open")) {
                godManager.getGodGui().getGuiHeldItem().openInventory(player);
                return true;
            }
            player.sendMessage(Component.text("Given god book!").color(NamedTextColor.DARK_GREEN));
            player.getInventory().addItem(godManager.getGodGui().getItem());
            return true;
        }
        return false;
    }

}
