package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.quests.QuestManager;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final TowerChallenge plugin;
    private Phase eventPhase;
    private BlockSets blockSets;
    private Objective towerHeight;
    private QuestManager questManager;
    private TowerListener towerListener;
    private EndPortal endPortal;

    private HashMap<String, TowerTeam> teams;

    // Constructor
    public EventManager(TowerChallenge plugin) {
        this.plugin = plugin;
        this.teams = new HashMap<>();
        eventPhase = Phase.SETUP;
        blockSets = new BlockSets();
        towerHeight = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(OBJECTIVE_NAME);
        if (towerHeight == null) {
            towerHeight = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(OBJECTIVE_NAME, "dummy", Component.text("Tower Height"));
        }
        towerListener = new TowerListener(this);
        questManager = new QuestManager(this);
        Bukkit.getServer().getPluginManager().registerEvents(towerListener, getPlugin());
        endPortal = new EndPortal(this);
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

    public TowerChallenge getPlugin() {
        return plugin;
    }

    public HashMap<String, TowerTeam> getTeams() {
        return teams;
    }

    public int getCompletedPortalFrames() {
        int count = 0;
        for (Map.Entry<String,TowerTeam> entry : towerListener.getTeams().entrySet()) {
            if (entry.getValue().hasEye()) {
                count++;
            }
        }
        return count;
    }

    public void resetEndPortal() {
        Bukkit.getLogger().info("Resetting End Portal");
        for (Map.Entry<String,TowerTeam> entry : towerListener.getTeams().entrySet()) {
            Bukkit.getLogger().info("Resetting frame for " + entry.getKey());
            entry.getValue().resetFrame();
        }
    }

    public EndPortal getEndPortal() {
        return endPortal;
    }

    public void setTeams(HashMap<String, TowerTeam> teams) {
        this.teams = teams;
    }

    public TowerListener getTowerListener() {
        return towerListener;
    }

    public QuestManager getQuestManager() {
        return questManager;
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
