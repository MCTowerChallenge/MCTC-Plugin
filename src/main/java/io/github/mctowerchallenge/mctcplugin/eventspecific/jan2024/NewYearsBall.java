package io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024;

import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.quests.Jan2024QuestManager;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystimer.ScheduledAction;
import io.github.mystievous.mystimer.Timer;
import io.github.mystievous.mystimer.exception.TimerUnsetException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

public class NewYearsBall {

    private final Plugin plugin;
    private final TeamManager teamManager;

    private final Location startLocation;
    private ItemDisplay ball;
    private BukkitTask task;
    private BukkitTask fireworkTask;
    private static Random random = new SecureRandom();

    public NewYearsBall(Plugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        startLocation = new Location(Worlds.Jan2024(), -1401.8, 125.07, -334.2);
        reloadBall();
    }

    public void reloadBall() {
        if (ball != null) {
            ball.remove();
        }
        ball = (ItemDisplay) Worlds.Jan2024().spawnEntity(startLocation, EntityType.ITEM_DISPLAY);
        ball.addScoreboardTag(Jan2024QuestManager.REMOVE_TAG);
        ball.setTransformation(new Transformation(
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f),
                new Vector3f(10.0f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f))
        );
        ball.setItemStack(GuiUtil.formatItem("New Years Ball", Material.OBSIDIAN, 37));
        ball.addScoreboardTag("dee:locked");
    }

    private static final Duration timerTime = Duration.ofMinutes(2);

    private static Location fireworkCorner1 = new Location(Worlds.Jan2024(), -1409, 72, -356);
    private static Location fireworkCorner2 = new Location(Worlds.Jan2024(), -1395, 75, -342);

    private static void setFireworkMeta(TowerTeam team, Firework firework) {
        FireworkMeta meta = firework.getFireworkMeta();
        switch (random.nextInt(3)) {
            case (0) -> {
                meta.setPower(3);
                meta.addEffect(FireworkEffect.builder()
                        .with(FireworkEffect.Type.STAR)
                        .withColor(team.getColor().toBukkitColor())
                        .withFade(Color.FUCHSIA)
                        .trail(true)
                        .build()
                );
                firework.setFireworkMeta(meta);
            }
            case (1) -> {
                meta.setPower(2);
                meta.addEffect(FireworkEffect.builder()
                        .with(FireworkEffect.Type.BURST)
                        .withColor(team.getColor().toBukkitColor(), Color.LIME, Color.TEAL, Color.AQUA)
                        .withFade(Color.PURPLE, Color.WHITE)
                        .flicker(true)
                        .build()
                );
                firework.setFireworkMeta(meta);
            }
            case (2) -> {
                meta.setPower(2);
                meta.addEffect(FireworkEffect.builder()
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .withColor(team.getColor().toBukkitColor())
                        .withFade(Color.PURPLE)
                        .trail(true)
                        .build()
                );
                firework.setFireworkMeta(meta);
            }
        }
    }

    private float fireworkChance = 0.15f;

    public void run(TowerTeam winningTeam, boolean developer) throws TimerUnsetException {
        Timer timer = new Timer(plugin, Duration.ofMinutes(2), 1L);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                ball.teleport(ball.getLocation().subtract(new Vector(0, (float) 5 / (20 * 8), 0)));
            }
        };

        fireworkChance = 0.15f;

        BukkitRunnable fireworkRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (random.nextFloat() <= fireworkChance) {
                    Location fireworkLocation = new Location(Worlds.Jan2024(),
                            random.nextFloat((float) fireworkCorner1.getX(), (float) fireworkCorner2.getX()),
                            random.nextFloat((float) fireworkCorner1.getY(), (float) fireworkCorner2.getY()),
                            random.nextFloat((float) fireworkCorner1.getZ(), (float) fireworkCorner2.getZ())
                    );
                    Firework firework = (Firework) fireworkLocation.getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
                    setFireworkMeta(winningTeam, firework);
                }
            }
        };

        timer.registerStartAction(timer1 -> {
            Bukkit.getServer().stopSound(SoundStop.all());
            Location soundLocation = new Location(Worlds.Jan2024(), -1401.5, 72, -348.5);
            soundLocation.getWorld().playSound(soundLocation, "mctc:band.jan2024.fullmono", SoundCategory.RECORDS, 5, 1);
        });

        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(1475)), timer1 -> {
            Bukkit.getServer().showTitle(Title.title(Component.text(8), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofMillis(250), Duration.ofMillis(750))));
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
            if (fireworkTask != null && !fireworkTask.isCancelled()) {
                fireworkTask.cancel();
            }
            task = runnable.runTaskTimer(plugin, 0, 1);
        }));
        timer.registerScheduledAction(countAction(Duration.ofMillis(4449), 7));
        timer.registerScheduledAction(countAction(Duration.ofMillis(7465), 6));
        timer.registerScheduledAction(countAction(Duration.ofMillis(10417), 5));
        timer.registerScheduledAction(countAction(Duration.ofMillis(13384), 4));
        timer.registerScheduledAction(countAction(Duration.ofMillis(16344), 3));
        timer.registerScheduledAction(countAction(Duration.ofMillis(19300), 2));
        timer.registerScheduledAction(countAction(Duration.ofMillis(22278), 1));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(25200)), timer1 -> {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
            fireworkTask = fireworkRunnable.runTaskTimer(plugin, 0, 5);
            Title title = Title.title(winningTeam.getDisplayName(), Component.text("is the winner of the event!").color(NamedTextColor.WHITE));
            Bukkit.getServer().showTitle(title);
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.wither.death"), Sound.Source.RECORD, 0.25f, 1));
            if (!developer) {
                teamManager.updateWinningTeam(winningTeam);
            }
        }));

        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(28158)), timer1 -> {
            Bukkit.getServer().sendMessage(teamManager.getTowerScoresNoAdded());
        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(31148)), timer1 -> {

        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(34143)), timer1 -> {
            Bukkit.getServer().sendMessage(Component.text("Thanks to: ").decorate(TextDecoration.BOLD));
            Bukkit.getServer().sendMessage(getCredit("Hosts: ", new Component[]{
                    Component.text("Lafonda1525"),
                    Component.text("Chazz")
            }));
            Bukkit.getServer().sendMessage(Component.newline().append(getCredit("Builders: ", new Component[]{
                    Component.text("apple270"),
                    Component.text("Gumko"),
                    Component.text("Ajax")
            })));
        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(37006)), timer1 -> {

        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(40050)), timer1 -> {
            Bukkit.getServer().sendMessage(Component.newline().append(getCredit("Model Designers: ", new Component[]{
                    Component.text("apple270"),
                    Component.text("Mystievous")
            })));
            Bukkit.getServer().sendMessage(Component.newline().append(getCredit("Plugin Developer: ", new Component[]{
                    Component.text("Mystievous")
            })));
            Bukkit.getServer().sendMessage(Component.newline().append(getCredit("Music Composer: ", new Component[]{
                    Component.text("Mystievous")
            })));
        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(42996)), timer1 -> {

        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(46007)), timer1 -> {
            Bukkit.getServer().sendMessage(Component.newline().append(getCredit("Voice Actor: ", new Component[]{
                    Component.text("Qrow (steve skellington, Generic Maintenance Man)")
            })));
            Bukkit.getServer().sendMessage(Component.newline().append(getCredit("Patreon Supporters: ", new Component[]{
                    Component.text("Quinklyn"),
                    Component.text("ScaredArti"),
                    Component.text("Claire"),
                    Component.text("Tenshiku")
            })));
        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(48953)), timer1 -> {

        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(51881)), timer1 -> {

        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(54836)), timer1 -> {
            Bukkit.getServer().showTitle(Title.title(Component.text("Thank you!", winningTeam.getColor().toTextColor()), Component.text("For being here!")));
            fireworkChance = 0.5f;
        }));
        timer.registerScheduledAction(new ScheduledAction(actionTime(Duration.ofMillis(60776)), timer1 -> {

        }));


        timer.registerScheduledAction(new ScheduledAction(Duration.ofSeconds(58), timer1 -> {
            if (fireworkTask != null && !fireworkTask.isCancelled()) {
                fireworkTask.cancel();
            }
        }));

        timer.startTimer();

    }

    public void run(TowerTeam winningTeam) throws TimerUnsetException {
        run(winningTeam, false);
    }

    private Component getCredit(String role, Component[] people) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text(role, Palette.PRIMARY.toTextColor()));
        builder.appendNewline();
        for (int i = 0; i < people.length; i++) {
            Component component = people[i];
            builder.append(Component.text("- ")).append(component);
            if (i < people.length - 1) {
                builder.appendNewline();
            }
        }
        return builder.build();
    }

    private static Duration actionTime(Duration fromStart) {
        return timerTime.minus(fromStart);
    }

    public static ScheduledAction countAction(Duration fromStart, int number) {
        return new ScheduledAction(actionTime(fromStart), timer1 -> {
            Bukkit.getServer().showTitle(Title.title(Component.text(number), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofMillis(250), Duration.ofMillis(750))));
        });
    }

}
