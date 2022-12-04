package io.github.idkahn.towerchallenge.misc.waterspouts;

import io.github.idkahn.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SpoutManager {

    private static final SecureRandom RANDOM = new SecureRandom();

    public SpoutManager() {

        List<Spout> spouts = new ArrayList<>();

        spouts.add(new Spout(this, new Location(TowerChallenge.WORLD(), (-824)+0.5, 68, (-808)+0.5))); // 1
        spouts.add(new Spout(this, new Location(TowerChallenge.WORLD(), (-824)+0.5, 68, (-801)+0.5))); // 4
        spouts.add(new Spout(this, new Location(TowerChallenge.WORLD(), (-825)+0.5, 68, (-806)+0.5))); // 2
        spouts.add(new Spout(this, new Location(TowerChallenge.WORLD(), (-826)+0.5, 68, (-800)+0.5))); // 5
        spouts.add(new Spout(this, new Location(TowerChallenge.WORLD(), (-823)+0.5, 68, (-803)+0.5))); // 3

        int count = 0;

        for (Spout spout : spouts) {
            count++;
            Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.me, () -> runSpout(spout), count*20L*5);
        }

    }

    public static final int SECONDS_RAMP = 3;
    private void runSpout(Spout spout) {
        long delaySeconds = RANDOM.nextInt(10)+7;
        Instant start = Instant.now().plusSeconds(delaySeconds);
        Instant end = start.plusSeconds(RANDOM.nextInt(10)+(SECONDS_RAMP*2+5));
        Bukkit.getScheduler().runTaskTimerAsynchronously(TowerChallenge.me, bukkitTask -> {
            Instant now = Instant.now();
            if (now.isBefore(end)) {
                double height = (RANDOM.nextDouble()*0.5d)+1.0d;
//                double height = 0.1d;
                Duration fromStart = Duration.between(start, now);
                Duration fromEnd = Duration.between(now, end);
                if (fromStart.compareTo(Duration.ofSeconds(SECONDS_RAMP)) < 0) {
                    // three seconds or less from start
                    height *= ((double) fromStart.toMillis() /((double) (1000*SECONDS_RAMP)));
                } else if (fromEnd.compareTo(Duration.ofSeconds(SECONDS_RAMP)) < 0) {
                    // three seconds or less from end
                    height *= ((double) fromEnd.toMillis() /((double) (1000*SECONDS_RAMP)));
                }
                spout.spray(height);
            } else {
                bukkitTask.cancel();
                runSpout(spout);
            }
        }, delaySeconds*20, 1);
    }

}
