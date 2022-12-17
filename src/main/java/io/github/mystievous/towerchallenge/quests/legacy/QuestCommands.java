package io.github.mystievous.towerchallenge.quests.legacy;

import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class QuestCommands implements CommandExecutor {

    private final LegacyQuestManager legacyQuestManager;

    public QuestCommands(LegacyQuestManager legacyQuestManager) {
        this.legacyQuestManager = legacyQuestManager;
        legacyQuestManager.getEventManager().getPlugin().getCommand("questbook").setTabCompleter(new QuestTabComplete());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(legacyQuestManager.getEventManager().getQuestManager().getQuestBook().getItem());
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
                    legacyQuestManager.getQuest(questName).openInventory((Player) sender);
                }
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("towerchallenge.questbook.reload")) {
                    legacyQuestManager.loadQuests();
                }
            }

            if (args[0].equalsIgnoreCase("reset")) {
                if (sender.hasPermission("towerchallenge.questbook.reset")) {
                    if (sender instanceof Player player) {
                        TowerTeam team = legacyQuestManager.getEventManager().getPlayerTeam(player);
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
                        TowerTeam team = legacyQuestManager.getEventManager().getPlayerTeam(player);
                        if (team != null){
                            QuestManager.resetTeamItems(team);
                            player.sendMessage(TextUtil.formatText("Reset Items..."));
                        } else {
                            player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("stage")) {
                if (sender.hasPermission("towerchallenge.questbook.stage")) {
                    if (sender instanceof Player player) {
                        TowerTeam team = legacyQuestManager.getEventManager().getPlayerTeam(player);
                        if (team != null){
                            YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(TowerChallenge.teamDataConfigFile);
                            if (args.length > 1) {
                                QuestManager.setTeamQuest(team, args[1]);
                            }
                            player.sendMessage(TextUtil.formatText("Current Quest: ")
                                    .append(TextUtil.formatText(QuestManager.getTeamQuest(team)).color(NamedTextColor.WHITE)));
                        } else {
                            player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
                        }
                    }
//                    if (args.length > 1) {
//                        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile);
//                        try {
//                            int newStage = Integer.parseInt(args[1]);
//                            config.set("Current Stage", newStage);
//                            config.save(TowerChallenge.questConfigFile);
//                            sender.sendMessage(Component.text("Stage set to ").append(Component.text(newStage)));
//                        } catch (NumberFormatException e) {
//                            sender.sendMessage(Component.text("Please enter a valid stage number."));
//                        } catch (IOException e) {
//                            Bukkit.getLogger().info(e.getMessage());
//                        }
//                        legacyQuestManager.loadQuests();
//                    } else {
//                        sender.sendMessage(Component.text("Current stage: ")
//                                .append(Component.text(YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile).getInt("Current Stage"))));
//                    }
                }
            }
        }
        
        return true;
    }
}
