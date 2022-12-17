package io.github.mystievous.towerchallenge.gui.element;

import io.github.mystievous.towerchallenge.NBTUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class Element {

    private UUID uuid;
    private ItemStack item;

    public static Element empty() {
        return new Element(new ItemStack(Material.AIR));
    }

    public Element(ItemStack item) {
        this.item = item;
        setUUID(UUID.randomUUID());
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
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
