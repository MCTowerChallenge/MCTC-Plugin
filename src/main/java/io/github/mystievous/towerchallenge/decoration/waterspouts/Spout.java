package io.github.mystievous.towerchallenge.decoration.waterspouts;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

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
