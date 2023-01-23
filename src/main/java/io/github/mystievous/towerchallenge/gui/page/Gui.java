package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.gui.element.Clickable;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class Gui implements Listener {

    /**
     * Creates an itemstack with the specified values
     * @param name Name of the item
     * @param material Material to make the item
     * @param customModelData Custom model ID of the texture
     * @return The item
     */
    public static @NotNull ItemStack formatItem(@Nullable Component name, @NotNull Material material, @Nullable Integer customModelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(TextUtil.noItalic(name));
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an itemstack with the specified values
     * @param name Name of the item
     * @param material Material to make the item
     * @param customModelData Custom model ID of the texture
     * @return The item
     */
    public static @NotNull ItemStack formatItem(String name, @NotNull Material material, @Nullable Integer customModelData) {
        return formatItem(Component.text(name), material, customModelData);
    }

    private final Component inventoryTitle;

    private Inventory firstInventory;
    private final List<Inventory> inventories;

    private final HashMap<UUID, Element> elements;

    /**
     * Initializes a new GUI
     *
     * @param name          title of the GUI firstInventory
     * @param textureAdjust pixel amount to adjust the character that the graphic is rendered by
     * @param customTexture character that renders the graphic
     * @param titleAdjust   pixel amount to adjust the title, to shift it back over the graphic
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
        inventories = new ArrayList<>();
        elements = new HashMap<>();
    }

    /**
     * Creates a simple gui, with only a title
     *
     * @param name the title of the firstInventory
     */
    public Gui(Component name) {
        inventoryTitle = name;
        inventories = new ArrayList<>();
        elements = new HashMap<>();
    }

    public Component getInventoryTitle() {
        return inventoryTitle;
    }

    public Inventory getFirstInventory() {
        return firstInventory;
    }

    public void registerInventory(Inventory inventory) {
        inventories.add(inventory);
    }

    public void setFirstInventory(Inventory firstInventory) {
        this.firstInventory = firstInventory;
    }

    /**
     * Registers element with the listeners
     *
     * @param uuid    Unique ID of the element
     * @param element Element to add
     */
    public void registerElement(UUID uuid, Element element) {
        elements.put(uuid, element);
    }

    /**
     * Loads the elements into the firstInventory
     */
    public abstract void loadInventory();

    /**
     * Opens the firstInventory for the specified humanEntity
     *
     * @param humanEntity the entity to open the firstInventory for
     */
    public void openInventory(HumanEntity humanEntity) {
        humanEntity.openInventory(firstInventory);
    }

    /**
     * Handles clicks on the firstInventory, cancelling the event to prevent users taking items, and running the action if it is a button.
     *
     * @param event object representing the event
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (!inventories.contains(event.getInventory()))
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
