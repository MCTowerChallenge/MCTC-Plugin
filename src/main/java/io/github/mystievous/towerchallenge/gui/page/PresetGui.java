package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.Element;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class PresetGui extends Gui {

    public static int colRowToIndex(int col, int row) {
        return ((row-1)*9)+(col-1);
    }

    public static int indexToCol(int index) {
        return (index%9)+1;
    }

    public static int indexToRow(int index) {
        return index/9;
    }

    private final int rows;

    public PresetGui(Component name, int textureAdjust, Character customTexture, int titleAdjust, int rows) {
        super(name, textureAdjust, customTexture, titleAdjust);
        this.rows = rows;
        loadInventory();
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
    }

    public PresetGui(Component name, int rows) {
        this(name, 0, null, 0, rows);
    }

    @Override
    public void loadInventory() {
        setInventory(Bukkit.createInventory(null, rows*9, getInventoryTitle()));
    }

    /**
     * Places an element in a specific slot of the inventory
     * @param col column to place it in, starting at 1
     * @param row row to place it in, starting at 1
     * @param element element to place in the gui
     * @throws IndexOutOfBoundsException if the resulting index is not valid for the inventory
     */
    public void placeElement(int col, int row, Element element) throws IndexOutOfBoundsException {
        int index = colRowToIndex(col, row);
        if (0 > index || index >= getInventory().getSize()) {
            throw new IndexOutOfBoundsException("Resulting index is invalid for the inventory");
        }
        putElement(element.getUUID(), element);
        getInventory().setItem(index, element.getItem());
    }

}
