package io.github.mystievous.towerchallenge.hats;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.inventory.ItemStack;

public class HatUtil {

    public static final String NBT_TAG = "is_hat";

    /**
     * Sets the value of the "is_hat" NBT tag on an itemstack
     *
     * @param itemStack ItemStack to change
     * @param hatState  State to change is_hat to
     * @return ItemStack, with the updated is_hat tag
     */
    public static ItemStack setHat(TowerChallenge plugin, ItemStack itemStack, Boolean hatState) {
        return NBTUtils.setNoUse(plugin, NBTUtils.setBool(plugin, NBT_TAG, itemStack, hatState));
    }

    /**
     * Tags an ItemStack as a hat
     *
     * @param itemStack ItemStack to change
     * @return ItemStack, with the updated is_hat tag
     */
    public static ItemStack setHat(TowerChallenge plugin, ItemStack itemStack) {
        return setHat(plugin, itemStack, true);
    }

    /**
     * Checks if an itemstack is a hat
     *
     * @param itemStack item to check
     * @return whether the item is a hat
     */
    public static Boolean isHat(ItemStack itemStack) {
        return NBTUtils.boolState(TowerChallenge.getInstance(), NBT_TAG, itemStack);
    }

}
