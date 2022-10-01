package io.github.idkahn.towerchallenge.quests;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class QuestUtil {


    public static final String NBT_TAG = "is_questbook";
    public static final String BUTTON_TAG = "is_button";

    /**
     * Sets the value of the "is_questbook" NBT tag on an itemstack
     * @param itemStack ItemStack to change
     * @param questbookState State to change is_questbook to
     * @return ItemStack, with the updated is_questbook tag
     */
    public static ItemStack setQuestbook(ItemStack itemStack, Boolean questbookState) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(NBT_TAG, questbookState);
        nbtItem.setUUID("id", UUID.randomUUID());
        return nbtItem.getItem();
    }

    /**
     * Tags an ItemStack as a hat
     * @param itemStack ItemStack to change
     * @return ItemStack, with the updated is_questbook tag
     */
    public static ItemStack setQuestbook(ItemStack itemStack) {
        return setQuestbook(itemStack, true);
    }

    /**
     * Checks if an itemstack is a hat
     * @param itemStack
     * @return
     */
    public static Boolean isQuestbook(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean(NBT_TAG);
    }

    public static ItemStack setButton(ItemStack itemStack, Boolean hatState) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(BUTTON_TAG, hatState);
        return nbtItem.getItem();
    }

    public static ItemStack setButton(ItemStack itemStack) {
        return setButton(itemStack, true);
    }

    public static Boolean isButton(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean(BUTTON_TAG);
    }

}
