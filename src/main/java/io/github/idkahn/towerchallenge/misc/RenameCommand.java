package io.github.idkahn.towerchallenge.misc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class RenameCommand implements CommandExecutor {

    public RenameCommand(JavaPlugin plugin) {
        plugin.getCommand("rename").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(CommandUtils.errorMessage("Hold an item to rename it."));
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(CommandUtils.errorMessage("Enter a name to rename your held item."));
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        TextComponent.Builder nameBuilder = Component.text();
        for (int i = 0; i < args.length; i++) {
            if (i != 0) {
                nameBuilder.append(Component.space());
            }
            nameBuilder.append(Component.text(args[i]).decoration(TextDecoration.ITALIC, false));
        }
        meta.displayName(nameBuilder.build());
        item.setItemMeta(meta);

        return true;
    }

}
