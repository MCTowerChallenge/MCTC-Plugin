package io.github.mystievous.towerchallenge.decoration;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A Gui Element for an item of the given model properties
 */
public class CustomModel {

    private final ItemStack item;

    /**
     * @param name            Display Name for the item
     * @param material        Item Type
     * @param customModelData Model ID
     * @param color           Color, for dye-able items
     * @param author          Model author
     * @param debug           Whether the item should have material and model id in the lore
     */
    public CustomModel(Component name, Material material, @Nullable Integer customModelData, @Nullable Color color, @Nullable String author, boolean debug) {
        item = NBTUtils.setNoUse(new ItemStack(material) {{
            ItemMeta meta = getItemMeta();
            meta.displayName(name);
            meta.setCustomModelData(customModelData);
            List<Component> lore = new ArrayList<>();
            if (author != null) {
                lore.add(TextUtil.formatText("Model by " + author));
            }
            if (debug) {
                lore.add(TextUtil.formatText(String.format("%s#%d", material.name().toLowerCase(), customModelData)));
            }
            meta.lore(lore);
            if (color != null && meta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(color.toBukkitColor());
            }
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            setItemMeta(meta);
        }});
    }

    /**
     * @param name            Display Name for the item
     * @param material        Item Type
     * @param customModelData Model ID
     * @param color           Color, for dye-able items
     * @param author          Model author
     * @param debug           Whether the item should have material and model id in the lore
     */
    public CustomModel(String name, Material material, @Nullable Integer customModelData, @Nullable Color color, @Nullable String author, boolean debug) {
        this(TextUtil.noItalic(name), material, customModelData, color, author, debug);
    }

    public ItemStack getItem() {
        return item;
    }

    public ButtonElement getGiveElement() {
        return ButtonElement.createGiveItemButton(item);
    }

}
