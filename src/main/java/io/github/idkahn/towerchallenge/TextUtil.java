package io.github.idkahn.towerchallenge;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    public static Component formatText(String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR);
    }

    public static List<Component> formatText(String... text) {
        List<Component> components = new ArrayList<>();

        for (String string : text) {
            components.add(formatText(string));
        }

        return components;
    }

}
