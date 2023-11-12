package io.github.mctowerchallenge.towerchallenge.god;

import io.github.mctowerchallenge.towerchallenge.GameFlowManager;
import io.github.mctowerchallenge.towerchallenge.eventspecific.oct2023.quest.Oct2023QuestManager;
import io.github.mctowerchallenge.towerchallenge.god.godgui.GodGui;
import io.github.mctowerchallenge.towerchallenge.magic.MagicItems;
import io.github.mctowerchallenge.towerchallenge.quest.QuestManager;
import io.github.mctowerchallenge.towerchallenge.team.TeamManager;
import io.github.mctowerchallenge.towerchallenge.TowerChallenge;
import io.github.mctowerchallenge.towerchallenge.decoration.customblock.CustomBlockManager;
import io.github.mctowerchallenge.towerchallenge.portal.PortalControllers;
import io.github.mctowerchallenge.towerchallenge.teleport.TeleportHistoryManager;
import org.bukkit.Bukkit;

/**
 * Manages various functionalities related to Gods in the Tower Challenge plugin.
 */
public class GodManager {

    private final GodGui godGui;

    /**
     * Initializes a new instance of the GodManager class.
     *
     * @param plugin              The main plugin instance.
     * @param gameFlowManager     The manager for game flow.
     * @param questManager        The manager for quests.
     * @param oct2023QuestManager The manager for the June 2023 quests.
     * @param teamManager         The manager for teams.
     * @param magicItems          The manager for magical items.
     * @param portalControllers   The manager for portal controllers.
     */
    public GodManager(TowerChallenge plugin, GameFlowManager gameFlowManager, CustomBlockManager customBlockManager, QuestManager questManager, Oct2023QuestManager oct2023QuestManager, TeamManager teamManager, MagicItems magicItems, PortalControllers portalControllers) {
        TeleportHistoryManager teleportHistoryManager = new TeleportHistoryManager(plugin, this);
        godGui = new GodGui(plugin, this, gameFlowManager, customBlockManager, questManager, oct2023QuestManager, teleportHistoryManager, teamManager, magicItems, portalControllers);

        GodMenuCommand command = new GodMenuCommand(this);
        Bukkit.getPluginCommand("godmenu").setExecutor(command);
    }

    /**
     * Gets the God Utility GUI.
     *
     * @return The God Utility GUI.
     */
    public GodGui getGodGui() {
        return godGui;
    }
}
