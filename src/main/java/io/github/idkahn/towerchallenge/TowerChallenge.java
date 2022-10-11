package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.candy.CandyCommands;
import io.github.idkahn.towerchallenge.candy.CandyTabComplete;
import io.github.idkahn.towerchallenge.commands.EnderChestCommand;
import io.github.idkahn.towerchallenge.commands.GodCommand;
import io.github.idkahn.towerchallenge.commands.InvseeCommand;
import io.github.idkahn.towerchallenge.hats.HatCommands;
import io.github.idkahn.towerchallenge.hats.HatTabComplete;
import io.github.idkahn.towerchallenge.penelope.PenelopeCommands;
import io.github.idkahn.towerchallenge.penelope.PenelopeTabComplete;
import io.github.idkahn.towerchallenge.timer.Timer;
import io.github.idkahn.towerchallenge.timer.TimerCommands;
import io.github.idkahn.towerchallenge.timer.TimerTabComplete;
import io.github.idkahn.towerchallenge.towering.ChatHandler;
import io.github.idkahn.towerchallenge.towering.TowerCommands;
import io.github.idkahn.towerchallenge.towering.TowerTabComplete;
import io.github.idkahn.towerchallenge.wands.WandCommands;
import io.github.idkahn.towerchallenge.wands.WandListener;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class TowerChallenge extends JavaPlugin {

    public static File regionConfigFile;
    public static File questConfigFile;
    public static File penelopeConfigFile;
    public static File wandConfigFile;
    public static File endPortalConfigFile;
    public static File candyConfigFile;

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.saveDefaultConfig();

        regionConfigFile = new File(getDataFolder(), "regions.yml");
        YamlConfiguration regionConfig = YamlConfiguration.loadConfiguration(regionConfigFile);

        questConfigFile = new File(getDataFolder(), "quests.yml");
        YamlConfiguration questConfig = YamlConfiguration.loadConfiguration(questConfigFile);

        penelopeConfigFile = new File(getDataFolder(), "penelope.yml");
        YamlConfiguration penelopeConfig = YamlConfiguration.loadConfiguration(penelopeConfigFile);

        wandConfigFile = new File(getDataFolder(), "wands.yml");
        YamlConfiguration wandConfig = YamlConfiguration.loadConfiguration(wandConfigFile);

        endPortalConfigFile = new File(getDataFolder(), "portalframes.yml");
        YamlConfiguration endPortalConfig = YamlConfiguration.loadConfiguration(endPortalConfigFile);

        candyConfigFile = new File(getDataFolder(), "candy.yml");
        YamlConfiguration candyConfig = YamlConfiguration.loadConfiguration(candyConfigFile);

        try {
            regionConfig.save(regionConfigFile);
            questConfig.save(questConfigFile);
            penelopeConfig.save(penelopeConfigFile);
            wandConfig.save(wandConfigFile);
            endPortalConfig.save(endPortalConfigFile);
            candyConfig.save(candyConfigFile);
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

        CandyCommands candyCommands = new CandyCommands();
        this.getCommand("candy").setExecutor(candyCommands);
        CandyTabComplete candyTabComplete = new CandyTabComplete();
        this.getCommand("candy").setTabCompleter(candyTabComplete);

        GodCommand godCommand = new GodCommand();
        this.getCommand("god").setExecutor(godCommand);

        new EnderChestCommand(this);
        new InvseeCommand(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
