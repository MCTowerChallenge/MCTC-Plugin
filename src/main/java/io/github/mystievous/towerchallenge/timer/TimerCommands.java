package io.github.mystievous.towerchallenge.timer;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystimer.Timer;
import io.github.mystievous.mystimer.exception.TimerUnsetException;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.format.DateTimeParseException;

/**
 * Commands for handling the timer
 */
public class TimerCommands implements CommandExecutor {

    /**
     * Announces that the timer is paused.
     */
    public static void announcePause() {
        Title title = Title.title(Component.text("Timer Paused!").color(NamedTextColor.WHITE), Component.empty());
        Bukkit.getServer().showTitle(title);
        Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.bell"), Sound.Source.MASTER, 100, 1));
    }

    /**
     * Announces that the timer is resumed.
     */
    public static void announceResume() {
        Title title = Title.title(Component.text("Timer Resumed!").color(NamedTextColor.WHITE), Component.empty());
        Bukkit.getServer().showTitle(title);
        Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.bell"), Sound.Source.MASTER, 100, 1.498307f));
    }

    private final TowerTimer towerTimer;
    private final OrganizationTimer organizationTimer;
    private Timer activeTimer;

    private boolean isOrganization;

    /**
     * Constructs a TimerCommands instance.
     *
     * @param towerTimer        The TowerTimer instance.
     * @param organizationTimer The OrganizationTimer instance.
     */
    public TimerCommands(TowerTimer towerTimer, OrganizationTimer organizationTimer) {
        this.towerTimer = towerTimer;
        this.organizationTimer = organizationTimer;
        this.isOrganization = false;
        this.activeTimer = towerTimer;
    }

    /**
     * Changes the active timer to the provided timer.
     *
     * @param timer The timer to set as active.
     */
    public void changeTimer(Timer timer) {
        activeTimer.pauseTimer();
        activeTimer.hideBossBar();
        timer.reset();
        timer.showBossBar();
        activeTimer = timer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("timer")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case ("set") -> {
                        try {
                            activeTimer.setTimeLeft(Duration.parse(args[1]));
                            activeTimer.pauseTimer();
                        } catch (DateTimeParseException | IllegalArgumentException e) {
                            sender.sendMessage(CommandUtils.errorMessage("Unable to set timer: " + e.getMessage()));
                        }
                    }
                    case ("help") -> sender.sendMessage(TextUtil.formatText("To set the timer, use the set command: ")
                            .appendNewline().append(TextUtil.formatText("`/timer set PT2H35M`")));
                    case ("show") -> activeTimer.showBossBar();
                    case ("hide") -> activeTimer.hideBossBar();
                    case ("phase") -> {
                        if (!isOrganization) {
                            changeTimer(organizationTimer);
                            isOrganization = true;
                        } else {
                            changeTimer(towerTimer);
                            isOrganization = false;
                        }
                    }
                    case ("reset") -> activeTimer.reset();
                    case ("pause") -> {
                        activeTimer.pauseTimer();
                        announcePause();
                    }
                    case ("resume") -> {
                        try {
                            activeTimer.startTimer();
                            announceResume();
                        } catch (TimerUnsetException e) {
                            Bukkit.getLogger().warning(e.getMessage());
                            sender.sendMessage(CommandUtils.errorMessage("Timer is not initialized"));
                        }
                    }
                    case ("start") -> {
                        try {
                            activeTimer.startTimer();
                        } catch (TimerUnsetException e) {
                            Bukkit.getLogger().warning(e.getMessage());
                            sender.sendMessage(CommandUtils.errorMessage("Timer is not initialized"));
                        }
                    }
                }
            }
        }

        return true;
    }
}
