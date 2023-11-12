package io.github.mctowerchallenge.towerchallenge.magic;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mctowerchallenge.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

/**
 * Represents a wand that players can use to perform actions upon interaction.
 */
public class Wand implements Listener {

    /**
     * The label used for identifying the wand type in NBT data.
     */
    public static final String TYPE_LABEL = "wand-type";

    private final Plugin plugin;
    private final String tag;
    private ItemStack template;
    private final Consumer<PlayerInteractEvent> consumer;

    /**
     * Creates a new Wand instance with the given plugin, tag, and interaction consumer.
     *
     * @param plugin   The plugin that owns the wand.
     * @param tag      The tag used to identify the wand type.
     * @param consumer The consumer that handles the interaction event.
     */
    public Wand(Plugin plugin, String tag, Consumer<PlayerInteractEvent> consumer) {
        this.plugin = plugin;
        this.tag = tag;
        this.consumer = consumer;
        this.template = new ItemStack(Material.STICK);
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    /**
     * Creates a new Wand instance with the given plugin, tag, template, and interaction consumer.
     *
     * @param plugin   The plugin that owns the wand.
     * @param tag      The tag used to identify the wand type.
     * @param template The template ItemStack representing the wand.
     * @param consumer The consumer that handles the interaction event.
     */
    public Wand(Plugin plugin, String tag, ItemStack template, Consumer<PlayerInteractEvent> consumer) {
        this(plugin, tag, consumer);
        this.template = template;
    }

    /**
     * Gets the tag associated with this wand.
     *
     * @return The wand's tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the ItemStack representation of this wand with the proper type tag in NBT.
     *
     * @return The ItemStack representing the wand.
     */
    public ItemStack getItem() {
        return NBTUtils.setString(plugin, TYPE_LABEL, this.template.clone(), tag);
    }

    /**
     * Checks if the given ItemStack matches the wand's type.
     *
     * @param item The ItemStack to check.
     * @return True if the ItemStack matches the wand's type, false otherwise.
     */
    private boolean matchItem(ItemStack item) {
        String type = NBTUtils.getString(plugin, TYPE_LABEL, item);
        return tag.equals(type);
    }

    /**
     * Handles the PlayerInteractEvent and invokes the consumer if applicable.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
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
