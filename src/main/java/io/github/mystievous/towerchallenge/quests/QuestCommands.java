package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class QuestCommands implements CommandExecutor {

    private final QuestManager questManager;

    public QuestCommands(QuestManager questManager) {
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(questManager.getQuestBook().getItem());
                if (!leftoverItems.isEmpty()) {
                    player.sendMessage(Component.text("You must have space in your inventory to get a questbook.").color(NamedTextColor.RED));
                }
            } else {
                sender.sendMessage(Component.text("You must be a player to use this command."));
            }
        } else {
            if (args[0].equalsIgnoreCase("reset")) {
                if (sender.hasPermission("towerchallenge.questbook.reset")) {
                    if (sender instanceof Player player) {
                        TowerTeam team = questManager.getChallengeManager().getPlayerTeam(player);
                        if (team != null){
                            QuestManager.resetTeamQuests(team);
                            player.sendMessage(TextUtil.formatText("Reset Quests..."));
                        } else {
                            player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("items")) {
                if (sender.hasPermission("towerchallenge.questbook.items")) {
                    if (sender instanceof Player player) {
                        TowerTeam team = questManager.getChallengeManager().getPlayerTeam(player);
                        if (team != null){
                            QuestManager.resetTeamItems(team);
                            player.sendMessage(TextUtil.formatText("Reset Items..."));
                        } else {
                            player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
                        }
                    }
                }
            }
        }
        
        return true;
    }
}
