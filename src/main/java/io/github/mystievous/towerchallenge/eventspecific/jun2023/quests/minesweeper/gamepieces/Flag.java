package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.minesweeper.gamepieces;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.minesweeper.MineHandler;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;

/**
 * Represents a flag used in the Minesweeper game to mark cells.
 */
public class Flag {

    /**
     * Modifies an ItemStack to include the flag-related properties.
     *
     * @param plugin     The plugin instance.
     * @param itemStack  The ItemStack to modify.
     * @return The modified ItemStack.
     */
    public static ItemStack makeItemFlag(Plugin plugin, ItemStack itemStack) {
        ItemMeta meta = makeItemFlag(plugin, itemStack.getItemMeta());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Modifies an ItemMeta to include the flag-related properties.
     *
     * @param plugin The plugin instance.
     * @param meta   The ItemMeta to modify.
     * @return The modified ItemMeta.
     */
    public static ItemMeta makeItemFlag(Plugin plugin, ItemMeta meta) {
        meta.setPlaceableKeys(new HashSet<>(){{
            add(NamespacedKey.minecraft("suspicious_sand"));
            add(NamespacedKey.minecraft("suspicious_gravel"));
        }});
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        NBTUtils.setBool(plugin, Flag.FLAG_TAG, meta, true);
        return meta;
    }

    public static final String FLAG_TAG = "flag";
    public static final String FLAG_LINK_TAG = "flag-link";
    public static final String FLAG_LOCATION_TAG = "flag-location";

    private final ItemDisplay display;
    private final Location location;
    private final Interaction interaction;

    /**
     * Constructs a Flag instance and places it in the world.
     *
     * @param plugin     The plugin instance.
     * @param mineHandler The MineHandler associated with this flag.
     * @param itemStack  The ItemStack to represent the flag.
     * @param location   The location where the flag should be placed.
     */
    public Flag(Plugin plugin, MineHandler mineHandler, ItemStack itemStack, Location location) {
        this.location = location;

        Location displayLocation = getDisplayLocation();
        ItemDisplay display = (ItemDisplay) displayLocation.getWorld().spawnEntity(displayLocation, EntityType.ITEM_DISPLAY);
        display.setItemStack(itemStack);
        display.addScoreboardTag(mineHandler.getTeamRemoveTag());
        this.display = display;

        Location interactionLocation = getInteractionLocation();
        Interaction interaction = (Interaction)  interactionLocation.getWorld().spawnEntity(interactionLocation, EntityType.INTERACTION);
        interaction.addScoreboardTag(mineHandler.getTeamRemoveTag());
        interaction.setInteractionHeight(0.5f);
        interaction.setInteractionWidth(1f);
        NBTUtils.setUniqueID(plugin, FLAG_LINK_TAG, interaction, display.getUniqueId());
        NBTUtils.setLocation(plugin, FLAG_LOCATION_TAG, interaction, location);
        this.interaction = interaction;
    }

    /**
     * Gets the location of the flag anchor.
     *
     * @return The location of the flag anchor.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the location of the flag display.
     *
     * @return The location of the flag display.
     */
    private Location getDisplayLocation() {
        return location.clone().add(0.6, 1.5, 0.566);
    }

    /**
     * Gets the location of the flag interaction.
     *
     * @return The location of the flag interaction.
     */
    private Location getInteractionLocation() {
        return location.clone().add(0.5, 1, 0.5);
    }

    /**
     * Removes the flag from the world.
     */
    public void remove() {
        display.remove();
        interaction.remove();
    }

}
