package io.github.idkahn.towerchallenge.gods;

import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.gods.godgui.GodGui;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import org.bukkit.Bukkit;

public class GodManager {

    private final GodGui godGui;

    public GodManager(ChallengeManager challengeManager, TowerListener towerListener) {
        godGui = new GodGui(challengeManager, this, towerListener);

        GodMenuCommand command = new GodMenuCommand(this);
        Bukkit.getPluginCommand("godmenu").setExecutor(command);
    }

    public GodGui getGodGui() {
        return godGui;
    }
}
