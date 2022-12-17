package io.github.mystievous.towerchallenge.decoration.waterspouts;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SpoutManager {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final String REGION_NAME = "water-fountains";

    public static Duration getRegionTime(OfflinePlayer player) {
        String playerPath = "Individual."+player.getUniqueId()+".WaterSpouts";
        String totalTimePath = playerPath + ".TotalTime";
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamDataConfigFile);
        Duration totalTime = Duration.ofMillis(config.getLong(totalTimePath));
        return totalTime;
    }

    private ChallengeManager challengeManager;

    public SpoutManager(ChallengeManager challengeManager) {

        this.challengeManager = challengeManager;

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

        Bukkit.getScheduler().runTaskTimerAsynchronously(TowerChallenge.me, bukkitTask -> {
            RegionManager regionManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(TowerChallenge.WORLD()));
            if (regionManager != null && regionManager.hasRegion(REGION_NAME)) {
                ProtectedRegion region = regionManager.getRegion(REGION_NAME);
                assert region != null;
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    String playerPath = "Individual."+player.getUniqueId()+".WaterSpouts";
                    if (region.contains(BukkitAdapter.adapt(player.getLocation()).toVector().toBlockPoint())) {
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamDataConfigFile);
                        String inRegionPath = playerPath + ".InRegion";
                        String enterTimePath = playerPath + ".EnterTime";
                        if (!config.getBoolean(inRegionPath)) {
                            config.set(enterTimePath, Instant.now().toEpochMilli());
                            config.set(inRegionPath, true);
                            try {
                                config.save(TowerChallenge.teamDataConfigFile);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamDataConfigFile);
                        String inRegionPath = playerPath + ".InRegion";
                        String enterTimePath = playerPath + ".EnterTime";
                        String totalTimePath = playerPath + ".TotalTime";
                        if (config.getBoolean(inRegionPath)) {
                            Instant enterTime = Instant.ofEpochMilli(config.getLong(enterTimePath));
                            Duration totalTime = Duration.between(enterTime, Instant.now());
                            Duration previousTotalTime = Duration.ofMillis(config.getLong(totalTimePath));
                            Duration newTotalTime = previousTotalTime.plus(totalTime);
                            config.set(totalTimePath, newTotalTime.toMillis());
                            config.set(inRegionPath, false);
                            try {
                                config.save(TowerChallenge.teamDataConfigFile);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }, 0, 10);

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
