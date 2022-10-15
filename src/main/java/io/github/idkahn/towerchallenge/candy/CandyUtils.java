package io.github.idkahn.towerchallenge.candy;

import de.tr7zw.nbtapi.NBTItem;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import org.bukkit.inventory.ItemStack;

public class CandyUtils {

    public static final String IS_CANDY = "is_candy";
    public static final String IS_BUNDLE = "is_bundle";
    public static final String TEAM = "candy_team";

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

    public static ItemStack setBundle(ItemStack itemStack, Boolean state) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(IS_BUNDLE, state);
        return nbtItem.getItem();
    }

    public static ItemStack setBundle(ItemStack itemStack) {
        return setBundle(itemStack, true);
    }

    public static Boolean isBundle(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return false;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean(IS_BUNDLE);
    }

    public static ItemStack setTeam(ItemStack itemStack, TowerTeam team) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString(TEAM, team.getTeam().getName());
        return nbtItem.getItem();
    }

    public static String getTeam(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getString(TEAM);
    }

}
