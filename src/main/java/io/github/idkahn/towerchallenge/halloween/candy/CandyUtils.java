package io.github.idkahn.towerchallenge.halloween.candy;

import io.github.idkahn.towerchallenge.NBTUtils;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import org.bukkit.inventory.ItemStack;

public class CandyUtils {

    public static final String IS_CANDY = "is_candy";
    public static final String IS_BUNDLE = "is_bundle";

    public static ItemStack setCandy(ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(IS_CANDY, itemStack, state);
    }

    public static ItemStack setCandy(ItemStack itemStack) {
        return setCandy(itemStack, true);
    }

    public static Boolean isCandy(ItemStack itemStack) {
        return NBTUtils.boolState(IS_CANDY, itemStack);
    }

    public static ItemStack setBundle(ItemStack itemStack, Boolean state) {
        return NBTUtils.setBool(IS_BUNDLE, itemStack, state);
    }

    public static ItemStack setBundle(ItemStack itemStack) {
        return setBundle(itemStack, true);
    }

    public static Boolean isBundle(ItemStack itemStack) {
        return NBTUtils.boolState(IS_BUNDLE, itemStack);
    }

    public static ItemStack setTeam(ItemStack itemStack, TowerTeam team) {
        return NBTUtils.setTeam(itemStack, team);
    }

    public static String getTeam(ItemStack itemStack) {
        return NBTUtils.getTeam(itemStack);
    }

}
