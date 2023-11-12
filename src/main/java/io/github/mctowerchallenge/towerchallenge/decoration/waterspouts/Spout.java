package io.github.mctowerchallenge.towerchallenge.decoration.waterspouts;

import io.github.mctowerchallenge.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * Water Spout, looks best when created
 * on an upwards-facing dispenser.
 * <p>
 * By default, sprays up and down at random
 * intervals, mimicking the ones usually
 * at water parks and such
 */
public class Spout {

    private static final Random RANDOM = new Random();

    /**
     * Seconds it takes for the spout to reach maximum/minimum height
     */
    public static final int SECONDS_RAMP = 3;

    private final Location location;
    private final World world;

    /**
     * @param location the location to create the water spout
     */
    public Spout(@NotNull Location location) {
        this.location = location.add(0, 1, 0);
        world = location.getWorld();
    }

    /**
     * Sprays water particles up from the spout
     *
     * @param height the height to spray the particles
     */
    public void spray(double height) {
        world.spawnParticle(Particle.WATER_SPLASH, location, (int) (height * 15), 0.1d, height, 0.1d, 0.0d);
    }

    /**
     * Runs the spout, with random times
     */
    public void runSpout() {
        long delaySeconds = RANDOM.nextInt(10) + 7;
        Instant start = Instant.now().plusSeconds(delaySeconds);
        Instant end = start.plusSeconds(RANDOM.nextInt(10) + (SECONDS_RAMP * 2 + 5));
        Bukkit.getScheduler().runTaskTimerAsynchronously(TowerChallenge.getInstance(), bukkitTask -> {
            Instant now = Instant.now();
            if (now.isBefore(end)) {
                double height = (RANDOM.nextDouble() * 0.5d) + 1.0d;
//                double height = 0.1d;
                Duration fromStart = Duration.between(start, now);
                Duration fromEnd = Duration.between(now, end);
                if (fromStart.compareTo(Duration.ofSeconds(SECONDS_RAMP)) < 0) {
                    // three seconds or less from start
                    height *= ((double) fromStart.toMillis() / ((double) (1000 * SECONDS_RAMP)));
                } else if (fromEnd.compareTo(Duration.ofSeconds(SECONDS_RAMP)) < 0) {
                    // three seconds or less from end
                    height *= ((double) fromEnd.toMillis() / ((double) (1000 * SECONDS_RAMP)));
                }
                spray(height);
            } else {
                bukkitTask.cancel();
                runSpout();
            }
        }, delaySeconds * 20, 1);
    }

}
