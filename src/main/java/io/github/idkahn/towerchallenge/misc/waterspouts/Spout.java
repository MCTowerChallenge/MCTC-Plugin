package io.github.idkahn.towerchallenge.misc.waterspouts;

import io.github.idkahn.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.time.Duration;
import java.time.Instant;

public class Spout {

    private SpoutManager spoutManager;
    private Location location;
    private boolean spraying;
    private World world;

    public Spout(SpoutManager spoutManager, Location location) {
        this.spoutManager = spoutManager;
        this.location = location;
        world = location.getWorld();
        this.spraying = false;
    }


    public void spray(double height) {
        world.spawnParticle(Particle.WATER_SPLASH, location, (int) (height*15), 0.1d, height, 0.1d, 0.0d);
    }

}
