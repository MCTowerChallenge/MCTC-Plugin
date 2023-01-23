package io.github.mystievous.towerchallenge.gods;

import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gods.godgui.GodGui;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.teleports.TeleportHistoryManager;
import org.bukkit.Bukkit;

public class GodManager {

    private final GodGui godGui;

    /**
     * Manager for everything pertaining to Gods
     *
     * @param teamManager   Team Manager for the event
     */
    public GodManager(TowerChallenge plugin, TeamManager teamManager, QuestManager questManager) {
        TeleportHistoryManager teleportHistoryManager = new TeleportHistoryManager(this);
        godGui = new GodGui(plugin, this, questManager, teleportHistoryManager, teamManager);

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
