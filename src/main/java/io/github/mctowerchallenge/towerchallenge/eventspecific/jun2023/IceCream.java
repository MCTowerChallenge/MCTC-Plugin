package io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023;

import io.github.mctowerchallenge.towerchallenge.quest.util.FullInventory;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mctowerchallenge.towerchallenge.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Random;

/**
 * Handles interactions with ice cream dispensers for the Jun 2023 event.
 */
public class IceCream implements Listener {

    private static final Random RANDOM = new Random();

    /**
     * Get a random item from an array of items.
     *
     * @param items The array of items.
     * @return A randomly selected item from the array.
     */
    private static ItemStack randomItem(ItemStack[] items) {
        return items[RANDOM.nextInt(items.length)];
    }

    public static final Location VANILLA_BUTTON = new Location(Worlds.Jun2023(), 206, 64, -2234);
    public static final String VANILLA_NAME = "Vanilla Ice Cream";
    public static final ItemStack VANILLA_GAY = GuiUtil.formatItem(VANILLA_NAME, Material.POTATO, 6);
    public static final ItemStack VANILLA_CARB = GuiUtil.formatItem(VANILLA_NAME, Material.POTATO, 7);
    public static final ItemStack[] VANILLA_ITEMS = {VANILLA_GAY, VANILLA_CARB};

    public static final Location CHOCOLATE_BUTTON = new Location(Worlds.Jun2023(), 207, 64, -2234);
    public static final String CHOCOLATE_NAME = "Chocolate Ice Cream";
    public static final ItemStack CHOCOLATE_GAY = GuiUtil.formatItem(CHOCOLATE_NAME, Material.POTATO, 8);
    public static final ItemStack CHOCOLATE_CARB = GuiUtil.formatItem(CHOCOLATE_NAME, Material.POTATO, 9);
    public static final ItemStack[] CHOCOLATE_ITEMS = {CHOCOLATE_GAY, CHOCOLATE_CARB};

    public static final Location STRAWBERRY_BUTTON = new Location(Worlds.Jun2023(), 208, 64, -2234);
    public static final String STRAWBERRY_NAME = "Strawberry Ice Cream";
    public static final ItemStack STRAWBERRY_GAY = GuiUtil.formatItem(STRAWBERRY_NAME, Material.POTATO, 10);
    public static final ItemStack STRAWBERRY_CARB = GuiUtil.formatItem(STRAWBERRY_NAME, Material.POTATO, 11);
    public static final ItemStack[] STRAWBERRY_ITEMS = {STRAWBERRY_GAY, STRAWBERRY_CARB};

    public IceCream(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // vanilla: 206 64 -2234
    // chocolate: 207 64 -2234
    // strawberry: 208 64 -2234

    /**
     * Handle player interaction with ice cream dispensers.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onButtonPush(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block != null && event.getAction().isRightClick()) {
            Location location = block.getLocation();

            // Check which dispenser button was clicked and give a random ice cream item
            if (location.equals(VANILLA_BUTTON)) {
                FullInventory.givePlayerItems(player, randomItem(VANILLA_ITEMS));
            } else if (location.equals(CHOCOLATE_BUTTON)) {
                FullInventory.givePlayerItems(player, randomItem(CHOCOLATE_ITEMS));
            } else if (location.equals(STRAWBERRY_BUTTON)) {
                FullInventory.givePlayerItems(player, randomItem(STRAWBERRY_ITEMS));
            }
        }

    }

}
