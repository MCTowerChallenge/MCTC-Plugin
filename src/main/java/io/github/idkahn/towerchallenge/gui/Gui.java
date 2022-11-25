package io.github.idkahn.towerchallenge.gui;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.NBTUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public abstract class Gui implements Listener {

    private Component inventoryTitle;

    private Inventory inventory;
    private HashMap<UUID, Element> elements;

    public Gui(EventManager manager, Component name, int customTextureWidth, Character customTexture) {
        TextComponent.Builder titleBuilder = Component.text();
        if (customTexture != null) {
            titleBuilder.append(Component.text(customTexture));
        }
        if (customTextureWidth != 0) {
            titleBuilder.append(Component.translatable(String.format("space.%d", -customTextureWidth)));
        }
        titleBuilder.append(name);
        inventoryTitle = titleBuilder.build();
        elements = new HashMap<>();
    }

    public Gui(EventManager manager, Component name) {
        inventoryTitle = name;
        elements = new HashMap<>();
    }

    public Component getInventoryTitle() {
        return inventoryTitle;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public HashMap<UUID, Element> getElements() {
        return elements;
    }

    public abstract void loadInventory();

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (!event.getInventory().equals(inventory))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir())
            return;

        if (!NBTUtils.hasUniqueID(item))
            return;
        UUID itemId = NBTUtils.getUniqueID(item);
        if (!elements.containsKey(itemId))
            return;

        if (!(event.getWhoClicked() instanceof Player player))
            return;

        Element element = elements.get(itemId);
        element.use(player);
    }

}
