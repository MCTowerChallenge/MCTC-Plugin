package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.gui.HatGUI;
import io.github.idkahn.towerchallenge.timer.Timer;
import io.github.idkahn.towerchallenge.timer.TimerCommands;
import io.github.idkahn.towerchallenge.timer.TimerTabComplete;
import io.github.idkahn.towerchallenge.towering.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class TowerChallenge extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.saveDefaultConfig();

        TowerListener towerListener = new TowerListener(this);

        HatGUI hatGUI = new HatGUI(this, towerListener);
        getServer().getPluginManager().registerEvents(hatGUI, this);

        TowerCommands towerCommands = new TowerCommands(this, towerListener, hatGUI);
        TowerTabComplete towerTabComplete = new TowerTabComplete();

        getServer().getPluginManager().registerEvents(towerListener, this);
        this.getCommand("tower").setExecutor(towerCommands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);

        // Teams
        ChatHandler chatHandler = new ChatHandler();
        getServer().getPluginManager().registerEvents(chatHandler, this);

        // Timer
        Timer timer = new Timer(this);
        TimerCommands timerCommands = new TimerCommands(timer);
        TimerTabComplete timerTabComplete = new TimerTabComplete();

        this.getCommand("timer").setExecutor(timerCommands);
        this.getCommand("timer").setTabCompleter(timerTabComplete);


//        getLogger().info("Hello World!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
