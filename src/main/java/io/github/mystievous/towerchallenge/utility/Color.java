package io.github.mystievous.towerchallenge.utility;

import net.kyori.adventure.text.format.TextColor;

public class Color extends Number {
    private final int color;

    public Color(int color) {
        this.color = color;
    }

    public Color(String hexColor) throws NumberFormatException {
        hexColor = hexColor.replaceAll("#", "");
        this.color = Integer.valueOf(hexColor, 16);
    }

    public TextColor toTextColor() {
        return TextColor.color(color);
    }

    public org.bukkit.Color toBukkitColor() {

        return org.bukkit.Color.fromRGB(color);
    }

    public String toHexString() {
        return Integer.toHexString(color);
    }

    @Override
    public int intValue() {
        return color;
    }

    @Override
    public long longValue() {
        return color;
    }

    @Override
    public float floatValue() {
        return color;
    }

    @Override
    public double doubleValue() {
        return color;
    }
}
