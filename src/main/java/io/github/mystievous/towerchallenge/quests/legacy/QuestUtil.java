package io.github.mystievous.towerchallenge.quests.legacy;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.inventory.ItemStack;

public class QuestUtil {

    public static final String BUTTON_TAG = "is_button";
    public static final String VOUCHER_TAG = "is_voucher";

    public static ItemStack setButton(TowerChallenge plugin, ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(plugin, BUTTON_TAG, itemStack, state);
    }

    public static ItemStack setButton(TowerChallenge plugin, ItemStack itemStack) {
        return setButton(plugin, itemStack, true);
    }

    public static ItemStack setVoucher(TowerChallenge plugin, ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(plugin, VOUCHER_TAG, itemStack, state);
    }

    public static ItemStack setVoucher(TowerChallenge plugin, ItemStack itemStack) {
        return setVoucher(plugin, itemStack, true);
    }

}
