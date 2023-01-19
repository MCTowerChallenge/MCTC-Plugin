package io.github.mystievous.towerchallenge;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtil {

    public static @Nullable Component noItalic(@Nullable Component component) {
        if (component != null) {
            return component.decoration(TextDecoration.ITALIC, false);
        }
        return null;
    }

    public static Component noItalic(String text) {
        return noItalic(Component.text(text));
    }

    public static Component formatText(Component text) {
        return text.decoration(TextDecoration.ITALIC, false).color(Palette.PRIMARY.getTextColor());
    }

    public static Component formatText(String text) {
        return formatText(Component.text(text));
    }

    public static List<Component> formatTexts(Component... components) {
        return Arrays.stream(components).map(TextUtil::formatText).collect(Collectors.toList());
    }

    public static List<Component> formatTexts(String... text) {
        return Arrays.stream(text).map(TextUtil::formatText).collect(Collectors.toList());
    }

    public static Component getItemName(@NotNull ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasDisplayName() ? meta.displayName() : Component.translatable(item.translationKey());
        } else {
            return Component.translatable(item.translationKey());
        }
    }
}
