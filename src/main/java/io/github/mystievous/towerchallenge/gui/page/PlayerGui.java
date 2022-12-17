package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.PlayerElement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerGui extends ListGui {

    List<OfflinePlayer> playerList;
    ItemStack skullTemplate;

    public PlayerGui(Component name, ItemStack skullTemplate, Function<OfflinePlayer, List<Component>> loreBuilder, List<OfflinePlayer> playerList, BiConsumer<Player, OfflinePlayer> biConsumer, ButtonElement lastElement) {
        super(name, lastElement);
        this.playerList = playerList;
        this.skullTemplate = skullTemplate;
        for (OfflinePlayer player : playerList) {
            if (player != null) {
                ItemStack skull = new ItemStack(skullTemplate);
                PlayerElement element = new PlayerElement(skull, player, loreBuilder.apply(player), biConsumer);
                addElement(element);
            }
        }
    }

    public PlayerGui(Component name, ItemStack skullTemplate, List<Component> lore, List<OfflinePlayer> playerList, BiConsumer<Player, OfflinePlayer> biConsumer, ButtonElement lastElement) {
        this(name, skullTemplate, player -> lore, playerList, biConsumer, lastElement);
    }

}
