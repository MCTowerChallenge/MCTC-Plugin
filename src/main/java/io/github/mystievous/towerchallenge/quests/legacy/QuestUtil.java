package io.github.mystievous.towerchallenge.quests.legacy;

import io.github.mystievous.towerchallenge.utility.NBTUtils;
import org.bukkit.inventory.ItemStack;

public class QuestUtil {

    public static final String BUTTON_TAG = "is_button";
    public static final String VOUCHER_TAG = "is_voucher";

    public static ItemStack setButton(ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(BUTTON_TAG, itemStack, state);
    }

    public static ItemStack setButton(ItemStack itemStack) {
        return setButton(itemStack, true);
    }

    public static ItemStack setVoucher(ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(VOUCHER_TAG, itemStack, state);
    }

    public static ItemStack setVoucher(ItemStack itemStack) {
        return setVoucher(itemStack, true);
    }

}
