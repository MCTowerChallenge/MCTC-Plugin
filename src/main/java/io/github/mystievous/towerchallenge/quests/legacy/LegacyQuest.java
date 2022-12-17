package io.github.mystievous.towerchallenge.quests.legacy;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.ListGui;
import io.github.mystievous.towerchallenge.gui.page.PresetGui;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public class LegacyQuest implements Listener {

    // Name
    // Description
    // Completion Criteria
    // Reward
    // Is Completed
    // Who Completed

    private final LegacyQuestManager legacyQuestManager;
    private final Component name;
    private final ArrayList<Component> description;
    private final ArrayList<ItemStack> criteria;
    private final ArrayList<ItemStack> reward;
    private final ParticipantTeam completed;
    private PresetGui gui;
    private ListGui completeGui;
//    private Inventory completeUI;

    public LegacyQuest(LegacyQuestManager legacyQuestManager, Component name, ArrayList<Component> description, ArrayList<ItemStack> criteria, ArrayList<ItemStack> reward, ParticipantTeam completed) {
        this.legacyQuestManager = legacyQuestManager;
        this.name = name;
        this.description = description;
        this.criteria = criteria;
        this.reward = reward;
        this.completed = completed;
        initInventory();
        initCompleteUI();
        Bukkit.getServer().getPluginManager().registerEvents(this, legacyQuestManager.getEventManager().getPlugin());
    }

    private void initInventory() {
        gui = new PresetGui(name.color(NamedTextColor.BLACK), -8, '\uE002', -170, 6);

        ItemStack backButton = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.displayName(Component.text("Back").decoration(TextDecoration.ITALIC, false));
        backButtonMeta.setCustomModelData(1);
        backButton.setItemMeta(backButtonMeta);
        ButtonElement backElement = new ButtonElement(backButton, legacyQuestManager::openQuestPicker);
        gui.placeElement(1, 5, backElement);

        ItemStack descriptionItem = new ItemStack(Material.PAPER);
        ItemMeta descriptionMeta = descriptionItem.getItemMeta();
        descriptionMeta.displayName(Component.text(" "));
        ArrayList<Component> lore = new ArrayList<>(this.description);
        lore.add(Component.empty());
        descriptionMeta.lore(lore);
        descriptionItem.setItemMeta(descriptionMeta);
        Element descriptionElement = new Element(descriptionItem);
        gui.placeElement(1, 2, descriptionElement);

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
        ButtonElement completedElement = new ButtonElement(completedItem, player -> {
            if (player.hasPermission("towerchallenge.quest.complete")) {
                openCompleteUI(player);
            }
        });
        gui.placeElement(9, 2, completedElement);

        for (int i = 0; i < criteria.size(); i++) {
            ItemStack item = criteria.get(i);
            ButtonElement element = new ButtonElement(item, player -> {
                if (player.hasPermission("towerchallenge.quest.getreward")) {
                    if (QuestUtil.isVoucher(item)) {
                        player.getInventory().addItem(BlockVoucher.getVouchers(2));
                    } else {
                        player.getInventory().addItem(item);
                    }
                }
            });
            if (i < 3) {
                gui.placeElement(2+i, 4, element);
            } else if (i < 6) {
                gui.placeElement(2+i-3, 5, element);
            } else {
                Bukkit.getLogger().info("Quest "+getTextName()+" has too many criteria items. Maximum of 6.");
            }
        }

        for (int i = 0; i < reward.size(); i++) {
            ItemStack item = QuestUtil.setButton(reward.get(i));
            ButtonElement element = new ButtonElement(item, player -> {
                if (player.hasPermission("towerchallenge.quest.getreward")) {
                    if (QuestUtil.isVoucher(item)) {
                        player.getInventory().addItem(BlockVoucher.getVouchers(2));
                    } else {
                        player.getInventory().addItem(item);
                    }
                }
            });
            if (i < 3) {
                gui.placeElement(6+i, 4, element);
            } else if (i < 6) {
                gui.placeElement(6+i-3, 5, element);
            } else {
                Bukkit.getLogger().info("Quest "+getTextName()+" has too many reward items. Maximum of 6.");
            }

        }

    }

    public void initCompleteUI() {
        HashMap<String, ParticipantTeam> teams = legacyQuestManager.getEventManager().getTowerListener().getTeams();

        List<Element> elements = new ArrayList<>();

        for (Map.Entry<String, ParticipantTeam> entry : teams.entrySet()) {
            ParticipantTeam team = entry.getValue();
            ItemStack item = QuestUtil.setButton(team.getItem());
            ButtonElement teamElement = new ButtonElement(item, player -> {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile);
                try {
                    String questName = PlainTextComponentSerializer.plainText().serialize(name);
                    String teamName = team.getTextName();
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
                    legacyQuestManager.loadQuests();
//                    Bukkit.getScheduler().scheduleSyncDelayedTask(questManager.getEventManager().getPlugin(), () ->
//                            this.openInventory(player), 1);
                } catch (IOException e) {
                    Bukkit.getLogger().info(e.getMessage());
                }
            });
            elements.add(teamElement);
        }

        ItemStack unset = QuestUtil.setButton(new ItemStack(Material.BARRIER));
        ItemMeta unsetMeta = unset.getItemMeta();
        unsetMeta.displayName(Component.text("None").decoration(TextDecoration.ITALIC, false));
        unset.setItemMeta(unsetMeta);
        ButtonElement unsetElement = new ButtonElement(unset, player -> {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile);
                String questName = PlainTextComponentSerializer.plainText().serialize(name);
                config.set("Quests."+questName+".completed", null);
                config.save(TowerChallenge.questConfigFile);
                player.sendMessage(name.append(Component.text(" set to incomplete.")));
                Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.MASTER, 100, 1));
                legacyQuestManager.loadQuests();
//                Bukkit.getScheduler().scheduleSyncDelayedTask(questManager.getEventManager().getPlugin(), () ->
//                        this.openInventory(player), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        elements.add(unsetElement);

        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(2);
        exit.setItemMeta(exitMeta);
        ButtonElement exitElement = new ButtonElement(exit, this::openInventory);

        completeGui = new ListGui(Component.text("Completed By: "), elements, exitElement);

    }

    public void closeInventories() {
        gui.getInventory().close();
        completeGui.getInventory().close();
    }

    public PresetGui getGui() {
        return gui;
    }

    public String getTextName() {
        return PlainTextComponentSerializer.plainText().serialize(name);
    }
    public List<Player> getViewers() {
        ArrayList<HumanEntity> players = new ArrayList<>();
        players.addAll(gui.getInventory().getViewers());
        players.addAll(completeGui.getInventory().getViewers());
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
        player.openInventory(gui.getInventory());
    }
    public void openCompleteUI(Player player) {
        completeGui.openInventory(player);
    }

}
