package io.github.idkahn.towerchallenge.timer;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Timer extends BukkitRunnable {

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private LocalDateTime endTime;
    private Duration maxDuration;
    private Duration timeLeft;
    private TimerState state;
    private BossBar bossBar;
    private JavaPlugin plugin;

    private LocalDateTime lastPause;

    public Timer(JavaPlugin plugin) {
        LocalDateTime now = LocalDateTime.now();
        this.endTime = now.plusSeconds(30);
        this.maxDuration = new Duration(now, this.endTime);
        this.timeLeft = new Duration(this.maxDuration);
        this.lastPause = now;
        this.state = TimerState.PAUSED;
        this.plugin = plugin;
        this.bossBar = BossBar.bossBar(Component.text("Time Left: ").append(Component.text(timeLeft.getFormattedTime(), NamedTextColor.LIGHT_PURPLE)), 1.0f, BossBar.Color.WHITE, BossBar.Overlay.NOTCHED_12);
        this.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {

        if (this.state == TimerState.PAUSED) {
//            Bukkit.getServer().sendMessage(Component.text("Timer Paused"));
            return;
        }

        if (this.timeLeft.getTime() <= 0) {
//            Bukkit.getServer().sendMessage(Component.text(this.timeLeft.getTime() + " Timer Ended"));
            this.state = TimerState.ENDED;
        }

        if (this.state == TimerState.ENDED) {
            Component newName = Component.text("Time is up!");
            bossBar.name(newName);
            return;
        }

        if (this.state == TimerState.UNSET) {
            removeBossBar();
        }

        this.timeLeft.setFromDateTime(LocalDateTime.now(), this.endTime);
        Component newName = Component.text("Time Left: ")
            .append(Component.text(timeLeft.getFormattedTime(), NamedTextColor.LIGHT_PURPLE));
        bossBar.name(newName);
        float progress = this.timeLeft.getTime().floatValue()/this.maxDuration.getTime().floatValue();
//        Bukkit.getServer().sendMessage(Component.text(progress));
        if (0 < progress && progress < 1) {
            bossBar.progress(progress);
        } else if (progress <= 0) {
            bossBar.progress(0);
        } else if (1 <= progress) {
            bossBar.progress(1);
        }
//        Bukkit.getServer().sendMessage(Component.text(this.maxDuration.getTime() +" "+this.timeLeft.getTime() +" "+ this.timeLeft.getTime().floatValue()/this.maxDuration.getTime().floatValue()));
    }

    public void setDuration(Duration input) {
        LocalDateTime now = LocalDateTime.now();
        this.endTime = input.getDateTime(now);
        this.maxDuration = new Duration(input);
//        Bukkit.getServer().sendMessage(Component.text(now.toString()));
//        Bukkit.getServer().sendMessage(Component.text(this.endTime.toString()));
//        Bukkit.getServer().sendMessage(Component.text(input.toString()));
        this.timeLeft = new Duration(this.maxDuration);
        this.lastPause = now;
        this.state = TimerState.PAUSED;
        Component newName = Component.text("Time Left: ")
                .append(Component.text(timeLeft.getFormattedTime(), NamedTextColor.LIGHT_PURPLE));
        bossBar.name(newName);
        bossBar.progress(1.0f);
    }

    public void pause() {
        if (this.state == TimerState.ENDED || this.state == TimerState.UNSET) {
            return;
        }
        if (this.state != TimerState.PAUSED) {
            this.lastPause = LocalDateTime.now();
            this.state = TimerState.PAUSED;
        }
    }

    public void resume() {
        if (this.state == TimerState.ENDED || this.state == TimerState.UNSET) {
            return;
        }
        if (this.state != TimerState.RUNNING) {
            LocalDateTime now = LocalDateTime.now();
//            Bukkit.getServer().sendMessage(Component.text(this.endTime.format(timeFormat)));
            this.endTime = this.endTime.plusNanos(new Duration(this.lastPause, now).getTime());
//            Bukkit.getServer().sendMessage(Component.text(this.endTime.format(timeFormat)));
            this.state = TimerState.RUNNING;
        }
    }

    public Duration getMaxDuration() {
        return maxDuration;
    }

    public Duration getTimeLeft() {
        return timeLeft;
    }

    public void showBossBar() {

//        this.timeLeft.setFromDateTime(LocalDateTime.now(), this.time);
//        Component newName = Component.text("Time Left: ")
//                .append(Component.text(timeLeft.getFormattedTime(), NamedTextColor.LIGHT_PURPLE));
//        bossBar.name(newName);
        Bukkit.getServer().showBossBar(bossBar);
    }

    public void removeBossBar() {
//        this.cancel();
        Bukkit.getServer().hideBossBar(bossBar);
    }

    public void setEndTime(LocalDateTime time) {
        this.endTime = time;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setCurrentTime() {
        endTime = LocalDateTime.now();
    }

    public String getFormattedTime() {

        return endTime.format(timeFormat);
    }

    public String getTimeFromSet() {
        LocalDateTime fromDateTime = LocalDateTime.now();
        LocalDateTime toDateTime = endTime;

        Duration duration = new Duration(fromDateTime, toDateTime);

        return ( duration.getYears() + " years " +
                duration.getMonths() + " months " +
                duration.getDays() + " days " +
                duration.getHours() + " hours " +
                duration.getMinutes() + " minutes " +
                duration.getSeconds()  + " seconds.");
    }

    public TimerState getState() {
        return state;
    }
    public void setState(TimerState state) {
        this.state = state;
    }

}
