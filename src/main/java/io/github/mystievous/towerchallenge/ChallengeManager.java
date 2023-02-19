package io.github.mystievous.towerchallenge;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.mystievous.towerchallenge.decoration.waterspouts.SpoutManager;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.magic.GoatHat;
import io.github.mystievous.towerchallenge.misc.fasttravel.FastTravelListener;
import io.github.mystievous.towerchallenge.spawncompass.SpawnCompass;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.towering.TowerListener;
import io.github.mystievous.towerchallenge.towering.WinnersGUI;
import io.github.mystievous.towerchallenge.utility.BlockSets;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scoreboard.Objective;

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
    public static Objective getScoreObjective() {
        return Bukkit.getScoreboardManager().getMainScoreboard().getObjective(OBJECTIVE_NAME);
    }

    // Instance Variables
    private final TowerChallenge plugin;
    private ChallengePhase challengePhase;
    private final TowerListener towerListener;
    private final WinnersGUI winnersGUI;

    // Constructor
    public ChallengeManager(TowerChallenge plugin, TeamManager teamManager) {
        this.plugin = plugin;
        challengePhase = ChallengePhase.IN_PROGRESS;
        if (getScoreObjective() == null) {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(OBJECTIVE_NAME, "dummy", Component.text("Tower Height"));
        }
        towerListener = new TowerListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(towerListener, getPlugin());
        winnersGUI = new WinnersGUI(plugin, teamManager, Element.empty());
        new SpawnCompass();
//        new MarketStalls();
        new FastTravelListener();
        new GoatHat();
        SpoutManager.runSpouts();
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
            BlockSets.FULL_BLOCKS.add(material);
        } else {
            throw new IllegalArgumentException("Material must be a block!");
        }
    }
    public void removeFullBlock(Material material) {
        BlockSets.FULL_BLOCKS.remove(material);
    }

    public TowerChallenge getPlugin() {
        return plugin;
    }

    public WinnersGUI getWinnersGUI() {
        return winnersGUI;
    }

}
