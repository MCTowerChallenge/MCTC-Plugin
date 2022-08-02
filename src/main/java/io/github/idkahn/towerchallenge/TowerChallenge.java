package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.Towering.TowerListener;
import io.github.idkahn.towerchallenge.Towering.DisableTower;
import io.github.idkahn.towerchallenge.Towering.EnableTower;
import io.github.idkahn.towerchallenge.Towering.RemoveBlocks;
import org.bukkit.plugin.java.JavaPlugin;

public final class TowerChallenge extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        TowerListener towerListener = new TowerListener();

        getServer().getPluginManager().registerEvents(towerListener, this);

        this.getCommand("removeBlocks").setExecutor(new RemoveBlocks(towerListener));
        this.getCommand("enableTower").setExecutor(new EnableTower(towerListener));
        this.getCommand("disableTower").setExecutor(new DisableTower(towerListener));

//        getLogger().info("Hello World!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
