package io.github.mystievous.towerchallenge.timer;

import io.github.mystievous.mystimer.TimerUnsetException;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Commands for handling the timer
 */
public class TimerCommands implements CommandExecutor {

    private final TowerTimer timer;

    public TimerCommands(TowerTimer timer) {
        this.timer = timer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("timer")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case ("set") -> {
                        try {
                            String[] times = args[1].split(":");
                            if (times.length == 1) {
                                timer.setTimeLeft(Duration.ofSeconds(Integer.parseInt(times[0])));
                            } else if (times.length == 2) {
                                timer.setTimeLeft(Duration.ofMinutes(Integer.parseInt(times[0])).plusSeconds(Integer.parseInt(times[1])));
                            } else if (times.length == 3) {
                                timer.setTimeLeft(Duration.ofHours(Integer.parseInt(times[0])).plusMinutes(Integer.parseInt(times[1])).plusSeconds(Integer.parseInt(times[2])));
                            } else {
                                sender.sendMessage("Invalid Time");
                            }
                            timer.pause(false);
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(CommandUtils.errorMessage("Unable to set timer: " + e.getMessage()));
                        }
                    }
                    case ("show") -> timer.showBossBar();
                    case ("hide") -> timer.hideBossBar();
                    case ("reset") -> {
                        timer.reset();
                    }
                    case ("pause") -> timer.pause(true);
                    case ("start"), ("resume") -> {
                        try {
                            timer.start(true);
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
