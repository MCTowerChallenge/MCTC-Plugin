package io.github.idkahn.towerchallenge;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.idkahn.towerchallenge.gods.GodManager;
import io.github.idkahn.towerchallenge.halloween.candy.Candy;
import io.github.idkahn.towerchallenge.misc.fasttravel.FastTravelListener;
import io.github.idkahn.towerchallenge.misc.waterspouts.SpoutManager;
import io.github.idkahn.towerchallenge.quests.QuestManager;
import io.github.idkahn.towerchallenge.halloween.steve.SteveManager;
import io.github.idkahn.towerchallenge.spawncompass.SpawnCompass;
import io.github.idkahn.towerchallenge.teleports.TeleportHistoryManager;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;
import io.github.idkahn.towerchallenge.towering.WinnerGUI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChallengeManager {

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

    public static RegionContainer regionContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    // Constants
    public static final Location NETHER_PORTAL_LOCATION = new Location(Bukkit.getWorld("December MCTC_nether"), -112, 92, -173, 0, 0);
    public static final Location OVERWORLD_PORTAL_LOCATION = new Location(Bukkit.getWorld("December MCTC"), -584, 65, -1327, 270, 0);
    private static final String OBJECTIVE_NAME = "TowerHeight";

    public static String formatBlockType(Material material) {
        String name = material.name().toLowerCase();
        StringBuilder output = new StringBuilder();
        for (String word : name.split("_")) {
            output.append(StringUtils.capitalize(word)).append(' ');
        }
        return output.toString();
    }

    public static void Log(String text) {
        Bukkit.getLogger().info(text);
    }

    // Instance Variables
    private final TowerChallenge plugin;
    private Phase eventPhase;
    private final BlockSets blockSets;
    private final QuestManager questManager;
    private Objective towerHeight;
    private final TowerListener towerListener;
    private final EndPortal endPortal;
    private final WinnerGUI winnerGUI;
    private final TeleportHistoryManager teleportHistoryManager;

    private final HashMap<String, ParticipantTeam> teams;

    // Constructor
    public ChallengeManager(TowerChallenge plugin) {
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
        winnerGUI = new WinnerGUI(this);
        new Candy(this);
        new SteveManager(this);
        GodManager godManager = new GodManager(this);
        teleportHistoryManager = new TeleportHistoryManager(godManager);
        new SpawnCompass();
        new SpoutManager();
        new FastTravelListener();
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

    public void showTowerScores(Audience audience) {
        ArrayList<ParticipantTeam> sortedTeams = new ArrayList<>(towerListener.getTeams().values());
        sortedTeams.sort((o1, o2) -> {
            Score score1 = towerHeight.getScore(PlainTextComponentSerializer.plainText().serialize(o1.getDisplayName()));
            Score score2 = towerHeight.getScore(PlainTextComponentSerializer.plainText().serialize(o2.getDisplayName()));
            return Integer.compare(score2.getScore(), score1.getScore());
        });

        for (ParticipantTeam team : sortedTeams) {
            if (team.getEntries().size() > 0) {
                audience.sendMessage(team.getDisplayName().color(team.getTextColor())
                        .append(Component.text(" has ").color(NamedTextColor.WHITE)
                                .append(Component.text(towerHeight.getScore(PlainTextComponentSerializer.plainText().serialize(team.getDisplayName())).getScore()+team.getExtraScore())
                                        .color(TowerChallenge.PRIMARY_COLOR))
                                .append(Component.text(" blocks"))));
            }
        }

    }

    public TowerChallenge getPlugin() {
        return plugin;
    }

    public TeleportHistoryManager getTeleportHistoryManager() {
        return teleportHistoryManager;
    }

    public WinnerGUI getWinnerGUI() {
        return winnerGUI;
    }

    public HashMap<String, ParticipantTeam> getTeams() {
        return teams;
    }

    public int getCompletedPortalFrames() {
        int count = 0;
        for (Map.Entry<String, ParticipantTeam> entry : towerListener.getTeams().entrySet()) {
            if (entry.getValue().hasEye()) {
                count++;
            }
        }
        return count;
    }

    public void resetEndPortal() {
        Bukkit.getLogger().info("Resetting End Portal");
        endPortal.resetPortal();
        for (Map.Entry<String, ParticipantTeam> entry : towerListener.getTeams().entrySet()) {
            if (entry.getValue().getTeam().getName().equalsIgnoreCase("Lime") ||
                    entry.getValue().getTeam().getName().equalsIgnoreCase("Purple") ||
                    entry.getValue().getTeam().getName().equalsIgnoreCase("Magenta") ||
                    entry.getValue().getTeam().getName().equalsIgnoreCase("White") ||
                    entry.getValue().getTeam().getName().equalsIgnoreCase("DarkGray") ||
                    entry.getValue().getTeam().getName().equalsIgnoreCase("Brown"))
                return;
            Bukkit.getLogger().info("Resetting frame for " + entry.getKey());
            entry.getValue().resetFrame();
        }
    }

    public void resetTeams() {
        for (Map.Entry<String, ParticipantTeam> entry : towerListener.getTeams().entrySet()) {
            ParticipantTeam team = entry.getValue();
            team.clear();
        }
        towerListener.getGodTeam().clear();
        towerListener.loadTeams();
    }

    public void openEndPortal() {
        Bukkit.getLogger().info("Opening End Portal");
        for (Map.Entry<String, ParticipantTeam> entry : towerListener.getTeams().entrySet()) {
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
        for (Map.Entry<String, ParticipantTeam> entry : towerListener.getTeams().entrySet()) {
            ParticipantTeam team = entry.getValue();
            for (Player player : team.getOnlinePlayers()) {
                dealPlayerItems(player);
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
        return this.getEventPhase().equals(ChallengeManager.Phase.TOWERING);
    }

    public boolean isFullBlock(Material material) {
        return blockSets.getFullBlocks().contains(material);
    }


}
