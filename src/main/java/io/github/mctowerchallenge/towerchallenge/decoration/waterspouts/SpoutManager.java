package io.github.mctowerchallenge.towerchallenge.decoration.waterspouts;

import io.github.mctowerchallenge.towerchallenge.TowerChallenge;
import io.github.mctowerchallenge.towerchallenge.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class SpoutManager {

    /**
     * Run all configured water spouts
     */
    public static void runSpouts() {

        // Dec2022
//        List<Spout> spouts = new ArrayList<>();
//        spouts.add(new Spout(new Location(Worlds.Dec2022(), (-824) + 0.5, 67, (-808) + 0.5))); // 1
//        spouts.add(new Spout(new Location(Worlds.Dec2022(), (-824) + 0.5, 67, (-801) + 0.5))); // 4
//        spouts.add(new Spout(new Location(Worlds.Dec2022(), (-825) + 0.5, 67, (-806) + 0.5))); // 2
//        spouts.add(new Spout(new Location(Worlds.Dec2022(), (-826) + 0.5, 67, (-800) + 0.5))); // 5
//        spouts.add(new Spout(new Location(Worlds.Dec2022(), (-823) + 0.5, 67, (-803) + 0.5))); // 3
//
//        int count = 0;
//        for (Spout spout : spouts) {
//            count++;
//            Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), spout::runSpout, count * 20L * 5);
//        }

        // Feb2023
//        final double height = 1.0d;
//        new ConstantSpout(new Location(Worlds.Feb2023(), 99.5, 65, -2110.5), height).runSpout();
//        new ConstantSpout(new Location(Worlds.Feb2023(), 102.5, 65, -2110.5), height).runSpout();
//        new ConstantSpout(new Location(Worlds.Feb2023(), 99.5, 65, -2118.5), height).runSpout();
//        new ConstantSpout(new Location(Worlds.Feb2023(), 102.5, 65, -2118.5), height).runSpout();


        // Jun2023
        List<Spout> spouts = new ArrayList<>();
        spouts.add(new Spout(new Location(Worlds.Jun2023(), (242) + 0.5, 62, (-2209) + 0.5))); // 1
        spouts.add(new Spout(new Location(Worlds.Jun2023(), (241) + 0.5, 62, (-2214) + 0.5))); // 4
        spouts.add(new Spout(new Location(Worlds.Jun2023(), (243) + 0.5, 62, (-2212) + 0.5))); // 2
        spouts.add(new Spout(new Location(Worlds.Jun2023(), (240) + 0.5, 62, (-2211) + 0.5))); // 3

        int count = 0;
        for (Spout spout : spouts) {
            count++;
            Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), spout::runSpout, count * 20L * 5);
        }

    }

}
