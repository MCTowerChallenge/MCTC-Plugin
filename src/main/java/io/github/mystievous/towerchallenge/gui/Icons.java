package io.github.mystievous.towerchallenge.gui;

import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.Element;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Icons {
    /**
     * Item that covers all of a slot
     * with the blank inventory texture
     */
    public static final Element blankSlot = new Element(new ItemStack(Material.PAPER) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(Component.empty());
        meta.setCustomModelData(2);
        meta.addItemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE);
        setItemMeta(meta);
    }});

    /**
     * Red left arrow item
     *
     * @return the Item
     */
    public static ItemStack backItem() {
        return GuiUtil.formatItem("Back", Material.REDSTONE_BLOCK, 1);
    }

    /**
     * Red X item
     *
     * @return the Item
     */
    public static ItemStack exitItem() {
        return GuiUtil.formatItem("Exit", Material.REDSTONE_BLOCK, 2);
    }
}
