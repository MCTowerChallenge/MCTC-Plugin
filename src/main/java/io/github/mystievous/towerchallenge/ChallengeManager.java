package io.github.mystievous.towerchallenge;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.mystievous.towerchallenge.decoration.sweetsburg.MarketStalls;
import io.github.mystievous.towerchallenge.gods.GodManager;
import io.github.mystievous.towerchallenge.halloween.candy.Candy;
import io.github.mystievous.towerchallenge.magic.GoatHat;
import io.github.mystievous.towerchallenge.misc.fasttravel.FastTravelListener;
import io.github.mystievous.towerchallenge.decoration.waterspouts.SpoutManager;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.TeamItemListener;
import io.github.mystievous.towerchallenge.quests.legacy.LegacyQuestManager;
import io.github.mystievous.towerchallenge.spawncompass.SpawnCompass;
import io.github.mystievous.towerchallenge.teleports.TeleportHistoryManager;
import io.github.mystievous.towerchallenge.towering.TowerListener;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import io.github.mystievous.towerchallenge.towering.WinnerGUI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChallengeManager {

    /**
     * The phases of the event
     */
    public enum ChallengePhase {
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

    private static final String OBJECTIVE_NAME = "TowerHeight";

    public static String formatBlockType(Material material) {
        String name = material.name().toLowerCase();
        StringBuilder output = new StringBuilder();
        for (String word : name.split("_")) {
            output.append(StringUtils.capitalize(word)).append(' ');
        }
        return output.toString();
    }

    public static void log(String text) {
        Bukkit.getLogger().info(text);
    }

    // Instance Variables
    private final TowerChallenge plugin;
    private ChallengePhase challengePhase;
    private final BlockSets blockSets;
    private final LegacyQuestManager legacyQuestManager;
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
        challengePhase = ChallengePhase.SETUP;
        blockSets = new BlockSets();
        towerHeight = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(OBJECTIVE_NAME);
        if (towerHeight == null) {
            towerHeight = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(OBJECTIVE_NAME, "dummy", Component.text("Tower Height"));
        }
        towerListener = new TowerListener(this);
        questManager = new QuestManager(this);
        legacyQuestManager = new LegacyQuestManager(this);
        Bukkit.getServer().getPluginManager().registerEvents(towerListener, getPlugin());
        endPortal = new EndPortal(this);
        winnerGUI = new WinnerGUI(this);
        new Candy(this);
        GodManager godManager = new GodManager(this, towerListener);
        teleportHistoryManager = new TeleportHistoryManager(godManager);
        new SpawnCompass();
        new MarketStalls();
        new SpoutManager(this);
        new FastTravelListener();
        new TeamItemListener();
        new GoatHat();
//        new BottleManager();
    }

    // Accessors and Mutators
    public ChallengePhase getChallengePhase() {
        return challengePhase;
    }
    public void setChallengePhase(ChallengePhase challengePhase) {
        ChallengePhaseChangeEvent event = new ChallengePhaseChangeEvent(challengePhase);
        Bukkit.getPluginManager().callEvent(event);
        this.challengePhase = event.getChallengePhase();
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
        HashMap<String, ParticipantTeam> teams = towerListener.getTeams();
        ChallengeManager.log(teams.toString());
        for (Map.Entry<String, ParticipantTeam> entry : teams.entrySet()) {
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

    public @Nullable TowerTeam getPlayerTeam(OfflinePlayer player) {
        return towerListener.getPlayerTeam(player);
    }

    public LegacyQuestManager getLegacyQuestManager() {
        return legacyQuestManager;
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
        return this.getChallengePhase().equals(ChallengePhase.TOWERING);
    }

    public boolean isFullBlock(Material material) {
        return blockSets.getFullBlocks().contains(material);
    }


}
