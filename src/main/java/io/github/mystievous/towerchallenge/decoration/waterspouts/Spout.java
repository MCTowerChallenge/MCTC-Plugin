package io.github.mystievous.towerchallenge.decoration.waterspouts;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class Spout {

    private final Location location;
    private final World world;

    public Spout(Location location) {
        this.location = location;
        world = location.getWorld();
    }


    public void spray(double height) {
        world.spawnParticle(Particle.WATER_SPLASH, location, (int) (height*15), 0.1d, height, 0.1d, 0.0d);
    }

}
