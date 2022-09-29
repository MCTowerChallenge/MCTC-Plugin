package io.github.idkahn.towerchallenge;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.EnumSet;

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
    private static final String OBJECTIVE_NAME = "TowerHeight";

    public static String formatBlockType(Material material) {
        String name = material.name().toLowerCase();
        String output = "";
        for (String word : name.split("_")) {
            output += StringUtils.capitalize(word)+' ';
        }
        return output;
    }

    // Instance Variables
    private final JavaPlugin plugin;
    private Phase eventPhase;
    private BlockSets blockSets;
    private Objective towerHeight;

    // Constructor
    public EventManager(JavaPlugin plugin) {
        this.plugin = plugin;
        eventPhase = Phase.SETUP;
        blockSets = new BlockSets();
        towerHeight = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(OBJECTIVE_NAME);
        if (towerHeight == null) {
            towerHeight = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(OBJECTIVE_NAME, "dummy", Component.text("Tower Height"));
        }
    }

    // Accessors and Mutators
    public Phase getEventPhase() {
        return eventPhase;
    }
    public void setEventPhase(Phase eventPhase) {
        this.eventPhase = eventPhase;
        if (this.eventPhase == Phase.TOWERING) {
            towerHeight.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            towerHeight.setDisplaySlot(null);
        }
    }

    public void addFullBlock(Material material) {
        if (material.isBlock()) {
            blockSets.getFullBlocks().add(material);
        } else {
            throw new IllegalArgumentException("Material must be a block!");
        }
    }
    public void removeFullBlock(Material material) {
        blockSets.getFullBlocks().remove(material);
    }

    public Objective getObjective() {
        return towerHeight;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    // Methods
    /**
     * Gets whether the event is in the towering phase
     * @return State of the towering phase
     */
    public boolean isTowering() {
        return this.getEventPhase().equals(EventManager.Phase.TOWERING);
    }

    public boolean isFullBlock(Material material) {
        return blockSets.getFullBlocks().contains(material);
    }


}
