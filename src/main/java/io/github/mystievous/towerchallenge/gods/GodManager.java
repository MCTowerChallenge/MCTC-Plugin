package io.github.mystievous.towerchallenge.gods;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.gods.godgui.GodGui;
import io.github.mystievous.towerchallenge.towering.TowerListener;
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
