package io.github.idkahn.towerchallenge;

import de.tr7zw.nbtapi.NBTItem;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class NBTUtils {

    public static final String TEAM = "item_team";
    public static final String UNIQUE_ID = "unique_id";
    public static final String NO_STACK = "no_stack";

    public static ItemStack noStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir())
            return itemStack;
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setUUID(NO_STACK, UUID.randomUUID());
        return nbtItem.getItem();
    }

    public static ItemStack setUniqueID(ItemStack itemStack, UUID uuid) {
        if (itemStack == null || itemStack.getType().isAir())
            return itemStack;
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setUUID(UNIQUE_ID, uuid);
        return nbtItem.getItem();
    }

    public static boolean hasUniqueID(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return false;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey(UNIQUE_ID);
    }

    public static UUID getUniqueID(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getUUID(UNIQUE_ID);
    }

    /**
     * Sets and item's tag to the specified state
     * @param tag tag to set
     * @param itemStack item to set tag on
     * @param tagState state to set tag to
     * @return the item with the tag changed
     */
    public static ItemStack setBool(String tag, ItemStack itemStack, Boolean tagState) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(tag, tagState);
        return nbtItem.getItem();
    }

    /**
     * Sets and item's tag to true
     * @param tag tag to set
     * @param itemStack item to set tag on
     * @return the item with the tag changed
     */
    public static ItemStack setBool(String tag, ItemStack itemStack) {
        return setBool(tag, itemStack, true);
    }

    /**
     * Checks if a certain tag is true on an item
     * @param tag the tag to check
     * @param itemStack the item to check the tag on
     * @return the value of the boolean tag
     */
    public static Boolean boolState(String tag, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return false;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean(tag);
    }

    public static ItemStack setString(String tag, ItemStack itemStack, String string) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString(tag, string);
        return nbtItem.getItem();
    }

    public static String getString(String tag, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getString(tag);
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

    public static ItemStack setInteger(String tag, ItemStack itemStack, int value) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger(tag, value);
        return nbtItem.getItem();
    }

    public static int getInteger(String tag, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getInteger(tag);
    }

}
