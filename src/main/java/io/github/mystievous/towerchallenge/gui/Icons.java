package io.github.mystievous.towerchallenge.gui;

import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.quests.legacy.QuestUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Icons {
    public static final Element blankSlot = new Element(new ItemStack(Material.PAPER) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(Component.empty());
        meta.setCustomModelData(2);
        meta.addItemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE);
        setItemMeta(meta);
    }});

    /**
     * Red left arrow item
     *
     * @return the Item
     */
    public static ItemStack backItem() {
        ItemStack exit = QuestUtil.setButton(TowerChallenge.getInstance(), new ItemStack(Material.REDSTONE_BLOCK));
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
        ItemStack exit = QuestUtil.setButton(TowerChallenge.getInstance(), new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(2);
        exit.setItemMeta(exitMeta);
        return exit;
    }
}
