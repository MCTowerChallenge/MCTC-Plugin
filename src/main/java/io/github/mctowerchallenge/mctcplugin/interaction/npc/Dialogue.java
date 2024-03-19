package io.github.mctowerchallenge.mctcplugin.interaction.npc;

import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sequence of messages and sounds to be played to a {@link Player} or {@link TowerTeam}.
 */
public class Dialogue {

    private static final List<Dialogue> displayDialogues = new ArrayList<>();

    /**
     * Adds a dialogue to the list of available dialogues.
     *
     * @param dialogue The dialogue to add.
     */
    private static void addDialogue(Dialogue dialogue) {
        displayDialogues.add(dialogue);
    }

    /**
     * Gets the list of available dialogues.
     *
     * @return The list of dialogues.
     */
    public static List<Dialogue> getDialogues() {
        return displayDialogues;
    }

    /**
     * Formats text to appear as a player's thoughts.
     *
     * @param text The message to format.
     * @return The formatted {@link Component}.
     */
    public static Component playerThoughts(Component text) {
        return TextUtil.formatText(text).decoration(TextDecoration.ITALIC, true);
    }

    /**
     * Formats text to appear as a player's thoughts.
     *
     * @param text The message to format.
     * @return The formatted {@link Component}.
     */
    public static Component playerThoughts(String text) {
        return playerThoughts(Component.text(text));
    }

    private final Plugin plugin;
    private String friendlyName;
    private Dialogue previous;
    private Dialogue next;
    private final Component message;
    private final long delayTicks;
    private Sound sound;

    /**
     * Creates a new dialogue with a message.
     * <p></p>
     * To add to a dialogue sequence, use {@link #setNext(Dialogue)}.
     *
     * @param plugin       Plugin dialogue was created by.
     * @param message      {@link Component} to put in chat for this dialogue.
     * @param delaySeconds The delay, in seconds, to wait after the start of
     *                     this dialogue, before playing the next one.
     * @see #setSound(Sound)
     * @see #setSoundKey(Key)
     * @see #setNext(Dialogue)
     */
    public Dialogue(Plugin plugin, Component message, double delaySeconds) {
        this.plugin = plugin;
        this.message = message;
        this.delayTicks = (long) (delaySeconds * 20L);
    }

    /**
     * Creates a new dialogue with a message.
     * <p></p>
     * To add to a dialogue sequence, use {@link #setNext(Dialogue)}.
     *
     * @param plugin       The plugin creating the dialogue.
     * @param message      The message to display in the dialogue.
     * @param delaySeconds The delay in seconds before playing the next dialogue.
     * @param soundKey     The sound key to play with the dialogue.
     * @see #setNext(Dialogue)
     */
    public Dialogue(Plugin plugin, Component message, double delaySeconds, Key soundKey) {
        this.plugin = plugin;
        this.message = message;
        this.delayTicks = (long) (delaySeconds * 20L);
        setSoundKey(soundKey);
    }

    /**
     * Creates an empty dialogue.
     * Useful for adding a sound with no
     * message and/or in the middle of
     * the previous dialogue.
     * <p></p>
     * To add to a dialogue sequence, use {@link #setNext(Dialogue)}.
     *
     * @param plugin The plugin creating the dialogue.
     * @param delay  The delay in seconds before playing the next dialogue.
     * @see #setSound(Sound)
     * @see #setSoundKey(Key)
     * @see #setNext(Dialogue)
     */
    public Dialogue(Plugin plugin, double delay) {
        this(plugin, null, delay);
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
        this.plugin = dialogue.plugin;
        this.friendlyName = dialogue.friendlyName;
        this.next = dialogue.next;
        this.message = dialogue.message;
        this.delayTicks = dialogue.delayTicks;
        this.sound = dialogue.sound;
    }

    /**
     * Sets the next dialogue in the sequence.
     *
     * @param next The dialogue to set as the next one.
     */
    public void setNext(Dialogue next) {
        this.next = next;
    }

    private void setPrevious(Dialogue previous) {
        this.previous = previous;
    }

    /**
     * Appends a dialogue to the end of this dialogue's sequence.
     *
     * @param dialogue The dialogue to append.
     */
    public Dialogue append(Dialogue dialogue) {
        if (next != null) {
            next.append(dialogue);
        } else {
            dialogue.setPrevious(this);
            setNext(dialogue);
        }
        return this;
    }

    /**
     * Appends a dialogue to the end of this dialogue's sequence.
     *
     * @param message      The component to append.
     * @param delaySeconds The delay in seconds after appending the dialogue to play the one after it.
     * @return The appended dialogue.
     */
    public Dialogue append(Component message, double delaySeconds) {
        Dialogue dialogue = new Dialogue(this.plugin, message, delaySeconds);
        if (next != null) {
            next.append(dialogue);
        } else {
            dialogue.setPrevious(this);
            setNext(dialogue);
        }
        return this;
    }

    /**
     * Appends a dialogue to the end of this dialogue's sequence.
     *
     * @param message      The component to append.
     * @param delaySeconds The delay in seconds after appending the dialogue to play the one after it.
     * @param soundKey     The sound key to play with this dialogue.
     */
    public void append(Component message, double delaySeconds, Key soundKey) {
        Dialogue dialogue = new Dialogue(this.plugin, message, delaySeconds);
        dialogue.setSoundKey(soundKey);
        if (next != null) {
            next.append(dialogue);
        } else {
            dialogue.setPrevious(this);
            setNext(dialogue);
        }
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
     * Sets the Sound {@link Key} to play with the dialogue.
     * <p></p>
     * Sets the key at full volume, with
     * default pitch, on {@link Sound.Source#RECORD}
     *
     * @param soundKey The sound key
     * @return This same dialogue.
     */
    public Dialogue setSoundKey(Key soundKey) {
        this.sound = Sound.sound(soundKey, Sound.Source.RECORD, 1f, 1f);
        return this;
    }

    /**
     * Sets the friendly name for the dialogue and adds it to the list of dialogues.
     *
     * @param friendlyName The friendly name to set.
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        addDialogue(this);
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Plays this dialogue and all subsequent dialogues for the given audience.
     *
     * @param audience The audience to play the dialogue for.
     * @param callback A runnable to run after the dialogue sequence is complete.
     */
    public void play(Audience audience, Runnable callback) {
        if (message != null) {
            audience.sendMessage(message);
        }
        if (sound != null) {
            audience.playSound(sound);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
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
        }, delayTicks);
    }

    /**
     * Plays this dialogue and all subsequent dialogues for the given audience.
     *
     * @param audience The audience to play the dialogue for.
     */
    public void play(Audience audience) {
        play(audience, null);
    }

}
