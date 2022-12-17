package io.github.mystievous.towerchallenge;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemUtil {

    public static Component getItemName(@NotNull ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasDisplayName() ? meta.displayName() : Component.translatable(item.translationKey());
        } else {
            return Component.translatable(item.translationKey());
        }
    }

}
