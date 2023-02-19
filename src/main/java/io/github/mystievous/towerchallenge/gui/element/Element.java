package io.github.mystievous.towerchallenge.gui.element;

import io.github.mystievous.towerchallenge.utility.NBTUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class Element {

    private UUID uuid;
    private ItemStack item;

    /**
     * Empty/air element
     *
     * @return The element
     */
    public static Element empty() {
        return new Element(new ItemStack(Material.AIR));
    }

    public static final Element blankSlot = new Element(new ItemStack(Material.PAPER) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(Component.empty());
        meta.setCustomModelData(2);
        meta.addItemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE);
        setItemMeta(meta);
    }});

    public Element(ItemStack item) {
        this.item = item;
        setUUID(UUID.randomUUID());
    }

    public UUID getUUID() {
        return uuid;
    }

    private void setUUID(UUID uuid) {
        this.item = NBTUtils.setUniqueID(item, uuid);
        this.uuid = uuid;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isAir() {
        return item.getType().isAir();
    }

}
