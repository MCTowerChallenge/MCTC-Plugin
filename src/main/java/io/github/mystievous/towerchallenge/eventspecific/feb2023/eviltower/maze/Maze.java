package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.maze;

import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.event.MVPortalEvent;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.ValentinesUtil;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.TowerPortalManager;
import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.FullInventory;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Maze implements Listener {

    private boolean hasBeenReset;

    private final TowerChallenge plugin;
    private final TeamManager teamManager;
    private final int teamId;
    private final QuestManager questManager;

    private final Dialogue finishMaze;
    private final Dialogue resetMaze;

    public Maze(TowerChallenge plugin, TeamManager teamManager, QuestManager questManager, int teamId) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.teamId = teamId;
        this.questManager = questManager;
        hasBeenReset = false;

        NPC spirit = questManager.getSpirit();

        finishMaze = new Dialogue(teamManager, spirit.formatMessage("Finally you made it to the exit."), 3.0d);
        finishMaze.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.made_it_to_exit"));
        {
            Dialogue foundTheMap = new Dialogue(teamManager, spirit.formatMessage("What, you never found the map? Oh well!"), 5.5d);
            foundTheMap.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.found_the_map"));
            finishMaze.setNext(foundTheMap);
        }

        resetMaze = new Dialogue(teamManager, spirit.formatMessage("By the way, you might want to watch for traps."), 5.0d);
        resetMaze.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.watch_for_traps"));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerPortal(MVPortalEvent event) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            MVPortal portal = event.getSendingPortal();
            Player player = event.getTeleportee();

            TowerTeam team = teamManager.getTeam(teamId);

            if (team == null)
                return;

            if (portal.getName().equals(TeamUtils.toTeamTag(team, TowerPortalManager.MAZE_EXIT))) {
                if (questManager.getTeamQuest(team).equals(QuestManager.LIBRARY_MAZE)) {
                    FullInventory.givePlayerItems(player, ValentinesUtil.mazeKey, ValentinesUtil.oceanExploreMap);
                    team.setInDialogue(true);
                    questManager.setTeamQuest(team, QuestManager.PICK_TOWER_ROOM);
                    finishMaze.play(team, () -> team.setInDialogue(false));
                }

            }

            if (portal.getName().equals(TeamUtils.toTeamTag(team, TowerPortalManager.MAZE_RESET))) {
                if (!hasBeenReset) {
                    team.setInDialogue(true);
                    resetMaze.play(team, () -> team.setInDialogue(false));
                    hasBeenReset = true;
                }
            }
        });


    }

}
