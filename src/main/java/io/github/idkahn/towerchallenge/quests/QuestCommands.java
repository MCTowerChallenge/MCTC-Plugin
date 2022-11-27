package io.github.idkahn.towerchallenge.quests;

import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.misc.CommandUtils;
import io.github.idkahn.towerchallenge.towering.TowerCommands;
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

import java.io.IOException;
import java.util.HashMap;

public class QuestCommands implements CommandExecutor {

    private final QuestManager questManager;

    public QuestCommands(QuestManager questManager) {
        this.questManager = questManager;
        questManager.getEventManager().getPlugin().getCommand("questbook").setTabCompleter(new QuestTabComplete());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(questManager.getBook());
                if (!leftoverItems.isEmpty()) {
                    player.sendMessage(Component.text("You must have space in your inventory to get a questbook.").color(NamedTextColor.RED));
                }
            } else {
                sender.sendMessage(Component.text("You must be a player to use this command."));
            }
        } else {

            if (args[0].equalsIgnoreCase("quest")) {
                StringBuilder input = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    input.append(args[i]);
                    input.append(" ");
                }
                String questName = input.substring(0, input.length()-1);

                if (sender instanceof Player) {
                    questManager.getQuest(questName).openInventory((Player) sender);
                }
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("towerchallenge.questbook.reload")) {
                    questManager.loadQuests();
                }
            }

            if (args[0].equalsIgnoreCase("stage")) {
                if (sender.hasPermission("towerchallenge.questbook.stage")) {
                    if (args.length > 1) {
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile);
                        try {
                            int newStage = Integer.parseInt(args[1]);
                            config.set("Current Stage", newStage);
                            config.save(TowerChallenge.questConfigFile);
                            sender.sendMessage(Component.text("Stage set to ").append(Component.text(newStage)));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(Component.text("Please enter a valid stage number."));
                        } catch (IOException e) {
                            Bukkit.getLogger().info(e.getMessage());
                        }
                        questManager.loadQuests();
                    } else {
                        sender.sendMessage(Component.text("Current stage: ")
                                .append(Component.text(YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile).getInt("Current Stage"))));
                    }
                }
            }
        }
        
        return true;
    }
}
