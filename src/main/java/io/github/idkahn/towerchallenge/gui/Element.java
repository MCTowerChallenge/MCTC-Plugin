package io.github.idkahn.towerchallenge.gui;

import io.github.idkahn.towerchallenge.NBTUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public class Element {

    private UUID uuid;
    private ItemStack item;
    private Consumer<Player> consumer;

    public static Element empty() {
        return new Element(new ItemStack(Material.AIR), null);
    }

    public Element(ItemStack item, Consumer<Player> consumer) {
        this.item = item;
        this.consumer = consumer;
        setUUID(UUID.randomUUID());
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.item = NBTUtils.setUniqueID(item, uuid);
        this.uuid = uuid;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isAir() {
        return item.getType().isAir();
    }

    public void use(Player player) {
        if (consumer != null) {
            consumer.accept(player);
        }
    }

}
