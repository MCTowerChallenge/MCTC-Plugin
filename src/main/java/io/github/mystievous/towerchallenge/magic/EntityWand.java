package io.github.mystievous.towerchallenge.magic;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class EntityWand implements Listener {

    public static final String TYPE_LABEL = "wand-type";

    private final Plugin plugin;
    private final String tag;
    private ItemStack template;
    private final Consumer<PlayerInteractEntityEvent> consumer;

    public EntityWand(Plugin plugin, String tag, Consumer<PlayerInteractEntityEvent> consumer) {
        this.plugin = plugin;
        this.tag = tag;
        this.consumer = consumer;
        this.template = new ItemStack(Material.STICK);
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    public EntityWand(Plugin plugin, String tag, ItemStack template, Consumer<PlayerInteractEntityEvent> consumer) {
        this(plugin, tag, consumer);
        this.template = template;
    }

    public String getTag() {
        return tag;
    }

    public ItemStack getItem() {
        return NBTUtils.setString(plugin, TYPE_LABEL, this.template.clone(), tag);
    }

    private boolean matchItem(ItemStack item) {
        String type = NBTUtils.getString(plugin, TYPE_LABEL, item);
        return tag.equals(type);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (consumer != null && matchItem(item)) {
            consumer.accept(event);
        }
    }

}
