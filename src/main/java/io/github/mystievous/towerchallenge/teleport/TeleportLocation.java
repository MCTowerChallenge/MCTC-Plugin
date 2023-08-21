package io.github.mystievous.towerchallenge.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a location with teleportation details.
 */
public class TeleportLocation extends Location {

    // Death = Red Mushroom Custom Model 8
    // Teleport = Ender Pearl

    /**
     * Enumeration of reasons for teleportation, each associated with a specific item.
     */
    public enum Reason {
        DEATH(deathItem()),
        TELEPORT(teleportItem()),
        PORTAL(portalItem()),
        PEARL(pearlItem());

        private static ItemStack deathItem() {
            ItemStack item = new ItemStack(Material.RED_MUSHROOM);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(8);
            meta.displayName(Component.text("Death").decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
            return item;
        }
        private static ItemStack teleportItem() {
            ItemStack item = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text("Teleport").decoration(TextDecoration.ITALIC, false));
            meta.addEnchant(Enchantment.MENDING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            return item;
        }
        private static ItemStack portalItem() {
            ItemStack item = new ItemStack(Material.ENDER_EYE);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text("Portal").decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
            return item;
        }
        private static ItemStack pearlItem() {
            ItemStack item = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text("Enderpearl").decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
            return item;
        }

        private final ItemStack item;

        /**
         * Constructs a Reason enum with the associated item.
         *
         * @param item The ItemStack associated with the reason.
         */
        Reason(ItemStack item) {
            this.item = item;
        }

        /**
         * Gets the ItemStack associated with the reason.
         *
         * @return The associated ItemStack.
         */
        public ItemStack getItem() {
            return new ItemStack(item);
        }
    }

    private final Reason reason;
    private final PlayerTeleportEvent.TeleportCause cause;

    /**
     * Constructs a TeleportLocation with the given location, reason, and teleport cause.
     *
     * @param location The base location.
     * @param reason   The reason for teleportation.
     * @param cause    The teleport cause.
     */
    public TeleportLocation(Location location, Reason reason, @Nullable PlayerTeleportEvent.TeleportCause cause) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.reason = reason;
        this.cause = cause;
    }

    /**
     * Gets the biome of the location.
     *
     * @return The biome.
     */
    public Biome getBiome() {
        return getWorld().getBiome(this);
    }

    /**
     * Gets the reason for teleportation.
     *
     * @return The reason.
     */
    public Reason getReason() {
        return reason;
    }

    /**
     * Gets the teleport cause.
     *
     * @return The teleport cause.
     */
    public PlayerTeleportEvent.TeleportCause getCause() {
        return cause;
    }
}
