package io.github.mctowerchallenge.mctcplugin.timer;

import io.github.mystievous.mystimer.ScheduledPausingAction;
import io.github.mystievous.mystimer.Timer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Main game timer for the event.
 */
public class TowerTimer extends Timer {

    // The duration of the entire game.
    public static final Duration GAME_DURATION = Duration.ofHours(5);

    /**
     * Constructs a TowerTimer.
     *
     * @param plugin The plugin instance.
     */
    public TowerTimer(Plugin plugin) {
        super(plugin, GAME_DURATION);

        // Announces to the server when this timer starts.
        registerStartAction(timer -> {
            Title title = Title.title(Component.text("Timer Started!").color(NamedTextColor.WHITE), Component.empty());
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.spawn"), Sound.Source.MASTER, 100, 1));
        });

        // Register a ScheduledPausingAction to trigger intermission at half the game timer.
        registerScheduledAction(getIntermissionAction());

        // Announce to the server when the timer is up.
        registerEndAction(timer -> {
            Title title = Title.title(Component.text("Time is up!").color(NamedTextColor.WHITE), Component.text("No more crafting!").color(NamedTextColor.BLUE));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "ui.toast.challenge_complete"), Sound.Source.MASTER, 100, 1));
            Component newName = Component.text("Time is up!");
            setBossBarName(newName);
        });

    }

    @NotNull
    private static ScheduledPausingAction getIntermissionAction() {
        ScheduledPausingAction intermissionAction = new ScheduledPausingAction(Duration.ofMinutes(150), timer -> {
            Title title = Title.title(Component.text("Intermission").color(NamedTextColor.WHITE), Component.text("30 minute break!").color(NamedTextColor.BLUE));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.death"), Sound.Source.MASTER, 100, 1));
            Component newName = Component.text("Intermission!");
            timer.setBossBarName(newName);
        });
        intermissionAction.setResumeAction(timer -> {
            Title title = Title.title(Component.text("Intermission Over!").color(NamedTextColor.WHITE), Component.empty());
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.spawn"), Sound.Source.MASTER, 100, 1));
        });
        return intermissionAction;
    }
}
