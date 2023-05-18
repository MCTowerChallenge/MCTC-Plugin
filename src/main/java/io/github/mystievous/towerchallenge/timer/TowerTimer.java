package io.github.mystievous.towerchallenge.timer;

import io.github.mystievous.mystimer.TimeTrigger;
import io.github.mystievous.mystimer.Timer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.time.Duration;

// TODO: Create GUI for timer controls.

/**
 * Main game timer for the event.
 */
public class TowerTimer extends Timer {

    public static final String INTERMISSION_ID = "intermission";

    public static final String GAME_TIMER = "play";

    public static final Duration GAME_DURATION = Duration.ofHours(5);

    public TowerTimer(Plugin plugin) {
        super(plugin, GAME_TIMER, GAME_DURATION);

        // Announces to the server when this timer starts.
        setOnStart(() -> {
            Title title = Title.title(Component.text("Timer Started!").color(NamedTextColor.WHITE), Component.empty());
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.spawn"), Sound.Source.MASTER, 100, 1));
        });

        // Sets intermission to trigger and pause the timer at half the game timer.
        addTrigger(new TimeTrigger(INTERMISSION_ID, GAME_DURATION.dividedBy(2), timer1 -> {
            timer1.pause(false);
            Title title = Title.title(Component.text("Intermission").color(NamedTextColor.WHITE), Component.text("30 minute break!").color(NamedTextColor.BLUE));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.death"), Sound.Source.MASTER, 100, 1));
            Component newName = Component.text("Intermission!");
            timer1.setBossBarName(newName);
        }));

        // Announce to the server when the timer is paused.
        setOnPause(() -> {
            Title title = Title.title(Component.text("Timer Paused!").color(NamedTextColor.WHITE), Component.empty());
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.bell"), Sound.Source.MASTER, 100, 1));
        });

        // Announce to the server when the timer is resumed.
        setOnResume(() -> {
            Title title = Title.title(Component.text("Timer Resumed!").color(NamedTextColor.WHITE), Component.empty());
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.spawn"), Sound.Source.MASTER, 100, 1));
        });

        // Announce to the server when the timer is up.
        setOnEnd(() -> {
            Title title = Title.title(Component.text("Time is up!").color(NamedTextColor.WHITE), Component.text("No more crafting!").color(NamedTextColor.BLUE));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "ui.toast.challenge_complete"), Sound.Source.MASTER, 100, 1));
            Component newName = Component.text("Time is up!");
            setBossBarName(newName);
        });

    }
}
