package io.github.mctowerchallenge.mctcplugin.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import io.github.mystievous.mysticore.Palette;

public class CommandUtils {

    public static final Component SENDER_NOT_PLAYER = Component.text("You must be a player to run this command.");
    public static final Component PLAYER_DOES_NOT_EXIST = Component.text("That player does not exist!").color(NamedTextColor.DARK_RED);

    public static Component errorMessage(Component text) {
        return text.color(Palette.NEGATIVE_COLOR.toTextColor());
    }

    public static Component errorMessage(String text) {
        return errorMessage(Component.text(text));
    }

}
