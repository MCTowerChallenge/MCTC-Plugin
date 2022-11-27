package io.github.idkahn.towerchallenge.gui.element;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public class ButtonElement extends Element implements Clickable {

    private final Consumer<Player> consumer;

    public ButtonElement(ItemStack item, Consumer<Player> consumer) {
        super(item);
        this.consumer = consumer;
    }

    public void use(Player player) {
        if (consumer != null) {
            consumer.accept(player);
        }
    }

}
