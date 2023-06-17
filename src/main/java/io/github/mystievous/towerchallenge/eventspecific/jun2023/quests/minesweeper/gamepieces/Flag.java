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

public class Flag {

    public static ItemStack makeItemFlag(Plugin plugin, ItemStack itemStack) {
        ItemMeta meta = makeItemFlag(plugin, itemStack.getItemMeta());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

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

    public Location getLocation() {
        return location;
    }

    private Location getDisplayLocation() {
        return location.clone().add(0.6, 1.5, 0.566);
    }

    private Location getInteractionLocation() {
        return location.clone().add(0.5, 1, 0.5);
    }

    public void remove() {
        display.remove();
        interaction.remove();
    }

}
