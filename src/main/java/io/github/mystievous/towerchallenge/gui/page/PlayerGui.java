package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.PlayerElement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerGui extends ListGui {

    public PlayerGui(Component name, Function<OfflinePlayer, List<Component>> loreBuilder, List<OfflinePlayer> playerList, BiConsumer<Player, OfflinePlayer> biConsumer, ButtonElement lastElement) {
        super(name, lastElement);
        for (OfflinePlayer player : playerList) {
            if (player != null) {
                PlayerElement element = new PlayerElement(player, loreBuilder.apply(player), biConsumer);
                addElement(element);
            }
        }
    }

}
