package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.commands.*;
import io.github.idkahn.towerchallenge.halloween.candy.CandyCommands;
import io.github.idkahn.towerchallenge.halloween.candy.CandyTabComplete;
import io.github.idkahn.towerchallenge.hats.HatCommands;
import io.github.idkahn.towerchallenge.hats.HatTabComplete;
import io.github.idkahn.towerchallenge.messaging.MessageCommands;
import io.github.idkahn.towerchallenge.penelope.PenelopeCommands;
import io.github.idkahn.towerchallenge.penelope.PenelopeTabComplete;
import io.github.idkahn.towerchallenge.resourcepack.ResourcePack;
import io.github.idkahn.towerchallenge.resourcepack.ResourcePackListener;
import io.github.idkahn.towerchallenge.timer.Timer;
import io.github.idkahn.towerchallenge.timer.TimerCommands;
import io.github.idkahn.towerchallenge.timer.TimerTabComplete;
import io.github.idkahn.towerchallenge.towering.ChatHandler;
import io.github.idkahn.towerchallenge.towering.TowerCommands;
import io.github.idkahn.towerchallenge.towering.TowerTabComplete;
import io.github.idkahn.towerchallenge.wands.WandCommands;
import io.github.idkahn.towerchallenge.wands.WandListener;
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

    public static TextColor PRIMARY_COLOR = TextColor.fromHexString("#44b9ad");
    public static TextColor SECONDARY_COLOR = TextColor.fromHexString("#6c8784");
    //d0608c
    //124f49

    public static String WORLD_NAME = "December MCTC";
    public static World WORLD = Bukkit.getWorld(WORLD_NAME);
    public static World NETHER = Bukkit.getWorld(WORLD_NAME+"_nether");
    public static World END = Bukkit.getWorld(WORLD_NAME+"_the_end");

    @Override
    public void onEnable() {
        // Plugin startup logic

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EventManager manager = new EventManager(this);


        TowerCommands towerCommands = new TowerCommands(manager);
        TowerTabComplete towerTabComplete = new TowerTabComplete();

        this.getCommand("tower").setExecutor(towerCommands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);

        HatCommands hatCommands = new HatCommands(manager.getTowerListener());
        HatTabComplete hatTabComplete = new HatTabComplete();
        this.getCommand("hat").setExecutor(hatCommands);
        this.getCommand("hat").setTabCompleter(hatTabComplete);


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

        CandyCommands candyCommands = new CandyCommands(manager);
        this.getCommand("candy").setExecutor(candyCommands);
        CandyTabComplete candyTabComplete = new CandyTabComplete();
        this.getCommand("candy").setTabCompleter(candyTabComplete);

        GodCommand godCommand = new GodCommand();
        this.getCommand("god").setExecutor(godCommand);

        ResourcePack resourcePack = new ResourcePack();
        this.getCommand("resourcepack").setExecutor(resourcePack);
        new ResourcePackListener(manager);

        BroadcastCommand broadcastCommand = new BroadcastCommand(manager);
        this.getCommand("broadcast").setExecutor(broadcastCommand);

        new EnderChestCommand(this);
        new InvseeCommand(this);
        new MessageCommands(manager);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
