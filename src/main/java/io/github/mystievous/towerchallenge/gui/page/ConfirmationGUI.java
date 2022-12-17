package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class ConfirmationGUI extends PresetGui {

    public ConfirmationGUI(Component title, Consumer<Player> confirm, Consumer<Player> cancel) {
        super(title, 3);

        ItemStack confirmItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.displayName(TextUtil.noItalic("Confirm").color(NamedTextColor.GREEN));
        confirmItem.setItemMeta(confirmMeta);
        ButtonElement confirmElement = new ButtonElement(confirmItem, confirm);
        placeElement(3, 2, confirmElement);

        ItemStack cancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.displayName(TextUtil.noItalic("Cancel").color(NamedTextColor.RED));
        cancelItem.setItemMeta(cancelMeta);
        ButtonElement cancelElement = new ButtonElement(cancelItem, cancel);
        placeElement(7, 2, cancelElement);

    }
}
