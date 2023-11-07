package io.github.mystievous.towerchallenge;

import io.github.mystievous.mystimer.Timer;
import io.github.mystievous.mystimer.exception.TimerUnsetException;
import io.github.mystievous.towerchallenge.interaction.npc.CharacterManager;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
import io.github.mystievous.towerchallenge.portal.PortalControllers;
import io.github.mystievous.towerchallenge.quest.QuestManager;
import io.github.mystievous.towerchallenge.team.ParticipantTeam;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.timer.TimerCommands;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Manages the flow of the game events and intermissions.
 */
public class GameFlowManager {

    private final Plugin plugin;
    private final Timer timer;
    private final CharacterManager characterManager;
    private final TeamManager teamManager;
    private final PortalControllers portalControllers;

    /**
     * Creates a new GameFlowManager instance.
     *
     * @param plugin The plugin instance.
     * @param timer The timer instance.
     * @param characterManager The character manager instance.
     * @param teamManager The team manager instance.
     */
    public GameFlowManager(Plugin plugin, Timer timer, CharacterManager characterManager, TeamManager teamManager, PortalControllers portalControllers) {
        this.plugin = plugin;
        this.timer = timer;
        this.characterManager = characterManager;
        this.teamManager = teamManager;
        this.portalControllers = portalControllers;
    }

    /**
     * Starts the game event.
     */
    public void startEvent() {
        try {
            timer.reset();
            timer.startTimer();
        } catch (TimerUnsetException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    /**
     * Ends the intermission and resumes the timer.
     */
    public void endIntermission() {
        portalControllers.getNetherPortal().openPortal();
        try {
            timer.startTimer();
            TimerCommands.announceResume();
        } catch (TimerUnsetException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

}
