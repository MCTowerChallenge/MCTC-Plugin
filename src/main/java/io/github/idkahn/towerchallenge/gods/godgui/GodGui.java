package io.github.idkahn.towerchallenge.gods.godgui;

import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.gods.godgui.regionteleports.WorldsRegionOverview;
import io.github.idkahn.towerchallenge.gui.GuiHeldItem;
import io.github.idkahn.towerchallenge.gui.element.ButtonElement;
import io.github.idkahn.towerchallenge.gui.page.Gui;
import io.github.idkahn.towerchallenge.gui.page.Openable;
import io.github.idkahn.towerchallenge.gui.page.PresetGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GodGui extends PresetGui implements Openable {

    public static final String TEXT_NAME = "God Menu";
    public static final String GUI_ID = "godmenu";
    public static final Component COMPONENT_NAME = Component.text(TEXT_NAME);
    private static final int ROWS = 6;

    private GuiHeldItem guiBook;
    private ChallengeManager challengeManager;
    private WorldsRegionOverview worldsRegionOverview;

    public GodGui(ChallengeManager challengeManager) {
        super(COMPONENT_NAME, ROWS);
        this.challengeManager = challengeManager;
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(Component.text("God Menu").decoration(TextDecoration.ITALIC, false));
        bookMeta.lore(new ArrayList<>(){{
            add(Component.text("Right click with me in your hand").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR));
            add(Component.text("to open the god menu!").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR));
        }});
        bookMeta.setCustomModelData(1);
        book.setItemMeta(bookMeta);
        guiBook = new GuiHeldItem(GUI_ID, book, this);

        ItemStack teleportHistory = new ItemStack(Material.ENDER_EYE);
        ItemMeta teleportHistoryMeta = teleportHistory.getItemMeta();
        teleportHistoryMeta.displayName(Component.text("Teleport History").decoration(TextDecoration.ITALIC, false));
        teleportHistory.setItemMeta(teleportHistoryMeta);
        ButtonElement teleportHistoryElement = new ButtonElement(teleportHistory, player -> challengeManager.getTeleportHistoryManager().getGui().openInventory(player));
        placeElement(1, 1, teleportHistoryElement);

        worldsRegionOverview = new WorldsRegionOverview();
        ItemStack regionTeleports = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta regionTeleportsMeta = regionTeleports.getItemMeta();
        regionTeleportsMeta.displayName(Component.text("Region Teleports").decoration(TextDecoration.ITALIC, false));
        regionTeleports.setItemMeta(regionTeleportsMeta);
        ButtonElement regionTeleportsElement = new ButtonElement(regionTeleports, player -> {
            worldsRegionOverview.getGui().openInventory(player);
        });
        placeElement(2, 1, regionTeleportsElement);
    }

    public ItemStack getGuiItem() {
        return guiBook.getItem();
    }

    @Override
    public Gui getGui() {
        return this;
    }
}
