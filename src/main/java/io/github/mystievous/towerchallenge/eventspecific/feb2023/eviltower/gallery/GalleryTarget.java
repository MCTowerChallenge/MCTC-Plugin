package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.gallery;

import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.papermc.paper.event.block.TargetHitEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashSet;

class GalleryTarget implements Listener {

    private final TowerChallenge plugin;
    private final ShootingGallery shootingGallery;

    private final Location targetLocation;
    private final Location targetUpLocation;
    private final Location pistonLocation;
    private final Location redstoneLocation;
    private BlockData targetUpPreviousData;
    private BlockData targetPreviousData;
    private BlockData pistonPreviousData;
    private BlockData redstonePreviousData;
    private final Collection<BukkitTask> tasks;

    private final int pointValue;
    private final double time;
    private boolean active;
    private boolean hit;

    public GalleryTarget(TowerChallenge plugin, ShootingGallery shootingGallery, Location location, int pointValue, double time) {
        this.plugin = plugin;
        this.shootingGallery = shootingGallery;
        this.targetLocation = location;
        this.targetUpLocation = location.clone().add(0, 1, 0);
        this.pistonLocation = location.clone().add(0, -1, 0);
        this.redstoneLocation = location.clone().add(0, -2, 0);
        this.tasks = new HashSet<>();
        this.pointValue = pointValue;
        this.active = false;
        this.hit = false;
        this.time = time;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public int getPointValue() {
        return pointValue;
    }

    public static final Long PISTON_TIME = 3L;
    /**
     * Activates the target for a number of seconds
     * <p/>
     * NOTE: This is the number of seconds that the
     * target is positioned up, not the number of seconds it is
     * activated in total.
     * @param seconds How long for the target to stay up
     * @throws TargetAlreadyActivateException if the target is currently active
     */
    public void activate(double seconds) throws TargetAlreadyActivateException {
        if (!active) {
            active = true;

            Block targetUp = targetUpLocation.getBlock();
            targetUpPreviousData = targetUp.getBlockData().clone();

            Block target = targetLocation.getBlock();
            targetPreviousData = target.getBlockData().clone();
            target.setType(getTargetMaterial(hit));

            Block piston = pistonLocation.getBlock();
            pistonPreviousData = piston.getBlockData().clone();
            piston.setType(Material.STICKY_PISTON);
            Piston pistonData = (Piston) piston.getBlockData();
            pistonData.setFacing(BlockFace.UP);
            piston.setBlockData(pistonData);

            Block redstoneBlock = redstoneLocation.getBlock();
            redstonePreviousData = redstoneBlock.getBlockData().clone();
            redstoneBlock.setType(Material.REDSTONE_BLOCK);

            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                redstoneBlock.setBlockData(redstonePreviousData);
                targetUp.setType(Material.DIRT);
                targetUp.setType(getTargetMaterial(hit));
                BukkitTask disable = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    targetUp.setType(Material.AIR);
                    target.setBlockData(targetPreviousData);
                    piston.setBlockData(pistonPreviousData);
                    active = false;
                    hit = false;
                }, PISTON_TIME);
                tasks.add(disable);
            }, Math.round((seconds*20)+PISTON_TIME));
            tasks.add(task);
        } else {
            throw new TargetAlreadyActivateException(targetLocation);
        }

    }

    public void activate() throws TargetAlreadyActivateException {
        activate(time);
    }

    public void deactivate() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        targetUpLocation.getBlock().setBlockData(targetUpPreviousData);
        targetLocation.getBlock().setBlockData(targetPreviousData);
        pistonLocation.getBlock().setBlockData(pistonPreviousData);
        redstoneLocation.getBlock().setBlockData(redstonePreviousData);
        hit = false;
        active = false;
    }

    private static Material getTargetMaterial(boolean hit) {
        if (hit) {
            return Material.EMERALD_BLOCK;
        }
        return Material.TARGET;
    }

    @EventHandler
    public void onArrowHitTarget(final TargetHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getType() != EntityType.ARROW && projectile.getType() != EntityType.SPECTRAL_ARROW)
            return;
        Block target = event.getHitBlock();
        if (target != null && target.getLocation().equals(targetUpLocation)) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player player) {
                hit = true;
                shootingGallery.addPoints(getPointValue());
                shootingGallery.playerShot(player);
                player.playSound(target.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100.0f, 1.0f);
                player.sendActionBar(Component.text(String.format("+%d Points (%d total)", getPointValue(), shootingGallery.getPoints())).color(Palette.PRIMARY.toTextColor()));
                tasks.add(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    target.setType(Material.DIRT);
                    target.setType(getTargetMaterial(true));
                }, 1L));
            }
        }
        projectile.remove();
    }

}
