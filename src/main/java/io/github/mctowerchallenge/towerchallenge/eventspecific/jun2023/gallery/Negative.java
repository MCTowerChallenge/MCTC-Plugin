package io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.gallery;

import io.github.mystievous.mystigui.GuiUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public enum Negative {
    CHICKEN(GuiUtil.formatItem("Chicken", Material.OBSIDIAN, 10)),
    BROWN_COW(GuiUtil.formatItem("Brown Cow", Material.OBSIDIAN, 8)),
    COW(GuiUtil.formatItem("Cow", Material.OBSIDIAN, 8)),
    GRAY_SHEEP(GuiUtil.formatItem("Gray Sheep", Material.OBSIDIAN, 7)),
    SHEEP(GuiUtil.formatItem("Sheep", Material.OBSIDIAN, 6));

    private static final Random RANDOM = new Random();
    public static Negative pickRandom() {
        Negative[] values = values();
        return values[RANDOM.nextInt(values.length)];
    }

    private final ItemStack itemStack;

    Negative(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItem() {
        return itemStack;
    }
}
