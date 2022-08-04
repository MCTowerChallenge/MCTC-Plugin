package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.towering.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class TowerChallenge extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        TowerListener towerListener = new TowerListener(this);
        TowerCommands commands = new TowerCommands(towerListener);
        TowerTabComplete towerTabComplete = new TowerTabComplete();

        getServer().getPluginManager().registerEvents(towerListener, this);

        this.getCommand("tower").setExecutor(commands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);

//        getLogger().info("Hello World!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
