package io.github.idkahn.towerchallenge.timer;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                    case("set"):
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

                        break;
                    case("show"):
                        timer.showBossBar();
                        break;
                    case("hide"):
                        timer.removeBossBar();
                        break;
                    case("pause"):
                        timer.pause();
                        break;
                    case("start"):
                    case("resume"):
                        timer.resume();
                        break;
                    case("var"):
                        sender.sendMessage("EndTime: "+timer.getEndTime().toString());
                        sender.sendMessage("State: "+timer.getState().toString());
                        sender.sendMessage("MaxDuration: "+timer.getMaxDuration().toString());
                        sender.sendMessage("MaxDuration Long: "+timer.getMaxDuration().getTime());
                        sender.sendMessage("TimeLeft: "+timer.getTimeLeft().toString());
                        sender.sendMessage("TimeLeft Long: "+timer.getTimeLeft().getTime());
                }
            }
        }

        return true;
    }
}
