package io.github.mctowerchallenge.mctcplugin.utility;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class FontUtils {

    public static Component toGuiFont(String string) {
        return Component.text(string).font(Key.key(MCTCPlugin.MCTC_NAMESPACE, "gui"));
    }

    public static Component toEmoteFont(String string) {
        return Component.text(string).font(Key.key(MCTCPlugin.MCTC_NAMESPACE, "emote"));
    }

}
