package io.github.idkahn.towerchallenge;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class EventManager {

    /**
     * The phases of the event
     */
    public enum Phase {
        SETUP,
        FIRSTHALF,
        INTERMISSION,
        SECONDHALF,
        TOWERING,
        PAUSED
    }

    // Constants
    public static final Location NETHER_PORTAL_LOCATION = new Location(Bukkit.getWorld("world_nether"), -112, 92, -173, 0, 0);
    public static final Location OVERWORLD_PORTAL_LOCATION = new Location(Bukkit.getWorld("world"), -584, 65, -1327, 270, 0);

    // Instance Variables
    private final JavaPlugin plugin;
    private Phase eventPhase;

    // Constructor
    public EventManager(JavaPlugin plugin) {
        this.plugin = plugin;
        eventPhase = Phase.SETUP;
    }

    // Accessors and Mutators
    public void setEventPhase(Phase eventPhase) {
        this.eventPhase = eventPhase;
    }
    public Phase getEventPhase() {
        return eventPhase;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
