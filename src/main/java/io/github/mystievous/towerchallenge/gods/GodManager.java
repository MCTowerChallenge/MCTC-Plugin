package io.github.mystievous.towerchallenge.gods;

import io.github.mystievous.towerchallenge.eventspecific.feb2023.FerrisWheel;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.EvilTowerManager;
import io.github.mystievous.towerchallenge.gods.godgui.GodGui;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.teleports.TeleportHistoryManager;
import io.github.mystievous.towerchallenge.timer.Timer;
import org.bukkit.Bukkit;

public class GodManager {

    private final GodGui godGui;

    /**
     * Manager for everything pertaining to Gods
     *
     * @param teamManager   Team Manager for the event
     */
    public GodManager(TowerChallenge plugin, Timer timer, TeamManager teamManager, FerrisWheel ferrisWheel, QuestManager questManager, EvilTowerManager evilTowerManager) {
        TeleportHistoryManager teleportHistoryManager = new TeleportHistoryManager(this);
        godGui = new GodGui(plugin, timer, this, ferrisWheel, questManager, teleportHistoryManager, teamManager, evilTowerManager);

        GodMenuCommand command = new GodMenuCommand(this);
        Bukkit.getPluginCommand("godmenu").setExecutor(command);
    }

    /**
     * Gets the God Utility GUI
     *
     * @return the GUI
     */
    public GodGui getGodGui() {
        return godGui;
    }
}
