package io.github.idkahn.towerchallenge.hats;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class HatUtil {

    public static final String NBT_TAG = "is_hat";

    /**
     * Sets the value of the "is_hat" NBT tag on an itemstack
     * @param itemStack ItemStack to change
     * @param hatState State to change is_hat to
     * @return ItemStack, with the updated is_hat tag
     */
    public static ItemStack setHat(ItemStack itemStack, Boolean hatState) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(NBT_TAG, hatState);
        return nbtItem.getItem();
    }

    /**
     * Tags an ItemStack as a hat
     * @param itemStack ItemStack to change
     * @return ItemStack, with the updated is_hat tag
     */
    public static ItemStack setHat(ItemStack itemStack) {
        return setHat(itemStack, true);
    }

    /**
     * Checks if an itemstack is a hat
     * @param itemStack
     * @return
     */
    public static Boolean isHat(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean(NBT_TAG);
    }

}
