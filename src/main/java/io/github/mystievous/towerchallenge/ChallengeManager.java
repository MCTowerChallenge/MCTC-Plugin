package io.github.mystievous.towerchallenge;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.mystievous.towerchallenge.decoration.waterspouts.SpoutManager;
import io.github.mystievous.towerchallenge.gods.GodManager;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.magic.GoatHat;
import io.github.mystievous.towerchallenge.misc.fasttravel.FastTravelListener;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.TeamItemListener;
import io.github.mystievous.towerchallenge.spawncompass.SpawnCompass;
import io.github.mystievous.towerchallenge.teleports.TeleportHistoryManager;
import io.github.mystievous.towerchallenge.towering.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

    public static final boolean DEBUG = true;

    /**
     * The phases of the event
     */
    public enum ChallengePhase {
        IN_PROGRESS,
        TOWERING,
    }

    public static RegionContainer regionContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    private static final String OBJECTIVE_NAME = "TowerHeight";

    // Instance Variables
    private final TowerChallenge plugin;
    private ChallengePhase challengePhase;
    private final BlockSets blockSets;
    private final QuestManager questManager;
    private Objective towerHeight;
    private final TowerListener towerListener;
    private final EndPortal endPortal;
    private final WinnersGUI winnersGUI;
    private final GodManager godManager;
    private final TeleportHistoryManager teleportHistoryManager;

    private final HashMap<String, ParticipantTeam> teams;

    // Constructor
    public ChallengeManager(TowerChallenge plugin) {
        this.plugin = plugin;
        this.teams = new HashMap<>();
        challengePhase = ChallengePhase.IN_PROGRESS;
        blockSets = new BlockSets();
        towerHeight = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(OBJECTIVE_NAME);
        if (towerHeight == null) {
            towerHeight = Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(OBJECTIVE_NAME, "dummy", Component.text("Tower Height"));
        }
        towerListener = new TowerListener(this);
        questManager = new QuestManager(this);
        Bukkit.getServer().getPluginManager().registerEvents(towerListener, getPlugin());
        endPortal = new EndPortal(this);
        winnersGUI = new WinnersGUI(this, Element.empty());
        this.godManager = new GodManager(this, towerListener);
        teleportHistoryManager = new TeleportHistoryManager(godManager);
        new SpawnCompass();
//        new MarketStalls();
        new FastTravelListener();
        new TeamItemListener();
        new GoatHat();
        SpoutManager.runSpouts();
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
                                        .color(Palette.PRIMARY.toTextColor()))
                                .append(Component.text(" blocks"))));
            }
        }

    }

    public TowerChallenge getPlugin() {
        return plugin;
    }

    public GodManager getGodManager() {
        return godManager;
    }

    public TeleportHistoryManager getTeleportHistoryManager() {
        return teleportHistoryManager;
    }

    public WinnersGUI getWinnersGUI() {
        return winnersGUI;
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
        TowerChallenge.log(teams.toString());
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
