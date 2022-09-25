package io.github.idkahn.towerchallenge.Wands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WandGUI {

    public WandGUI() {

    }

    public static void getWand(Player player, int id) {
        ItemStack stick = WandUtil.setMagic(WandUtil.setWand(new ItemStack(Material.STICK)), id);
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.displayName(Component.text("Wand").decoration(TextDecoration.ITALIC, false));
        stick.setItemMeta(stickMeta);
        player.getInventory().addItem(stick);
    }

}
