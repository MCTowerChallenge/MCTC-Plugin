package io.github.idkahn.towerchallenge.gui;

import io.github.idkahn.towerchallenge.EventManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ListGui extends Gui {

    public static int getInventorySize(int NumberOfItems) {
        return 9*(((NumberOfItems-1)/9)+1);
    }

    private Element exitElement;
    private List<Element> elementList;

    public ListGui(EventManager manager, Component name, List<Element> elementList, Element exitElement) {
        super(manager, name);
        this.elementList = elementList;
        this.exitElement = exitElement;
        loadInventory();
        Bukkit.getPluginManager().registerEvents(this, manager.getPlugin());
    }

    @Override
    public void loadInventory() {
        int numItems = elementList.size() + (!exitElement.isAir() ? 1 : 0);
        int invSize = getInventorySize(numItems);
        Inventory inventory = Bukkit.createInventory(null, invSize, getInventoryTitle());

        for (Element element : elementList) {
            getElements().put(element.getUUID(), element);
            inventory.addItem(element.getItem());
        }

        if (!exitElement.isAir()) {
            getElements().put(exitElement.getUUID(), exitElement);
            inventory.setItem(inventory.getSize()-1, exitElement.getItem());
        }
        setInventory(inventory);

    }

}
