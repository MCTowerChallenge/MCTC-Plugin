package io.github.mystievous.towerchallenge.gui.element;

import io.github.mystievous.towerchallenge.quests.legacy.QuestUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class ButtonElement extends Element implements Clickable {


    // STATIC METHODS AND FIELDS

    /**
     * Red left arrow item
     *
     * @return the Item
     */
    public static ItemStack backItem() {
        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(1);
        exit.setItemMeta(exitMeta);
        return exit;
    }

    /**
     * Red X item
     *
     * @return the Item
     */
    public static ItemStack exitItem() {
        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(2);
        exit.setItemMeta(exitMeta);
        return exit;
    }


    // INSTANCE VARIABLES

    private Consumer<Player> consumer;


    // CONSTRUCTORS

    /**
     * Clickable Button element to go in a GUI
     *
     * @param item     The item to represent the element
     * @param consumer The consumer for the button to run
     */
    public ButtonElement(ItemStack item, Consumer<Player> consumer) {
        super(item);
        this.consumer = consumer;
    }

    public ButtonElement(ItemStack item) {
        this(item, null);
    }

    // ACCESSORS AND MUTATORS

    public void setConsumer(Consumer<Player> consumer) {
        this.consumer = consumer;
    }


    // METHODS

    public void use(Player player) {
        if (consumer != null) {
            consumer.accept(player);
        }
    }

}
