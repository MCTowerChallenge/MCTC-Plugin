package io.github.mystievous.towerchallenge;

import io.github.mystievous.towerchallenge.halloween.candy.CandyCommands;
import io.github.mystievous.towerchallenge.halloween.candy.CandyTabComplete;
import io.github.mystievous.towerchallenge.hats.HatCommands;
import io.github.mystievous.towerchallenge.hats.HatTabComplete;
import io.github.mystievous.towerchallenge.messaging.MessageCommands;
import io.github.mystievous.towerchallenge.halloween.penelope.PenelopeCommands;
import io.github.mystievous.towerchallenge.halloween.penelope.PenelopeTabComplete;
import io.github.mystievous.towerchallenge.misc.*;
import io.github.mystievous.towerchallenge.misc.resourcepack.ResourcePack;
import io.github.mystievous.towerchallenge.misc.resourcepack.ResourcePackListener;
import io.github.mystievous.towerchallenge.timer.Timer;
import io.github.mystievous.towerchallenge.timer.TimerCommands;
import io.github.mystievous.towerchallenge.timer.TimerTabComplete;
import io.github.mystievous.towerchallenge.towering.ChatHandler;
import io.github.mystievous.towerchallenge.towering.TowerCommands;
import io.github.mystievous.towerchallenge.towering.TowerTabComplete;
import io.github.mystievous.towerchallenge.wands.WandCommands;
import io.github.mystievous.towerchallenge.wands.WandListener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class TowerChallenge extends JavaPlugin {

    public static File teamConfigFile;
    public static File regionConfigFile;
    public static File questConfigFile;
    public static File penelopeConfigFile;
    public static File wandConfigFile;
    public static File endPortalConfigFile;
    public static File candyConfigFile;
    public static File hatConfigFile;
    public static File steveConfigFile;
    public static File teamScoreConfigFile;
    public static File teamDataConfigFile;

    public static TextColor PRIMARY_COLOR = TextColor.fromHexString("#44b9ad");
    public static TextColor SECONDARY_COLOR = TextColor.fromHexString("#6c8784");
    public static TextColor NEGATIVE_COLOR = NamedTextColor.DARK_RED;
    //d0608c
    //124f49

    public static final String MCTC_NAMESPACE = "mctc";

    public static String WORLD_NAME = "December MCTC";
    public static String NETHER_NAME = WORLD_NAME+"_nether";
    public static String THE_END_NAME = WORLD_NAME+"_the_end";
    public static World WORLD() {
        return Bukkit.getWorld(WORLD_NAME);
    };
    public static World NETHER() {
        return Bukkit.getWorld(NETHER_NAME);
    };
    public static World THE_END() {
        return Bukkit.getWorld(THE_END_NAME);
    }

    public static TowerChallenge me;

    private ChallengeManager challengeManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        me = this;

        this.saveDefaultConfig();

        teamConfigFile = new File(getDataFolder(), "teams.yml");
        saveResource("teams.yml", false);
        YamlConfiguration teamConfig = YamlConfiguration.loadConfiguration(teamConfigFile);

        regionConfigFile = new File(getDataFolder(), "regions.yml");
        saveResource("regions.yml", false);
        YamlConfiguration regionConfig = YamlConfiguration.loadConfiguration(regionConfigFile);

        questConfigFile = new File(getDataFolder(), "quests.yml");
        saveResource("quests.yml", false);
        YamlConfiguration questConfig = YamlConfiguration.loadConfiguration(questConfigFile);

        penelopeConfigFile = new File(getDataFolder(), "penelope.yml");
        YamlConfiguration penelopeConfig = YamlConfiguration.loadConfiguration(penelopeConfigFile);

        wandConfigFile = new File(getDataFolder(), "wands.yml");
        saveResource("wands.yml", false);
        YamlConfiguration wandConfig = YamlConfiguration.loadConfiguration(wandConfigFile);

        endPortalConfigFile = new File(getDataFolder(), "portalframes.yml");
        saveResource("portalframes.yml", false);
        YamlConfiguration endPortalConfig = YamlConfiguration.loadConfiguration(endPortalConfigFile);

        candyConfigFile = new File(getDataFolder(), "candy.yml");
        YamlConfiguration candyConfig = YamlConfiguration.loadConfiguration(candyConfigFile);

        hatConfigFile = new File(getDataFolder(), "hat.yml");
        saveResource("hat.yml", false);
        YamlConfiguration hatConfig = YamlConfiguration.loadConfiguration(hatConfigFile);

        steveConfigFile = new File(getDataFolder(), "steve.yml");
        saveResource("steve.yml", false);
        YamlConfiguration steveConfig = YamlConfiguration.loadConfiguration(steveConfigFile);

        teamScoreConfigFile = new File(getDataFolder(), "teamscores.yml");
        YamlConfiguration teamScoreConfig = YamlConfiguration.loadConfiguration(teamScoreConfigFile);

        teamDataConfigFile = new File(getDataFolder(), "teamdata.yml");
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(teamDataConfigFile);

        try {
            teamConfig.save(teamConfigFile);
            regionConfig.save(regionConfigFile);
            questConfig.save(questConfigFile);
            penelopeConfig.save(penelopeConfigFile);
            wandConfig.save(wandConfigFile);
            endPortalConfig.save(endPortalConfigFile);
            candyConfig.save(candyConfigFile);
            hatConfig.save(hatConfigFile);
            steveConfig.save(steveConfigFile);
            teamScoreConfig.save(teamScoreConfigFile);
            teamDataConfig.save(teamDataConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        challengeManager = new ChallengeManager(this);

        TowerCommands towerCommands = new TowerCommands(challengeManager);
        TowerTabComplete towerTabComplete = new TowerTabComplete();

        this.getCommand("tower").setExecutor(towerCommands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);

        HatCommands hatCommands = new HatCommands(challengeManager.getTowerListener());
        HatTabComplete hatTabComplete = new HatTabComplete();
        this.getCommand("hat").setExecutor(hatCommands);
        this.getCommand("hat").setTabCompleter(hatTabComplete);

        new SitEventHandler();

        // Teams
        ChatHandler chatHandler = new ChatHandler();
        getServer().getPluginManager().registerEvents(chatHandler, this);

        // Wands
        WandCommands wandCommands = new WandCommands(this);
        this.getCommand("wand").setExecutor(wandCommands);
        WandListener wandListener = new WandListener();
        getServer().getPluginManager().registerEvents(wandListener, this);

        // Timer
        Timer timer = new Timer(this);
        TimerCommands timerCommands = new TimerCommands(timer);
        TimerTabComplete timerTabComplete = new TimerTabComplete();

        this.getCommand("timer").setExecutor(timerCommands);
        this.getCommand("timer").setTabCompleter(timerTabComplete);

        // Other Commands
        PenelopeCommands penelopeCommands = new PenelopeCommands();
        this.getCommand("penelope").setExecutor(penelopeCommands);
        PenelopeTabComplete penelopeTabComplete = new PenelopeTabComplete();
        this.getCommand("penelope").setTabCompleter(penelopeTabComplete);

        CandyCommands candyCommands = new CandyCommands(challengeManager);
        this.getCommand("candy").setExecutor(candyCommands);
        CandyTabComplete candyTabComplete = new CandyTabComplete();
        this.getCommand("candy").setTabCompleter(candyTabComplete);

        GodCommand godCommand = new GodCommand();
        this.getCommand("god").setExecutor(godCommand);

        ResourcePack resourcePack = new ResourcePack();
        this.getCommand("resourcepack").setExecutor(resourcePack);
        new ResourcePackListener(challengeManager);

        BroadcastCommand broadcastCommand = new BroadcastCommand(challengeManager);
        this.getCommand("broadcast").setExecutor(broadcastCommand);

        new EnderChestCommand(this);
        new InvseeCommand(this);
        new MessageCommands(challengeManager);
        new AnvilCommand(this);
        new CraftCommand(this);
        new RenameCommand(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getScheduler().cancelTasks(this);
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }
}
