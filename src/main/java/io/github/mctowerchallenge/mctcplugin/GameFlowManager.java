package io.github.mctowerchallenge.mctcplugin;

import io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.quests.Jan2024QuestManager;
import io.github.mctowerchallenge.mctcplugin.eventspecific.may2024.quests.May2024QuestManager;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.CharacterManager;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.character.SteveSkellington;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mystimer.Timer;
import io.github.mystievous.mystimer.exception.TimerUnsetException;
import io.github.mctowerchallenge.mctcplugin.portal.PortalControllers;
import io.github.mctowerchallenge.mctcplugin.timer.TimerCommands;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Manages the flow of the game events and intermissions.
 */
public class GameFlowManager {

    private final Plugin plugin;
    private final Timer timer;
    private final CharacterManager characterManager;
    private final TeamManager teamManager;
    private final PortalControllers portalControllers;
    private final May2024QuestManager may2024QuestManager;

    private final Dialogue startDialogue;


    /**
     * Creates a new GameFlowManager instance.
     *
     * @param plugin The plugin instance.
     * @param timer The timer instance.
     * @param characterManager The character manager instance.
     * @param teamManager The team manager instance.
     */
    public GameFlowManager(Plugin plugin, Timer timer, CharacterManager characterManager, TeamManager teamManager, PortalControllers portalControllers, May2024QuestManager may2024QuestManager) {
        this.plugin = plugin;
        this.timer = timer;
        this.characterManager = characterManager;
        this.teamManager = teamManager;
        this.portalControllers = portalControllers;
        this.may2024QuestManager = may2024QuestManager;

        startDialogue = new Dialogue(plugin, Dialogue.playerThoughts("Oh no, that's terrible! How am I gonna get all the bee related blocks if there are no more bees?"), 6.0d);
        Dialogue startDialogue2 = new Dialogue(plugin, Dialogue.playerThoughts("I should see if there is someone around here I can talk to and ask how I can help."), 5.5d);
        startDialogue.setNext(startDialogue2);
    }

    public void resetQuests() {
        teamManager.getAllTeams().forEach(team -> {
            team.setQuest(QuestTags.GENERIC_BEE_CONSERVATIONIST_START);
        });
    }

    /**
     * Starts the game event.
     */
    public void startEvent() {
        Worlds.WORLD().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        Worlds.WORLD().setGameRule(GameRule.DO_MOB_SPAWNING, true);
        Worlds.WORLD().setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        Worlds.WORLD().setClearWeatherDuration(0);
        timer.reset();
        portalControllers.getNetherPortal().resetPortal();
        portalControllers.getEndPortal().resetPortal();
        try {
            timer.showBossBar();
            timer.startTimer();
        } catch (TimerUnsetException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        startDialogue.play(Bukkit.getServer(), () -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    teamManager.getAllTeams().forEach(team -> {
                        team.dealItemsAllPlayers();
                        team.completeQuest(QuestTags.NOT_STARTED);
                        team.setQuest(QuestTags.GENERIC_BEE_CONSERVATIONIST_START);
                    });
                }
            }.runTask(plugin);
        });
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

    /**
     * Triggers the winner announcement sequence
     */
//    public void triggerWinners(TowerTeam team, boolean developer) {
//        SteveSkellington steveSkellington = (SteveSkellington) CharacterManager.getCharacter(SteveSkellington.SteveTrait.class);
//        steveSkellington.getBallDropDialogue().play(Bukkit.getServer(), () -> {
//            try {
//                jan2024QuestManager.getNewYearsBall().run(team, developer);
//            } catch (TimerUnsetException e) {
//                e.printStackTrace();
//            }
//        });
//    }

//    public void triggerWinners(TowerTeam team) {
//        triggerWinners(team);
//    }

}