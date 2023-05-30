package io.github.mystievous.towerchallenge.quests;

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

public class QuestItems implements Openable {

    private static final Map<String, ItemStack> items = new HashMap<>();

    /**
     * Adds an item to be used
     * in the Quest Item gui.
     *
     * @param tag  {@link String} to TAG the item.
     * @param item The {@link ItemStack} to add.
     */
    public static void putItem(String tag, ItemStack item) {
        items.put(tag, item);
    }

    private final Plugin plugin;

    public QuestItems(Plugin plugin) {

        this.plugin = plugin;

    }

    /**
     * Gets the Quest Item Gui.
     * <p></p>
     * Add items to this with {@link #putItem(String, ItemStack)}.
     *
     * @param player The player for the Gui.
     * @return The Gui.
     */
    @Override
    public Gui getGui(Player player) {
        List<Element> listItems = items.values().stream().map(itemStack -> new ButtonElement(itemStack, player1 -> player1.getInventory().addItem(itemStack))).collect(Collectors.toList());
        return new ListGui(plugin, Component.text("Quest Items"), listItems, Element.blank());
    }
}
