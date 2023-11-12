package io.github.mctowerchallenge.towerchallenge.quest;

import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mystievous.mystigui.page.Openable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages quest items and provides a GUI for them.
 */
public class QuestItems implements Openable {

    private static final Map<String, ItemStack> items = new HashMap<>();

    /**
     * Adds an item to the list of quest items.
     *
     * @param tag  The tag to identify the item.
     * @param item The item to add.
     */
    public static void putItem(String tag, ItemStack item) {
        items.put(tag, item);
    }

    private final Plugin plugin;

    /**
     * Constructs a QuestItems instance.
     *
     * @param plugin The plugin instance.
     */
    public QuestItems(Plugin plugin) {

        this.plugin = plugin;

    }

    /**
     * Creates a GUI for quest items.
     *
     * @param player The player for the GUI.
     * @return The quest items GUI.
     */
    @Override
    public Gui getGui(Player player) {
        List<Element> listItems = items.values().stream().map(itemStack -> new ButtonElement(itemStack, player1 -> player1.getInventory().addItem(itemStack))).collect(Collectors.toList());
        return new ListGui(plugin, Component.text("Quest Items"), listItems, Element.blank());
    }
}
