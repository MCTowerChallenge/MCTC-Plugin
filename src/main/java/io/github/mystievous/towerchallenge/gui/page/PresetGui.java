package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.Element;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class PresetGui extends Gui {

    /**
     * Converts a column and a row (starting at 1)
     * to an index of the inventory slots
     *
     * @param col Column to set the item into
     * @param row Row to set the item into
     * @return The index
     */
    public static int colRowToIndex(int col, int row) {
        return ((row - 1) * 9) + (col - 1);
    }

    /**
     * Converts an inventory index to the corresponding column
     *
     * @param index index of the inventory
     * @return The column
     */
    public static int indexToCol(int index) {
        return (index % 9) + 1;
    }

    /**
     * Converts an inventory index to the corresponding row
     *
     * @param index index of the inventory
     * @return The row
     */
    public static int indexToRow(int index) {
        return index / 9;
    }

    private final int rows;

    public PresetGui(Component name, int textureAdjust, Character customTexture, int titleAdjust, int rows) {
        super(name, textureAdjust, customTexture, titleAdjust);
        this.rows = rows;
        loadInventory();
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    public PresetGui(Component name, int rows) {
        this(name, 0, null, 0, rows);
    }

    @Override
    public void loadInventory() {
        Inventory inventory = Bukkit.createInventory(null, rows * 9, getInventoryTitle());
        setFirstInventory(inventory);
        registerInventory(inventory);
    }

    /**
     * Places an element in a specific slot of the inventory
     *
     * @param col     column to place it in, starting at 1
     * @param row     row to place it in, starting at 1
     * @param element element to place in the gui
     * @throws IndexOutOfBoundsException if the resulting index is not valid for the inventory
     */
    public void placeElement(int col, int row, Element element) throws IndexOutOfBoundsException {
        int index = colRowToIndex(col, row);
        if (0 > index || index >= getFirstInventory().getSize()) {
            throw new IndexOutOfBoundsException("Resulting index is invalid for the inventory");
        }
        registerElement(element.getUUID(), element);
        getFirstInventory().setItem(index, element.getItem());
    }

}
