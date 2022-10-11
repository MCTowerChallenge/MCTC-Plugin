package io.github.idkahn.towerchallenge.candy;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class CandyUtils {

    public static final String IS_CANDY = "is_candy";

    public static ItemStack setCandy(ItemStack itemStack, Boolean state) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(IS_CANDY, state);
        return nbtItem.getItem();
    }

    public static ItemStack setCandy(ItemStack itemStack) {
        return setCandy(itemStack, true);
    }

    public static Boolean isCandy(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return false;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean(IS_CANDY);
    }

}
