package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.wands.WandListener;
import io.github.idkahn.towerchallenge.timer.Timer;
import io.github.idkahn.towerchallenge.timer.TimerCommands;
import io.github.idkahn.towerchallenge.timer.TimerTabComplete;
import io.github.idkahn.towerchallenge.towering.ChatHandler;
import io.github.idkahn.towerchallenge.towering.TowerCommands;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import io.github.idkahn.towerchallenge.towering.TowerTabComplete;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class TowerChallenge extends JavaPlugin {

    public static File regionConfigFile;
    public static File questConfigFile;

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.saveDefaultConfig();

        regionConfigFile = new File(getDataFolder(), "regions.yml");
        YamlConfiguration regionConfig = YamlConfiguration.loadConfiguration(regionConfigFile);

        questConfigFile = new File(getDataFolder(), "quests.yml");
        YamlConfiguration questConfig = YamlConfiguration.loadConfiguration(questConfigFile);

        try {
            regionConfig.save(regionConfigFile);
            questConfig.save(questConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EventManager manager = new EventManager(this);


        TowerCommands towerCommands = new TowerCommands(manager);
        TowerTabComplete towerTabComplete = new TowerTabComplete();

        this.getCommand("tower").setExecutor(towerCommands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);

        // Teams
        ChatHandler chatHandler = new ChatHandler();
        getServer().getPluginManager().registerEvents(chatHandler, this);

        // Wands
        WandListener wandListener = new WandListener();
        getServer().getPluginManager().registerEvents(wandListener, this);

        // Timer
        Timer timer = new Timer(this);
        TimerCommands timerCommands = new TimerCommands(timer);
        TimerTabComplete timerTabComplete = new TimerTabComplete();

        this.getCommand("timer").setExecutor(timerCommands);
        this.getCommand("timer").setTabCompleter(timerTabComplete);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
