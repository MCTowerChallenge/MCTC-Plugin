package io.github.idkahn.towerchallenge.gods;

import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.gods.godgui.GodGui;
import org.bukkit.Bukkit;

public class GodManager {

    private final GodGui godGui;
    private final ChallengeManager challengeManager;

    public GodManager(ChallengeManager challengeManager) {
        godGui = new GodGui(challengeManager);
        this.challengeManager = challengeManager;
        GodMenuCommand command = new GodMenuCommand(this);
        Bukkit.getPluginCommand("godmenu").setExecutor(command);
    }

    public ChallengeManager getEventManager() {
        return challengeManager;
    }

    public GodGui getGodGui() {
        return godGui;
    }
}
