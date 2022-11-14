package io.github.idkahn.towerchallenge.wands;

import io.github.idkahn.towerchallenge.NBTUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WandUtil {

    public static final String WAND_TAG = "is_wand";
    public static final String MAGIC_TAG = "magic_type";


    public enum MagicTypes {
        COW(1),
        LIGHTNING(2),
        FIREWORK(3);

        private final int value;

        MagicTypes(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Sets the value of the "is_wand" NBT tag on an itemstack
     * @param itemStack ItemStack to change
     * @param state State to change is_wand to
     * @return ItemStack, with the updated is_wand tag
     */
    public static ItemStack setWand(ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(WAND_TAG, itemStack, state);
    }

    public static ItemStack setMagic(ItemStack itemStack, int ID, int model) {
        ItemMeta itemMeta = setWand(itemStack).getItemMeta();
        itemMeta.setCustomModelData(model);
        itemStack.setItemMeta(itemMeta);
        return NBTUtils.setInteger(MAGIC_TAG, itemStack, ID);
    }

    public static ItemStack setMagic(ItemStack itemStack, int ID) {
        return setMagic(itemStack, ID, 0);
    }

    /**
     * Tags an ItemStack as a wand
     * @param itemStack ItemStack to change
     * @return ItemStack, with the updated is_wand tag
     */
    public static ItemStack setWand(ItemStack itemStack) {
        return setWand(itemStack, true);
    }

    /**
     * Checks if an itemstack is a wand
     * @param itemStack item to check
     * @return true, if the item is a wand
     */
    public static Boolean isWand(ItemStack itemStack) {
        return NBTUtils.boolState(WAND_TAG, itemStack);
    }

    public static int getMagic(ItemStack itemStack) {
        return NBTUtils.getInteger(MAGIC_TAG, itemStack);
    }

}
