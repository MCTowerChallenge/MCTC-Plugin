package io.github.mystievous.towerchallenge.quests;

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

    public Dialogue(TeamManager teamManager, Component message, double delay) {
        this.teamManager = teamManager;
        this.message = message;
        this.delay = (long) (delay * 20L);
    }

    public Dialogue(Dialogue dialogue) {
        this.teamManager = dialogue.teamManager;
        this.friendlyName = dialogue.friendlyName;
        this.next = dialogue.next;
        this.message = dialogue.message;
        this.delay = dialogue.delay;
        this.sound = dialogue.sound;
    }

    public Dialogue clone() {
        return new Dialogue(this);
    }

    public Dialogue setNext(Dialogue next) {
        this.next = next;
        return this;
    }

    public Dialogue setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public Dialogue setSoundKey(Key soundKey) {
        this.sound = Sound.sound(soundKey, Sound.Source.RECORD, 1f, 1f);
        return this;
    }

    public Dialogue setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        addDialogue(this);
        return this;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void play(Audience audience, Runnable callback) {
        audience.sendMessage(message);
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

//    public void play(TowerTeam team, Runnable callback) {
//        Audience audience = team;
//        audience.sendMessage(message);
//        if (soundKey != null) {
//            audience.playSound(Sound.sound(soundKey, Sound.Source.RECORD, 1f ,1f));
//        }
//        Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), () -> {
//            if (next != null) {
//                if (team.shouldStopDialogue()) {
//                    team.setInDialogue(false);
//                    team.setStopDialogue(false);
//                } else {
//                    next.play(team, callback);
//                }
//            } else {
//                if (callback != null) {
//                    callback.run();
//                }
//            }
//        }, delay);
//    }

    public void play(TowerTeam team) {
        play(team, null);
    }

    public void play(Player player) {
        play(player, null);
    }

}
