package io.github.idkahn.towerchallenge.quests;

import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Quest implements Listener {

    // Name
    // Description
    // Completion Criteria
    // Reward
    // Is Completed
    // Who Completed

    private QuestManager questManager;
    private Component name;
    private ArrayList<Component> description;
    private ArrayList<ItemStack> criteria;
    private ArrayList<ItemStack> reward;
    private TowerTeam completed;

    private Inventory inventory;

    public Quest(QuestManager questManager, Component name, ArrayList<Component> description, ArrayList<ItemStack> criteria, ArrayList<ItemStack> reward, TowerTeam completed) {
        this.questManager = questManager;
        this.name = name;
        this.description = description;
        this.criteria = criteria;
        this.reward = reward;
        this.completed = completed;
        initInventory();
        Bukkit.getServer().getPluginManager().registerEvents(this, questManager.getEventManager().getPlugin());
    }

    private void initInventory() {
        inventory = Bukkit.createInventory(null, 54, Component.text("\uF808\uE002\uF80C\uF80A\uF808\uF802").color(NamedTextColor.WHITE).append(name.color(NamedTextColor.BLACK)));

        ItemStack backButton = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.displayName(Component.text("Back").decoration(TextDecoration.ITALIC, false));
        backButtonMeta.setCustomModelData(0);
        backButton.setItemMeta(backButtonMeta);
        inventory.setItem(45, backButton);

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
            completedItem.setItemMeta(completedMeta);
        } else {
            completedItem = new ItemStack(Material.BARRIER);
            ItemMeta completedMeta = completedItem.getItemMeta();
            completedMeta.displayName(Component.text("Not Completed Yet").decoration(TextDecoration.ITALIC, false));
            completedItem.setItemMeta(completedMeta);
        }
        inventory.setItem(17, completedItem);

        for (int i = 0; i < criteria.size(); i++) {
            ItemStack item = criteria.get(i);
            inventory.setItem(28+(Math.floorDiv(i,3)*9)+i, item);
        }

        for (int i = 0; i < reward.size(); i++) {
            ItemStack item = reward.get(i);
            inventory.setItem(32+(Math.floorDiv(i,3)*9)+i, item);
        }

    }

    public ItemStack getItem() {
        ItemStack item = QuestUtil.setButton(new ItemStack(Material.PAPER));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(name.decoration(TextDecoration.ITALIC, false));
        itemMeta.lore(description);
        if (completed != null) {
            itemMeta.setCustomModelData(0);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (!event.getInventory().equals(inventory))
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir())
            return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (QuestUtil.isButton(event.getCurrentItem())) {
            questManager.openQuestPicker(player);
        }
    }

}
