package io.github.mystievous.towerchallenge.quests.legacy;

import io.github.mystievous.towerchallenge.utility.NBTUtils;
import org.bukkit.inventory.ItemStack;

public class QuestUtil {


    public static final String NBT_TAG = "is_questbook";
    public static final String BUTTON_TAG = "is_button";
    public static final String VOUCHER_TAG = "is_voucher";

    /**
     * Sets the value of the "is_questbook" NBT tag on an itemstack
     *
     * @param itemStack      ItemStack to change
     * @param questbookState State to change is_questbook to
     * @return ItemStack, with the updated is_questbook tag
     */
    public static ItemStack setQuestbook(ItemStack itemStack, Boolean questbookState) {
        return NBTUtils.setBool(NBT_TAG, itemStack, questbookState);
    }

    /**
     * Tags an ItemStack as a hat
     *
     * @param itemStack ItemStack to change
     * @return ItemStack, with the updated is_questbook tag
     */
    public static ItemStack setQuestbook(ItemStack itemStack) {
        return setQuestbook(itemStack, true);
    }

    public static Boolean isQuestbook(ItemStack itemStack) {
        return NBTUtils.boolState(NBT_TAG, itemStack);
    }

    public static ItemStack setButton(ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(BUTTON_TAG, itemStack, state);
    }

    public static ItemStack setButton(ItemStack itemStack) {
        return setButton(itemStack, true);
    }

    public static Boolean isButton(ItemStack itemStack) {
        return NBTUtils.boolState(BUTTON_TAG, itemStack);
    }

    public static ItemStack setVoucher(ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(VOUCHER_TAG, itemStack, state);
    }

    public static ItemStack setVoucher(ItemStack itemStack) {
        return setVoucher(itemStack, true);
    }

    public static Boolean isVoucher(ItemStack itemStack) {
        return NBTUtils.boolState(VOUCHER_TAG, itemStack);
    }

}
