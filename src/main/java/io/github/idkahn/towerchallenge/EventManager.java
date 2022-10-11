package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.candy.Candy;
import io.github.idkahn.towerchallenge.other.PeacefulListener;
import io.github.idkahn.towerchallenge.quests.QuestManager;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        StringBuilder output = new StringBuilder();
        for (String word : name.split("_")) {
            output.append(StringUtils.capitalize(word)).append(' ');
        }
        return output.toString();
    }

    // Instance Variables
    private final TowerChallenge plugin;
    private Phase eventPhase;
    private final BlockSets blockSets;
    private final QuestManager questManager;
    private Objective towerHeight;
    private final TowerListener towerListener;
    private final EndPortal endPortal;

    private final HashMap<String, TowerTeam> teams;

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
        new Candy(this);
        new PeacefulListener(this);
    }

    // Accessors and Mutators
    public Phase getEventPhase() {
        return eventPhase;
    }
    public void setEventPhase(Phase eventPhase) {
        this.eventPhase = eventPhase;
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

    public void showTowerScores() {
        ArrayList<TowerTeam> sortedTeams = new ArrayList<>(towerListener.getTeams().values());
        sortedTeams.sort((o1, o2) -> {
            Score score1 = towerHeight.getScore(PlainTextComponentSerializer.plainText().serialize(o1.getDisplayName()));
            Score score2 = towerHeight.getScore(PlainTextComponentSerializer.plainText().serialize(o2.getDisplayName()));
            return Integer.compare(score2.getScore(), score1.getScore());
        });

        for (TowerTeam team : sortedTeams) {
//            if (team.getEntries().size() > 0) {
                Bukkit.getServer().sendMessage(team.getDisplayName().color(team.getTextColor())
                        .append(Component.text(" has ").color(NamedTextColor.WHITE)
                                .append(Component.text(towerHeight.getScore(PlainTextComponentSerializer.plainText().serialize(team.getDisplayName())).getScore())
                                        .color(NamedTextColor.AQUA))
                                .append(Component.text(" blocks"))));
//            }
        }

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
        endPortal.resetPortal();
        for (Map.Entry<String,TowerTeam> entry : towerListener.getTeams().entrySet()) {
            Bukkit.getLogger().info("Resetting frame for " + entry.getKey());
            entry.getValue().resetFrame();
        }
    }

    public void openEndPortal() {
        Bukkit.getLogger().info("Opening End Portal");
        for (Map.Entry<String,TowerTeam> entry : towerListener.getTeams().entrySet()) {
            Bukkit.getLogger().info("Setting frame for " + entry.getKey());
            entry.getValue().placeEye();
        }
//        endPortal.openPortal();
    }

    public EndPortal getEndPortal() {
        return endPortal;
    }

    public void giveShulker(Player player, int number) {
        towerListener.getPlayerTeam(player).giveShulker(player, number);
    }

    public void dealItems() {
        Bukkit.getLogger().info("Dealing items...");
        for (Map.Entry<String,TowerTeam> entry : towerListener.getTeams().entrySet()) {
            TowerTeam team = entry.getValue();
            for (String uuid : team.getTeam().getEntries()) {
                Entity entity = Bukkit.getEntity(UUID.fromString(uuid));
                if (entity instanceof Player player) {
                    dealPlayerItems(player);
                }
            }
        }
    }

    public void dealPlayerItems(Player player) {
        towerListener.getPlayerTeam(player).dealItems(player);
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
