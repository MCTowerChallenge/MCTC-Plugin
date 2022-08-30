package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.timer.Timer;
import io.github.idkahn.towerchallenge.timer.TimerCommands;
import io.github.idkahn.towerchallenge.timer.TimerTabComplete;
import io.github.idkahn.towerchallenge.towering.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class TowerChallenge extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Tower Phase
        TowerListener towerListener = new TowerListener(this);
        TowerCommands towerCommands = new TowerCommands(towerListener);
        TowerTabComplete towerTabComplete = new TowerTabComplete();

        getServer().getPluginManager().registerEvents(towerListener, this);
        this.getCommand("tower").setExecutor(towerCommands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);


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
