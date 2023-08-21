package io.github.mystievous.towerchallenge;

import io.github.mystievous.mystimer.Timer;
import io.github.mystievous.mystimer.exception.TimerUnsetException;
import io.github.mystievous.towerchallenge.interaction.npc.CharacterManager;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
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

    private final Dialogue intermissionDialogue;

    /**
     * Creates a new GameFlowManager instance.
     *
     * @param plugin The plugin instance.
     * @param timer The timer instance.
     * @param characterManager The character manager instance.
     * @param teamManager The team manager instance.
     */
    public GameFlowManager(Plugin plugin, Timer timer, CharacterManager characterManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.timer = timer;
        this.characterManager = characterManager;
        this.teamManager = teamManager;

        intermissionDialogue = new Dialogue(plugin, Dialogue.playerThoughts("You feel something strange in your pocket..."), 3.0d);
        intermissionDialogue.append(new Dialogue(plugin, Dialogue.playerThoughts("As you pull it out, you notice it's a small piece of paper with a note on it."), 5.0d));
        intermissionDialogue.append(new Dialogue(plugin, Dialogue.playerThoughts("It says \"Meet me at taco stand have info about band feud and drumsticks\""), 6.5d));
        intermissionDialogue.append(new Dialogue(plugin, Dialogue.playerThoughts("Hmmm, maybe we should go check it out..."), 3.5d));
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
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            characterManager.getEventStartDialogue().play(Audience.audience(Bukkit.getOnlinePlayers()), () -> {
                List<ParticipantTeam> teams = teamManager.getParticipantTeams();
                for (ParticipantTeam team : teams) {
                    team.setQuest(QuestManager.BAND_TROUBLE);
                }
            });
        }, 100);
    }

    /**
     * Ends the intermission and resumes the timer.
     */
    public void endIntermission() {

        if (QuestManager.stageLocation.getChunk().load() && QuestManager.foodLocation.getChunk().load()) {
            Entity daveEntity = Bukkit.getEntity(QuestManager.daveUUID);

            if (daveEntity != null) {
                daveEntity.teleport(QuestManager.foodLocation);
            }
        }

        try {
            timer.startTimer();
            TimerCommands.announceResume();
        } catch (TimerUnsetException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            intermissionDialogue.play(Audience.audience(Bukkit.getOnlinePlayers()), () -> {
                List<ParticipantTeam> teams = teamManager.getParticipantTeams();
                for (ParticipantTeam team : teams) {
                    team.setQuest(QuestManager.MEETING);
                }
            });
        }, 100);

    }

}
