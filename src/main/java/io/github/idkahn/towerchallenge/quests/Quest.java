package io.github.idkahn.towerchallenge.quests;

import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Quest implements Listener {

    // Name
    // Description
    // Completion Criteria
    // Reward
    // Is Completed
    // Who Completed

    private final QuestManager questManager;
    private final Component name;
    private final ArrayList<Component> description;
    private final ArrayList<ItemStack> criteria;
    private final ArrayList<ItemStack> reward;
    private final ParticipantTeam completed;
    private Inventory inventory;
    private Inventory completeUI;

    public Quest(QuestManager questManager, Component name, ArrayList<Component> description, ArrayList<ItemStack> criteria, ArrayList<ItemStack> reward, ParticipantTeam completed) {
        this.questManager = questManager;
        this.name = name;
        this.description = description;
        this.criteria = criteria;
        this.reward = reward;
        this.completed = completed;
        initInventory();
        initCompleteUI();
        Bukkit.getServer().getPluginManager().registerEvents(this, questManager.getEventManager().getPlugin());
    }

    private void initInventory() {
        inventory = Bukkit.createInventory(null, 54, Component.text("\uF808\uE002\uF80C\uF80A\uF808\uF802").color(NamedTextColor.WHITE).append(name.color(NamedTextColor.BLACK)));

        ItemStack backButton = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.displayName(Component.text("Back").decoration(TextDecoration.ITALIC, false));
        backButtonMeta.setCustomModelData(1);
        backButton.setItemMeta(backButtonMeta);
        inventory.setItem(36, backButton);

        ItemStack descriptionItem = new ItemStack(Material.PAPER);
        ItemMeta descriptionMeta = descriptionItem.getItemMeta();
        descriptionMeta.displayName(Component.text(" "));
        ArrayList<Component> lore = new ArrayList<>(this.description);
        lore.add(Component.empty());
        descriptionMeta.lore(lore);
        descriptionItem.setItemMeta(descriptionMeta);

        inventory.setItem(9, descriptionItem);

        ItemStack completedItem;
        if (completed != null) {
            completedItem = new ItemStack(Material.valueOf(completed.getDye()+"_CONCRETE"));
            ItemMeta completedMeta = completedItem.getItemMeta();
            completedMeta.displayName(completed.getDisplayName().decoration(TextDecoration.ITALIC, false));
            completedMeta.setCustomModelData(1);
            completedItem.setItemMeta(completedMeta);
        } else {
            completedItem = new ItemStack(Material.BARRIER);
            ItemMeta completedMeta = completedItem.getItemMeta();
            completedMeta.displayName(Component.text("Not Completed Yet").decoration(TextDecoration.ITALIC, false));
            completedItem.setItemMeta(completedMeta);
        }
        inventory.setItem(17, QuestUtil.setButton(completedItem));

        for (int i = 0; i < criteria.size(); i++) {
            ItemStack item = criteria.get(i);
            if (i < 3) {
                inventory.setItem(28+i, item);
            } else if (i < 6) {
                inventory.setItem(37+i-3, item);
            } else {
                Bukkit.getLogger().info("Quest "+getTextName()+" has too many criteria items. Maximum of 6.");
            }
        }

        for (int i = 0; i < reward.size(); i++) {
            ItemStack item = QuestUtil.setButton(reward.get(i));
            if (i < 3) {
                inventory.setItem(32+i, item);
            } else if (i < 6) {
                inventory.setItem(41+i-3, item);
            } else {
                Bukkit.getLogger().info("Quest "+getTextName()+" has too many reward items. Maximum of 6.");
            }

        }

    }

    public void initCompleteUI() {
        HashMap<String, ParticipantTeam> teams = questManager.getEventManager().getTowerListener().getTeams();
        completeUI = Bukkit.createInventory(null, HatGUI.getInventorySize(teams.size()+2), Component.text("Completed By: "));

        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(2);
        exit.setItemMeta(exitMeta);
        completeUI.setItem(completeUI.getSize()-1, exit);

        for (Map.Entry<String, ParticipantTeam> entry : teams.entrySet()) {
            ParticipantTeam team = entry.getValue();
            ItemStack item = QuestUtil.setButton(new ItemStack(Material.valueOf(team.getDye()+"_CONCRETE")));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.displayName(team.getDisplayName().decoration(TextDecoration.ITALIC, false));
            itemMeta.setCustomModelData(1);
            item.setItemMeta(itemMeta);
            completeUI.addItem(item);
        }

        ItemStack unset = QuestUtil.setButton(new ItemStack(Material.BARRIER));
        ItemMeta unsetMeta = unset.getItemMeta();
        unsetMeta.displayName(Component.text("None").decoration(TextDecoration.ITALIC, false));
        unset.setItemMeta(unsetMeta);
        completeUI.addItem(unset);
    }

    public void closeInventories() {
        inventory.close();
        completeUI.close();
    }

    public String getTextName() {
        return PlainTextComponentSerializer.plainText().serialize(name);
    }
    public List<Player> getViewers() {
        ArrayList<HumanEntity> players = new ArrayList<>();
        players.addAll(inventory.getViewers());
        players.addAll(completeUI.getViewers());
        return players.stream().map((entity) -> (Player) entity).collect(Collectors.toList());
    }

    public Component getName() {
        return name;
    }

    public ItemStack getItem() {
        ItemStack item = QuestUtil.setButton(new ItemStack(Material.PAPER));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(name.decoration(TextDecoration.ITALIC, false));
        itemMeta.lore(description);
        if (completed != null) {
            itemMeta.setCustomModelData(1);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }
    public void openCompleteUI(Player player) {
        player.openInventory(completeUI);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir())
            return;
        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            ItemStack item = event.getCurrentItem();
            if (QuestUtil.isButton(item)) {
                if (item.getType().equals(Material.REDSTONE_BLOCK)) {
                    questManager.openQuestPicker(player);
                } else if (event.getSlot() == 17) {
                    if (player.hasPermission("towerchallenge.quest.complete")) {
                        openCompleteUI(player);
                    }
                } else {
                    if (player.hasPermission("towerchallenge.quest.getreward")) {
                        if (QuestUtil.isVoucher(item)) {
                            player.getInventory().addItem(BlockVoucher.getVouchers(2));
                        } else {
                            player.getInventory().addItem(item);
                        }
                    }
                }
            }
        } else if (event.getInventory().equals(completeUI)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            ItemStack item = event.getCurrentItem();
            if (QuestUtil.isButton(item)) {
                if (event.getSlot() == completeUI.getSize()-1) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(questManager.getEventManager().getPlugin(), player::closeInventory, 1);
                } else {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile);
                    try {
                        if (item.getType().equals(Material.BARRIER)) {
                            String questName = PlainTextComponentSerializer.plainText().serialize(name);
                            config.set("Quests."+questName+".completed", null);
                            config.save(TowerChallenge.questConfigFile);
                            player.sendMessage(name.append(Component.text(" set to incomplete.")));
                        } else {
                            String questName = PlainTextComponentSerializer.plainText().serialize(name);
                            String teamName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(item.getItemMeta().displayName()));
                            ParticipantTeam team = questManager.getEventManager().getTowerListener().getTeam(teamName);
                            config.set("Quests."+questName+".completed", teamName);
                            config.save(TowerChallenge.questConfigFile);
                            player.sendMessage(name.append(Component.text(" set to complete by ")).append(Component.text(teamName)).append(Component.text(".")));
                            Bukkit.getServer().sendMessage(
                                    Component.text(questName, NamedTextColor.AQUA)
                                            .clickEvent(ClickEvent.runCommand("/qb quest "+questName))
                                            .hoverEvent(HoverEvent.showText(Component.text("Open Quest Page")))
                                            .append(Component.text(" has been completed by ", NamedTextColor.WHITE))
                                            .append(team.getDisplayName().color(team.getTextColor()))
                            );
                            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.MASTER, 100, 1));
                            // entity.player.levelup
                        }
                        questManager.loadQuests();
                        Bukkit.getScheduler().scheduleSyncDelayedTask(questManager.getEventManager().getPlugin(), () ->
                                questManager.getQuest(PlainTextComponentSerializer.plainText().serialize(name)).openInventory(player), 1);
                    } catch (IOException e) {
                        Bukkit.getLogger().info(e.getMessage());
                    }
                }
            }

        }
    }

}
