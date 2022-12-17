package io.github.mystievous.towerchallenge;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtil {

    public static Component noItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component noItalic(String text) {
        return noItalic(Component.text(text));
    }

    public static Component formatText(Component text) {
        return text.decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR);
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

}
