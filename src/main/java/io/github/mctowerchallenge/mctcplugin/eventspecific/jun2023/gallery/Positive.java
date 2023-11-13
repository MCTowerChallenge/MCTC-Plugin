package io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.gallery;

import io.github.mystievous.mystigui.GuiUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public enum Positive {

    WOLF(GuiUtil.formatItem("Wolf", Material.OBSIDIAN, 4)),
    DISGUISED_WOLF(GuiUtil.formatItem("Disguised Wolf", Material.OBSIDIAN, 5));

    private static final Random RANDOM = new Random();
    public static Positive pickRandom() {
        Positive[] values = values();
        return values[RANDOM.nextInt(values.length)];
    }

    private final ItemStack itemStack;

    Positive(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItem() {
        return itemStack;
    }

}
