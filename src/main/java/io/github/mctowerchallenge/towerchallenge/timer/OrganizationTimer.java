package io.github.mctowerchallenge.towerchallenge.timer;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystimer.Timer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.time.Duration;

/**
 * Timer for the organization phase
 * of the event.
 */
public class OrganizationTimer extends Timer {

    /**
     * Constructs an OrganizationTimer.
     *
     * @param plugin The plugin instance.
     */
    public OrganizationTimer(Plugin plugin) {
        super(plugin, Duration.ofMinutes(15));

        // Set the boss bar message displayed during the timer.
        setBarMessage(Component.text("Organization Time: "));

        // Register an action to be executed when the timer starts.
        registerStartAction(timer -> {
            Title title = Title.title(Component.text("Organization!").color(NamedTextColor.WHITE), TextUtil.formatText("Grab all your blocks!"));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.spawn"), Sound.Source.MASTER, 100, 1));
        });

        // Register an action to be executed when the timer ends.
        registerEndAction(timer -> {
            Title title = Title.title(Component.text("Time is up!").color(NamedTextColor.WHITE), Component.text("Head to the towering area!").color(NamedTextColor.BLUE));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "item.totem.use"), Sound.Source.MASTER, 100, 1));
            Component newName = Component.text("Head to the Towering Area!");
            setBossBarName(newName);
        });

    }
}
