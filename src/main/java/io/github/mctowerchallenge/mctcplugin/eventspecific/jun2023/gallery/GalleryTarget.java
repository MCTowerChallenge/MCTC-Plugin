package io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.gallery;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mctowerchallenge.mctcplugin.utility.EntityUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * Individual target for a shooting gallery.
 */
public class GalleryTarget implements Listener {

    // Offset for spawning the armor stand above the target
    private static final Vector OFFSET = new Vector(-0.3125, -1.8125, 0.5);
    // Tag used to identify armor stands as gallery targets
    public static final String TAG = "Jun2023-gallery-target";

    public static void errorNoise(Audience audience, Location location) {
        audience.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.bit"), Sound.Source.PLAYER, 100.0f, 0.529732f), location.x(), location.y(), location.z());
        audience.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.bit"), Sound.Source.PLAYER, 100.0f, 0.749154f), location.x(), location.y(), location.z());
        audience.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.bit"), Sound.Source.PLAYER, 100.0f, 1.059463f), location.x(), location.y(), location.z());
    }

    // Reference to the main plugin
    private final Plugin plugin;
    // Reference to the parent Gallery instance
    private final Gallery gallery;

    private final Location location;
    private Entity target;
    private final UUID uuid;
    private final Collection<BukkitTask> tasks;

    private final int pointValue;
    private boolean positive;
    private boolean active;
    private boolean hit;

    /**
     * Construct a GalleryTarget.
     *
     * @param plugin     The main plugin instance.
     * @param gallery    The parent Gallery instance.
     * @param location   The location of the target.
     * @param pointValue The point value associated with the target.
     */
    public GalleryTarget(Plugin plugin, Gallery gallery, Location location, int pointValue) {
        this.plugin = plugin;
        this.gallery = gallery;
        this.location = location;
        this.tasks = new HashSet<>();
        this.pointValue = pointValue;
        this.positive = true;
        this.active = false;
        this.hit = false;
        this.uuid = UUID.randomUUID();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Get the point value associated with the target.
     *
     * @return The point value.
     */
    public int getPointValue() {
        return pointValue;
    }

    /**
     * Set whether the target represents positive or negative points.
     *
     * @param positive True if positive points, false if negative.
     */
    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    /**
     * Activates the target for a specified number of seconds
     * <p/>
     * NOTE: This is the number of seconds that the
     * target is positioned up, not the number of seconds it is
     * activated in total.
     *
     * @param seconds How long for the target to stay up
     * @throws TargetAlreadyActivateException if the target is currently active
     */
    public void activate(double seconds) throws TargetAlreadyActivateException {
        if (!active) {
            active = true;

            // Spawn an ArmorStand entity at the target location
            ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(OFFSET).setDirection(BlockFace.EAST.getDirection()), EntityType.ARMOR_STAND, false);
            entity.addScoreboardTag(TAG);
            if (positive) {
                entity.getEquipment().setHelmet(Positive.pickRandom().getItem());
            } else {
                entity.getEquipment().setHelmet(Negative.pickRandom().getItem());
            }
            entity.setGravity(false);
            entity.setInvisible(true);
            entity.setInvulnerable(true);
            entity.setDisabledSlots(EquipmentSlot.values());
            NBTUtils.setUniqueID(plugin, NBTUtils.UNIQUE_ID, entity, uuid);
            target = entity;

            // Raise the entity above the ground and set a timer for deactivation
            EntityUtil.raiseEntity(plugin, entity, 1, 0.25, 8, () -> {
                BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, this::deactivate, Math.round(seconds * 20));
                tasks.add(task);
            });
        } else {
            throw new TargetAlreadyActivateException(location);
        }
    }

    /**
     * Deactivate the target, removing it from the world.
     */
    public void deactivate() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
        if (target != null && active) {
            EntityUtil.raiseEntity(plugin, target, -1, 0.25, 8, () -> {
                if (target != null) {
                    target.remove();
                }
                target = null;
                hit = false;
                positive = true;
            });
        } else {
            if (target != null) {
                target.remove();
                target = null;
            }
            hit = false;
            active = false;
            positive = true;
        }
    }

    /**
     * Handle the event when a projectile hits the target.
     *
     * @param event The ProjectileHitEvent.
     */
    @EventHandler
    public void onTargetHit(final ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity hitEntity = event.getHitEntity();
        if (hitEntity != null && !hit && target != null && Objects.equals(hitEntity, target)) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player player) {
                gallery.addShooter(player);
                hit = true;
                if (positive) {
                    int points = gallery.addPoints(getPointValue());
                    gallery.getShooters().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.arrow.hit_player"), Sound.Source.PLAYER, 100.0f, 1.0f), location.x(), location.y(), location.z());
                    gallery.getShooters().sendActionBar(TextUtil.formatText(String.format("+%d Points (%d total)", getPointValue(), points)));
                } else {
                    int points = gallery.addPoints(-1 * getPointValue());
                    errorNoise(gallery.getShooters(), location);
                    gallery.getShooters().sendActionBar(Component.text(String.format("-%d Points (%d total)", getPointValue(), points)).color(Palette.NEGATIVE_COLOR.toTextColor()));
                }
                deactivate();
            }
        }
        projectile.remove();
    }

}
