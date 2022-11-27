package io.github.idkahn.towerchallenge.gui.page;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.gui.element.ButtonElement;
import io.github.idkahn.towerchallenge.gui.element.PlayerElement;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerGui extends ListGui {

    List<OfflinePlayer> playerList;
    ItemStack skullTemplate;

    public PlayerGui(EventManager manager, Component name, ItemStack skullTemplate, Function<OfflinePlayer, List<Component>> loreBuilder, List<OfflinePlayer> playerList, BiConsumer<Player, OfflinePlayer> biConsumer, ButtonElement lastElement) {
        super(manager, name, lastElement);
        this.playerList = playerList;
        this.skullTemplate = skullTemplate;
        for (OfflinePlayer player : playerList) {
            ItemStack skull = new ItemStack(skullTemplate);
            PlayerElement element = new PlayerElement(skull, player, loreBuilder.apply(player), biConsumer);
            addElement(element);
        }
    }

    public PlayerGui(EventManager manager, Component name, ItemStack skullTemplate, List<Component> lore, List<OfflinePlayer> playerList, BiConsumer<Player, OfflinePlayer> biConsumer, ButtonElement lastElement) {
        this(manager, name, skullTemplate, player -> lore, playerList, biConsumer, lastElement);
    }


    }
