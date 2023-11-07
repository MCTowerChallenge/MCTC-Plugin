package io.github.mystievous.towerchallenge.quest;

import io.github.mystievous.towerchallenge.eventspecific.oct2023.quest.Oct2023QuestManager;
import io.github.mystievous.towerchallenge.team.TeamManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class QuestCommands implements CommandExecutor {

    private final TeamManager teamManager;

    public QuestCommands(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(teamManager.getQuestBook().getItem());
                if (!leftoverItems.isEmpty()) {
                    player.sendMessage(Component.text("You must have space in your inventory to get a questbook.").color(NamedTextColor.RED));
                }
            } else {
                sender.sendMessage(Component.text("You must be a player to use this command."));
            }
        }
        return true;
    }
}
