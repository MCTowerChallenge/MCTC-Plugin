package io.github.idkahn.towerchallenge.gui.element;

import io.github.idkahn.towerchallenge.gui.page.Gui;
import io.github.idkahn.towerchallenge.gui.page.Openable;
import io.github.idkahn.towerchallenge.quests.QuestUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.function.Consumer;

public class ButtonElement extends Element implements Clickable {

    public static ItemStack backItem() {
        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(1);
        exit.setItemMeta(exitMeta);
        return exit;
    }

    public static ItemStack exitItem() {
        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(2);
        exit.setItemMeta(exitMeta);
        return exit;
    }

    private final Consumer<Player> consumer;

    public ButtonElement(ItemStack item, Consumer<Player> consumer) {
        super(item);
        this.consumer = consumer;
    }

    public void use(Player player) {
        if (consumer != null) {
            consumer.accept(player);
        }
    }

}
