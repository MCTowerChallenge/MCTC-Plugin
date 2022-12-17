package io.github.mystievous.towerchallenge.timer;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Timer extends BukkitRunnable {

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private LocalDateTime endTime;
    private final Duration maxDuration;
    private Duration timeLeft;
    private final Duration intermission;
    private boolean hadIntermission;
    private boolean started;
    private TimerState state;
    private final BossBar bossBar;

    private boolean bossBarShown;

    private LocalDateTime lastPause;

    public Timer(JavaPlugin plugin) {
        LocalDateTime now = LocalDateTime.now();
        this.endTime = now.plusHours(5);
        this.maxDuration = new Duration(now, this.endTime);
        this.timeLeft = new Duration(this.maxDuration);
        this.intermission = new Duration(0, 0, 0, 2, 30, 0, 0);
        this.hadIntermission = false;
        this.started = false;
        this.lastPause = now;
        this.state = TimerState.PAUSED;
        this.bossBar = BossBar.bossBar(Component.text("Time Left: ").append(Component.text(timeLeft.getFormattedTime(), NamedTextColor.BLUE)), 1.0f, BossBar.Color.WHITE, BossBar.Overlay.NOTCHED_10);
        this.bossBar.color(BossBar.Color.BLUE);
        this.bossBarShown = false;
        TimerListener timerListener = new TimerListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(timerListener, plugin);
        this.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {

        if (this.state == TimerState.PAUSED) {
//            Bukkit.getServer().sendMessage(Component.text("Timer Paused"));
            return;
        }

        if (!this.hadIntermission) {
            if (this.timeLeft.getTime() <= intermission.getTime()) {
                pause();
                this.hadIntermission = true;
                Title title = Title.title(Component.text("Intermission").color(NamedTextColor.WHITE), Component.text("30 minute break!").color(NamedTextColor.BLUE));
                Bukkit.getServer().showTitle(title);
                Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.death"), Sound.Source.MASTER, 100, 1));
                Component newName = Component.text("Intermission!");
                bossBar.name(newName);
                return;
            }
        }
        if (this.timeLeft.getTime() <= 0) {
//            Bukkit.getServer().sendMessage(Component.text(this.timeLeft.getTime() + " Timer Ended"));
            if (this.state != TimerState.ENDED) {
                Title title = Title.title(Component.text("Time is up!").color(NamedTextColor.WHITE), Component.text("No more crafting!").color(NamedTextColor.BLUE));
                Bukkit.getServer().showTitle(title);
                Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "ui.toast.challenge_complete"), Sound.Source.MASTER, 100, 1));
                Component newName = Component.text("Time is up!");
                bossBar.name(newName);
                this.state = TimerState.ENDED;
            }
        }

        if (this.state == TimerState.ENDED) {
            return;
        }

        if (this.state == TimerState.UNSET) {
            removeBossBar();
        }

        this.timeLeft.setFromDateTime(LocalDateTime.now(), this.endTime);
        Component newName = Component.text("Time Left: ")
            .append(Component.text(timeLeft.getFormattedTime(), NamedTextColor.BLUE));
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
        this.hadIntermission = input.compareTo(intermission) <= 0;
        LocalDateTime now = LocalDateTime.now();
        this.endTime = input.getDateTime(now);
//        this.maxDuration = new Duration(input);
//        Bukkit.getServer().sendMessage(Component.text(now.toString()));
//        Bukkit.getServer().sendMessage(Component.text(this.endTime.toString()));
//        Bukkit.getServer().sendMessage(Component.text(input.toString()));
        this.timeLeft = new Duration(input);
        this.lastPause = now;
        this.state = TimerState.PAUSED;
        Component newName = Component.text("Time Left: ")
                .append(Component.text(timeLeft.getFormattedTime(), NamedTextColor.BLUE));
        bossBar.name(newName);
        bossBar.progress(this.timeLeft.getTime().floatValue()/this.maxDuration.getTime().floatValue());
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

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void resume() {
        if (this.state == TimerState.ENDED || this.state == TimerState.UNSET) {
            return;
        }
        if (!this.started) {
            this.started = true;
            Title title = Title.title(Component.text("Timer Started!").color(NamedTextColor.WHITE), Component.empty());
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.spawn"), Sound.Source.MASTER, 100, 1));
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

    public BossBar getBossBar() {
        return bossBar;
    }

    public boolean isBossBarShown() {
        return bossBarShown;
    }

    public void showBossBar() {
        bossBarShown = true;
//        this.timeLeft.setFromDateTime(LocalDateTime.now(), this.time);
//        Component newName = Component.text("Time Left: ")
//                .append(Component.text(timeLeft.getFormattedTime(), NamedTextColor.BLUE));
//        bossBar.name(newName);
        Bukkit.getServer().showBossBar(bossBar);
    }

    public void removeBossBar() {
//        this.cancel();
        bossBarShown = false;
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
