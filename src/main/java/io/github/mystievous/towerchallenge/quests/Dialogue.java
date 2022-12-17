package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class Dialogue {

    private String friendlyName;
    private Dialogue next;
    private final Component message;
    private final long delay;
    private Key soundKey;

    public Dialogue(Component message, double delay) {
        this.message = message;
        this.delay = (long) (delay * 20L);
    }

    public Dialogue setNext(Dialogue next) {
        this.next = next;
        return this;
    }

    public Dialogue setSoundKey(Key soundKey) {
        this.soundKey = soundKey;
        return this;
    }

    public Dialogue setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        return this;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void play(Audience audience, Runnable callback) {
        audience.sendMessage(message);
        if (soundKey != null) {
            audience.playSound(Sound.sound(soundKey, Sound.Source.RECORD, 1f ,1f));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.me, () -> {
            if (next != null) {
                next.play(audience, callback);
            } else {
                if (callback != null) {
                    callback.run();
                }
            }
        }, delay);
    }

    public void play(Player player, Consumer<Player> callback) {
        TowerTeam team = TowerChallenge.me.getChallengeManager().getPlayerTeam(player);
        if (team == null) {
            return;
        }
        Audience audience = team.getAudience();
        audience.sendMessage(message);
        if (soundKey != null) {
            audience.playSound(Sound.sound(soundKey, Sound.Source.RECORD, 1f ,1f));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.me, () -> {
            if (next != null) {
                next.play(player, callback);
            } else {
                if (callback != null) {
                    callback.accept(player);
                }
            }
        }, delay);
    }

    public void play(Player player) {
        play(player, (Consumer<Player>) null);
    }

}
