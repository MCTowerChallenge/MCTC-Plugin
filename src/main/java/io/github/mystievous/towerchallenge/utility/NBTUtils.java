package io.github.mystievous.towerchallenge.utility;

import de.tr7zw.nbtapi.NBTItem;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static ItemStack setUniqueID(String tag, ItemStack itemStack, UUID uuid) {
        if (itemStack == null || itemStack.getType().isAir())
            return itemStack;
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setUUID(tag, uuid);
        return nbtItem.getItem();
    }

    public static ItemStack setUniqueID(ItemStack itemStack, UUID uuid) {
        return setUniqueID(UNIQUE_ID, itemStack, uuid);
    }

    public static boolean hasUniqueID(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return false;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey(UNIQUE_ID);
    }

    public static UUID getUniqueID(String tag, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getUUID(tag);
    }

    public static UUID getUniqueID(ItemStack itemStack) {
        return getUniqueID(UNIQUE_ID, itemStack);
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

    public static @Nullable String getString(String tag, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getString(tag);
    }

    public static boolean hasTeam(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return false;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey(TEAM);
    }

    public static ItemStack setTeam(ItemStack itemStack, @NotNull TowerTeam team) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString(TEAM, team.getTextName());
        return nbtItem.getItem();
    }

    public static boolean matchTeam(ItemStack itemStack, TowerTeam team) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return false;
        }
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getString(TEAM).equals(team.getTextName());
    }

}
