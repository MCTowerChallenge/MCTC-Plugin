package io.github.idkahn.towerchallenge.Wands;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WandUtil {

    public static final String WAND_TAG = "is_wand";
    public static final String MAGIC_TAG = "magic_type";


    public enum MagicTypes {
        LIGHTNING(0),
        COW(1);

        private int value;

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
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(WAND_TAG, state);
        return nbtItem.getItem();
    }

    public static ItemStack setMagic(ItemStack itemStack, int ID) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(ID);
        itemStack.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger(MAGIC_TAG, ID);
        return nbtItem.getItem();
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
     * Checks if an itemstack is a hat
     * @param itemStack
     * @return
     */
    public static Boolean isWand(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean(WAND_TAG);
    }

    public static int getMagic(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getInteger(MAGIC_TAG);
    }

}
