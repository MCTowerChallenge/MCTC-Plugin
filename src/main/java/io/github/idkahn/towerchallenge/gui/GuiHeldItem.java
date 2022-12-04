package io.github.idkahn.towerchallenge.gui;

import io.github.idkahn.towerchallenge.NBTUtils;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.gui.page.Gui;
import io.github.idkahn.towerchallenge.gui.page.Openable;
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

public class GuiHeldItem implements Listener {

    public static final String GUI_ID = "gui_id";

    private ItemStack item;
    private Openable openable;
    private String guiId;

    public GuiHeldItem(String guiId, ItemStack item, Openable openable) {
        this.guiId = guiId;
        this.item = NBTUtils.noStack(NBTUtils.setString(GUI_ID, item, this.guiId));
        this.openable = openable;
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
    }

    public Gui getGui() {
        return openable.getGui();
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean matchItem(ItemStack item) {
        return NBTUtils.getString(GUI_ID, item).equals(guiId);
    }

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
        if (matchItem(item)) {
            openable.getGui().openInventory(player);
        }
    }

}
