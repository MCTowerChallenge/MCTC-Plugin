package io.github.idkahn.towerchallenge.timer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TimerCommands implements CommandExecutor {

    Timer timer;

    public TimerCommands(Timer timer) {
        this.timer = timer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("timer")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case ("set") -> {
                        String[] times = args[1].split(":");
                        if (times.length == 1) {
                            timer.setDuration(new Duration(0, 0, 0, 0, 0, Integer.parseInt(times[0]), 0));
                        } else if (times.length == 2) {
                            timer.setDuration(new Duration(0, 0, 0, 0, Integer.parseInt(times[0]), Integer.parseInt(times[1]), 0));
                        } else if (times.length == 3) {
                            timer.setDuration(new Duration(0, 0, 0, Integer.parseInt(times[0]), Integer.parseInt(times[1]), Integer.parseInt(times[2]), 0));
                        } else {
                            sender.sendMessage("Invalid Time");
                        }
                        timer.pause();
                    }
                    case ("show") -> timer.showBossBar();
                    case ("hide") -> timer.removeBossBar();
                    case ("reset") -> {
                        timer.setStarted(false);
                        timer.setDuration(new Duration(0, 0, 0, 5, 0, 0, 0));
                        timer.pause();
                    }
                    case ("pause") -> timer.pause();
                    case ("start"), ("resume") -> timer.resume();

                }
            }
        }

        return true;
    }
}
