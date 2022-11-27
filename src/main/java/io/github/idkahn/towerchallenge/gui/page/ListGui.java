package io.github.idkahn.towerchallenge.gui.page;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.gui.element.ButtonElement;
import io.github.idkahn.towerchallenge.gui.element.Element;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ListGui extends Gui {

    public static int getInventorySize(int NumberOfItems) {
        return 9*(((NumberOfItems-1)/9)+1);
    }

    private final Element lastElement;
    private List<Element> elementList;

    public ListGui(EventManager manager, Component name, List<Element> elementList, Element lastElement) {
        super(name);
        this.elementList = elementList;
        this.lastElement = lastElement;
        loadInventory();
        Bukkit.getPluginManager().registerEvents(this, manager.getPlugin());
    }

    public ListGui(EventManager manager, Component name, Element lastElement) {
        this(manager, name, new ArrayList<>(), lastElement);
    }

    public void setElementList(List<Element> elementList) {
        this.elementList = elementList;
        List<HumanEntity> viewers = getInventory().getViewers();
        loadInventory();
        for (HumanEntity viewer : viewers) {
            openInventory(viewer);
        }
    }

    public void addElement(Element element) {
        this.elementList.add(element);
        List<HumanEntity> viewers = getInventory().getViewers();
        loadInventory();
        for (HumanEntity viewer : viewers) {
            openInventory(viewer);
        }
    }

    @Override
    public void loadInventory() {
        int numItems = elementList.size() + (!lastElement.isAir() ? 1 : 0);
        int invSize = 9;
        if (numItems >= 1) {
            invSize = getInventorySize(numItems);
        }
        Inventory inventory = Bukkit.createInventory(null, invSize, getInventoryTitle());

        for (Element element : elementList) {
            putElement(element.getUUID(), element);
            inventory.addItem(element.getItem());
        }

        if (!lastElement.isAir()) {
            putElement(lastElement.getUUID(), lastElement);
            inventory.setItem(inventory.getSize()-1, lastElement.getItem());
        }
        setInventory(inventory);

    }

}
