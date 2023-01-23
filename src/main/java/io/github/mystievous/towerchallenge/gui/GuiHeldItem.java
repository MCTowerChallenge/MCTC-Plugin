package io.github.mystievous.towerchallenge.gui;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.Openable;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GuiHeldItem implements Listener {

    public static final String GUI_ID = "gui_id";

    private final ItemStack item;
    private final Openable openable;
    private final String guiId;
    private String permission;

    public GuiHeldItem(String guiId, ItemStack item, Openable openable) {
        this.guiId = guiId;
        this.item = NBTUtils.noStack(NBTUtils.setString(GUI_ID, item, this.guiId));
        this.openable = openable;
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    /**
     * Sets the permission required to use this item
     *
     * @param permission The permission string
     */
    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }

    /**
     * @param player player to pass into gui
     * @return the gui that is opened by this item
     */
    public Gui getGui(Player player) {
        return openable.getGui(player);
    }

    public ItemStack getItem() {
        return item;
    }

    /**
     * Checks if an items matches the criteria to open the gui
     *
     * @param item item to check
     * @return true, if the item has the proper tag for the gui
     */
    public boolean matchItem(ItemStack item) {
        return NBTUtils.getString(GUI_ID, item).equals(guiId);
    }

    public void openInventory(Player player) {
        openable.getGui(player).openInventory(player);
    }

    /**
     * Disallows players to craft using the item
     */
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled())
            return;
        CraftingInventory inventory = event.getInventory();
        for (ItemStack item : inventory.getMatrix()) {
            if (matchItem(item)) {
                event.getWhoClicked().sendMessage(Component.text("Nice try ;)"));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getAction() == Action.PHYSICAL
                || event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK
                || item == null)
            return;
        if (matchItem(item) && (permission == null || player.hasPermission(permission))) {
            openable.getGui(player).openInventory(player);
        }
    }

}
