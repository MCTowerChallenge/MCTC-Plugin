package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.NBTUtils;
import io.github.mystievous.towerchallenge.gui.element.Clickable;
import io.github.mystievous.towerchallenge.gui.element.Element;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.HumanEntity;
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

    /**
     * Initializes a new GUI
     * @param name title of the GUI inventory
     * @param textureAdjust pixel amount to adjust the character that the graphic is rendered by
     * @param customTexture character that renders the graphic
     * @param titleAdjust pixel amount to adjust the title, to shift it back over the graphic
     */
    public Gui(Component name, int textureAdjust, Character customTexture, int titleAdjust) {
        TextComponent.Builder titleBuilder = Component.text();
        if (textureAdjust != 0) {
            titleBuilder.append(Component.translatable(String.format("space.%d", textureAdjust)));
        }
        if (customTexture != null) {
            titleBuilder.append(Component.text(customTexture).color(NamedTextColor.WHITE));
        }
        if (titleAdjust != 0) {
            titleBuilder.append(Component.translatable(String.format("space.%d", titleAdjust)));
        }
        titleBuilder.append(name);
        inventoryTitle = titleBuilder.build();
        elements = new HashMap<>();
    }

    /**
     * Creates a simple gui, with only a title
     * @param name the title of the inventory
     */
    public Gui(Component name) {
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

    /**
     * Registers element with the listeners
     * @param uuid Unique ID of the element
     * @param element Element to add
     */
    public void putElement(UUID uuid, Element element) {
        elements.put(uuid, element);
    }

    /**
     * Loads the elements into the inventory
     */
    public abstract void loadInventory();

    /**
     * Opens the inventory for the specified humanEntity
     * @param humanEntity the entity to open the inventory for
     */
    public void openInventory(HumanEntity humanEntity) {
        humanEntity.openInventory(inventory);
    }

    /**
     * Handles clicks on the inventory, cancelling the event to prevent users taking items, and running the action if it is a button.
     * @param event object representing the event
     */
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
        if (element instanceof Clickable clickElement) {
            clickElement.use(player);
        }
    }

}
