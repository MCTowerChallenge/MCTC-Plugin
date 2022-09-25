package io.github.idkahn.towerchallenge.Hats;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class HatItem {

    private Component name;
    private ItemStack item;

    public HatItem(Component name, ItemStack item) {
        this.name = name;
        this.item = item;
    }

    public Component getName() {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
    }
}
