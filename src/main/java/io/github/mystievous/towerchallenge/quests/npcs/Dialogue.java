package io.github.mystievous.towerchallenge.quests.npcs;

import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for sending a sequence of messages
 * to a {@link Player} or {@link TowerTeam}.
 */
public class Dialogue {

    private static final List<Dialogue> displayDialogues = new ArrayList<>();

    private static void addDialogue(Dialogue dialogue) {
        displayDialogues.add(dialogue);
    }

    public static List<Dialogue> getDialogues() {
        return displayDialogues;
    }

    private final TeamManager teamManager;
    private String friendlyName;
    private Dialogue next;
    private final Component message;
    private final long delay;
    private Sound sound;

    /**
     * Creates a new dialogue with a message.
     * <p></p>
     * To add to a dialogue sequence, use {@link #setNext(Dialogue)}.
     *
     * @param teamManager Current instance of {@link TeamManager}.
     * @param message     {@link Component} to put in chat for this dialogue.
     * @param delay       The delay, in seconds, to wait after the start of
     *                    this dialogue, before playing the next one.
     * @see #setSound(Sound)
     * @see #setSoundKey(Key)
     * @see #setNext(Dialogue)
     */
    public Dialogue(TeamManager teamManager, Component message, double delay) {
        this.teamManager = teamManager;
        this.message = message;
        this.delay = (long) (delay * 20L);
    }

    /**
     * Creates an empty dialogue.
     * Useful for adding a sound with no
     * message and/or in the middle of
     * the previous dialogue.
     * <p></p>
     * To add to a dialogue sequence, use {@link #setNext(Dialogue)}.
     *
     * @param teamManager Current instance of {@link TeamManager}.
     * @param message     {@link Component} to put in chat for this dialogue.
     * @param delay       The delay, in seconds, to wait after the start of
     *                    this dialogue, before playing the next one.
     * @see #setSound(Sound)
     * @see #setSoundKey(Key)
     * @see #setNext(Dialogue)
     */
    public Dialogue(TeamManager teamManager, double delay) {
        this(teamManager, null, delay);
    }

    /**
     * Makes a copy of the given dialogue.
     *
     * @param dialogue The {@link Dialogue} to copy.
     * @see #setSound(Sound)
     * @see #setSoundKey(Key)
     * @see #setNext(Dialogue)
     */
    public Dialogue(Dialogue dialogue) {
        this.teamManager = dialogue.teamManager;
        this.friendlyName = dialogue.friendlyName;
        this.next = dialogue.next;
        this.message = dialogue.message;
        this.delay = dialogue.delay;
        this.sound = dialogue.sound;
    }

    /**
     * Sets the next {@link Dialogue},
     * to play after this one's delay
     *
     * @param next The dialogue to set as the next one.
     */
    public void setNext(Dialogue next) {
        this.next = next;
    }

    /**
     * Sets the sound that plays along
     * with the dialogue.
     * <p></p>
     * Try to use {@link Sound.Source#RECORD},
     * to keep consistent with the rest of the plugin.
     *
     * @param sound The sound to play with the dialogue.
     * @return The same dialogue.
     */
    public Dialogue setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    /**
     * Sets the Sound {@link Key} for the
     * sound to play with the dialogue.
     * <p></p>
     * Sets the key at full volume, with
     * default pitch, on {@link Sound.Source#RECORD}
     *
     * @param soundKey The sound key
     */
    public void setSoundKey(Key soundKey) {
        this.sound = Sound.sound(soundKey, Sound.Source.RECORD, 1f, 1f);
    }

    /**
     * Sets the friendly name for
     * the dialogue.
     * <p>
     * Also calls {@link #addDialogue(Dialogue)}
     *
     * @param friendlyName The {@link String} to
     *                     call the dialogue.
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        addDialogue(this);
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Plays this dialogue and all
     * dialogues after it for the
     * given audience.
     *
     * @param audience The audience to play
     *                 the dialogue for
     * @param callback A {@link Runnable} to
     *                 run after the dialogue
     *                 sequence is done.
     */
    public void play(Audience audience, Runnable callback) {
        if (message != null) {
            audience.sendMessage(message);
        }
        if (sound != null) {
            audience.playSound(sound);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), () -> {
            if (next != null) {
                if (audience instanceof TowerTeam team && team.shouldStopDialogue()) {
                    team.setInDialogue(false);
                    team.setStopDialogue(false);
                    return;
                }
                next.play(audience, callback);
            } else {
                if (callback != null) {
                    callback.run();
                }
            }
        }, delay);
    }

    /**
     * @see #play(Audience, Runnable) 
     */
    public void play(Audience audience) {
        play(audience, null);
    }

}
