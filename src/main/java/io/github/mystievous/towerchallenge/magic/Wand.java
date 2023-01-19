package io.github.mystievous.towerchallenge.magic;

import io.github.mystievous.towerchallenge.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Wand implements Listener {

    public static final String TYPE_LABEL = "wand-type";

    private final String tag;
    private ItemStack template;
    private final Consumer<PlayerInteractEvent> consumer;

    public Wand(String tag, Consumer<PlayerInteractEvent> consumer) {
        this.tag = tag;
        this.consumer = consumer;
        this.template = new ItemStack(Material.STICK);
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    public Wand(String tag, ItemStack template, Consumer<PlayerInteractEvent> consumer) {
        this(tag, consumer);
        this.template = template;
    }

    public String getTag() {
        return tag;
    }

    public ItemStack getItem() {
        return NBTUtils.setString(TYPE_LABEL, this.template.clone(), tag);
    }

    private boolean matchItem(ItemStack item) {
        String type = NBTUtils.getString(TYPE_LABEL, item);
        return tag.equals(type);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getAction() == Action.PHYSICAL
                || event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK
                || item == null)
            return;
        if (consumer != null && matchItem(item)) {
            consumer.accept(event);
        }
    }

}
