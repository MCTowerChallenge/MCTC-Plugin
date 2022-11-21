package io.github.idkahn.towerchallenge.hats;

import io.github.idkahn.towerchallenge.NBTUtils;
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
        return NBTUtils.setBool(NBT_TAG, itemStack, hatState);
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
     * @param itemStack item to check
     * @return whether the item is a hat
     */
    public static Boolean isHat(ItemStack itemStack) {
        return NBTUtils.boolState(NBT_TAG, itemStack);
    }

}
