package io.github.mystievous.towerchallenge.timer;

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

    public static final String TIMER_ID = "organization";

    public OrganizationTimer(Plugin plugin) {
        super(plugin, TIMER_ID, Duration.ofMinutes(15));

        setOnStart(() -> {
            Title title = Title.title(Component.text("Organization Phase!").color(NamedTextColor.WHITE), TextUtil.formatText("Blocks into shulker boxes"));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.spawn"), Sound.Source.MASTER, 100, 1));
        });

        setOnEnd(() -> {
            Title title = Title.title(Component.text("Organization Over!").color(NamedTextColor.WHITE), Component.text("Head to the towering area").color(NamedTextColor.BLUE));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "item.totem.use"), Sound.Source.MASTER, 100, 1));
            Component newName = Component.text("Organization phase over!");
            setBossBarName(newName);
        });

    }
}
