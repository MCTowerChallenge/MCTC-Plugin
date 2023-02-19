package io.github.mystievous.towerchallenge.utility;

import de.tr7zw.nbtapi.NBTItem;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NBTUtils implements Listener {

    public static final String TEAM = "item_team";
    public static final String UNIQUE_ID = "unique_id";
    public static final String NO_STACK = "no_stack";

    public static String toTeamTag(TowerTeam team, String tag) {
        return String.format("%s-%s", team.getServerTeamName(), tag);
    }

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
        if (uuid != null) {
            nbtItem.setUUID(tag, uuid);
        } else {
            nbtItem.removeKey(tag);
        }
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

    public static final String NO_USE_TAG = "no-use";

    public static ItemStack setNoUse(ItemStack itemStack) {
        return setBool(NO_USE_TAG, itemStack);
    }

    public static boolean isNoUse(ItemStack itemStack) {
        return boolState(NO_USE_TAG, itemStack);
    }

    public NBTUtils(TowerChallenge plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCraft(final CraftItemEvent event) {
        if (event.isCancelled())
            return;
        CraftingInventory inventory = event.getInventory();
        for (ItemStack item : inventory.getMatrix()) {
//            event.getWhoClicked().sendMessage("Craft");
            if (isNoUse(item)) {
                event.getWhoClicked().sendMessage(Component.text("You can't craft with that!"));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerShoot(final EntityShootBowEvent event) {
        ItemStack item = event.getConsumable();
        if (isNoUse(item)) {
            event.getEntity().sendMessage(TextUtil.formatText("The arrow falls out of your bow as you try to shoot.").decoration(TextDecoration.ITALIC, true));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCrossbowLoad(final EntityLoadCrossbowEvent event) {
        if (event.getEntity() instanceof InventoryHolder inventoryHolder) {
            for (ItemStack itemStack : inventoryHolder.getInventory().getContents()) {
                if (isNoUse(itemStack)) {
                    event.getEntity().sendMessage(TextUtil.formatText("The arrow falls out of your crossbow as you try to load it.").decoration(TextDecoration.ITALIC, true));
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}
