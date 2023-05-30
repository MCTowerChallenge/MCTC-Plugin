package io.github.mystievous.towerchallenge.utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityUtil {

    /**
     * Moves an entity up/down smoothly,
     * given the parameters.
     *
     * @param plugin   The current plugin instance.
     * @param entity   The entity to move.
     * @param height   The height in blocks to
     *                 move the entity.
     * @param seconds  The amount of seconds over which
     *                 to move the entity.
     * @param steps    The number of steps/"resolution"
     *                 to move the entity at.
     * @param callback A {@link Runnable} to run after
     *                 the movement is completed.
     * @throws IllegalArgumentException If the number of steps
     *                                  is less than the total
     *                                  number of ticks, which
     *                                  is the seconds * 20.
     */
    public static void raiseEntity(Plugin plugin, Entity entity, double height, double seconds, int steps, @Nullable Runnable callback) throws IllegalArgumentException {
        if (steps < seconds * 20L) {
            throw new IllegalArgumentException("The number of steps cannot be less than the number of ticks (seconds * 20)");
        }
        long totalTicks = (long) (seconds * 20L);
        long ticksPerStep = totalTicks / steps;
        double heightPerStep = height / steps;
        AtomicInteger count = new AtomicInteger();
        Bukkit.getScheduler().runTaskTimer(plugin, (bukkitTask) -> {
            if (count.get() < steps) {
                count.addAndGet(1);
                entity.teleport(entity.getLocation().add(0, heightPerStep, 0));
            } else {
                bukkitTask.cancel();
                if (callback != null) {
                    callback.run();
                }
            }
        }, 0, ticksPerStep);
    }

}
