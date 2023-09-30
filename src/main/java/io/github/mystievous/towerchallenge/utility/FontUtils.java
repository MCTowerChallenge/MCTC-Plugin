package io.github.mystievous.towerchallenge.utility;

import io.github.mystievous.towerchallenge.TowerChallenge;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class FontUtils {

    public static Component toGuiFont(String string) {
        return Component.text(string).font(Key.key(TowerChallenge.MCTC_NAMESPACE, "gui"));
    }

    public static Component toEmoteFont(String string) {
        return Component.text(string).font(Key.key(TowerChallenge.MCTC_NAMESPACE, "emote"));
    }

}
