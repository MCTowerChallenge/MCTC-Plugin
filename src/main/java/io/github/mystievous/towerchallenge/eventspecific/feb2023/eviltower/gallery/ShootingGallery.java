package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.gallery;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.EvilTower;
import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.mysticore.Palette;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

import java.security.SecureRandom;
import java.util.*;

public class ShootingGallery implements Listener {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Location[] baseRow1Locations = new Location[]{
            new Location(Worlds.eviltowers(), -34, 102, -43),
            new Location(Worlds.eviltowers(), -33, 102, -43),
            new Location(Worlds.eviltowers(), -32, 102, -43)
    };

    private static final Location[] baseRow2Locations = new Location[]{
            new Location(Worlds.eviltowers(), -25, 103, -47),
            new Location(Worlds.eviltowers(), -24, 103, -47),
            new Location(Worlds.eviltowers(), -23, 103, -47),
            new Location(Worlds.eviltowers(), -22, 103, -47),
            new Location(Worlds.eviltowers(), -21, 103, -47),
            new Location(Worlds.eviltowers(), -20, 103, -47),
    };

    public static final Location[] baseRow3Locations = new Location[]{
            new Location(Worlds.eviltowers(), -35, 104, -51),
            new Location(Worlds.eviltowers(), -34, 104, -51),
            new Location(Worlds.eviltowers(), -33, 104, -51),
            new Location(Worlds.eviltowers(), -32, 104, -51),
            new Location(Worlds.eviltowers(), -31, 104, -51),
            new Location(Worlds.eviltowers(), -30, 104, -51)
    };

    public static final Location[] baseRow4Locations = new Location[]{
            new Location(Worlds.eviltowers(), -26, 105, -54),
            new Location(Worlds.eviltowers(), -25, 105, -54),
            new Location(Worlds.eviltowers(), -24, 105, -54),
            new Location(Worlds.eviltowers(), -23, 105, -54),
            new Location(Worlds.eviltowers(), -22, 105, -54),
            new Location(Worlds.eviltowers(), -21, 105, -54),
    };

    public static final Location baseButtonLocation = new Location(Worlds.eviltowers(), -35, 103, -38);
    public static final Location baseDropperLocation = new Location(Worlds.eviltowers(), -28, 100, -34);
    public static final Location baseDropper2Location = new Location(Worlds.eviltowers(), -27, 100, -34);

    public static final int GOAL_SCORE = 75;
    public static final int TOTAL_SCORE = 200;
    public static final double TOTAL_TIME = 20;

    public static final int ROW_1_SCORE = 3;
    public static final int ROW_2_SCORE = 5;
    public static final int ROW_3_SCORE = 8;
    public static final int ROW_4_SCORE = 11;
    public static final double ROW_1_TIME = 6;
    public static final double ROW_2_TIME = 4;
    public static final double ROW_3_TIME = 3;
    public static final double ROW_4_TIME = 1.5;

    private final TowerChallenge plugin;
    private final TeamManager teamManager;
    private final QuestManager questManager;
    private final EvilTower evilTower;

    private final GalleryTarget[] row1Targets;
    private final GalleryTarget[] row2Targets;
    private final GalleryTarget[] row3Targets;
    private final GalleryTarget[] row4Targets;
    private final Location buttonLocation;
    private final Location dropperLocation;
    private final Location dropper2Location;
    public final int teamId;

    private final Collection<BukkitTask> tasks;
    private final Collection<Player> participants;

    private boolean active;

    private int points;

    private final Dialogue finishGallery;

    public ShootingGallery(TowerChallenge plugin, TeamManager teamManager, QuestManager questManager, EvilTower evilTower, int teamId) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.questManager = questManager;
        this.evilTower = evilTower;
        this.teamId = teamId;

        row1Targets = getGalleryTargets(baseRow1Locations, ROW_1_SCORE, ROW_1_TIME);
        row2Targets = getGalleryTargets(baseRow2Locations, ROW_2_SCORE, ROW_2_TIME);
        row3Targets = getGalleryTargets(baseRow3Locations, ROW_3_SCORE, ROW_3_TIME);
        row4Targets = getGalleryTargets(baseRow4Locations, ROW_4_SCORE, ROW_4_TIME);
        buttonLocation = evilTower.offsetLocation(baseButtonLocation);

        dropperLocation = evilTower.offsetLocation(baseDropperLocation);
        dropper2Location = evilTower.offsetLocation(baseDropper2Location);

        tasks = new HashSet<>();
        active = false;

        points = 0;

        participants = new HashSet<>();

        new GalleryRegion(plugin, evilTower, teamManager.getTeam(teamId));

        NPC spirit = questManager.getSpirit();



        finishGallery = new Dialogue(teamManager, spirit.formatMessage("Wow... Good job. /s"), 6.0d);
        finishGallery.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.good_job"));
        {
            Dialogue easiest = new Dialogue(teamManager, spirit.formatMessage("That was the easiest of the three, so don't feel too proud of yourself"), 4.5d);
            easiest.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.easiest"));
            finishGallery.setNext(easiest);
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    private GalleryTarget[] getGalleryTargets(Location[] locations, int pointValue, double time) {

        GalleryTarget[] targets = new GalleryTarget[locations.length];
        for (int i = 0; i < locations.length; i++) {
            targets[i] = new GalleryTarget(plugin, this, evilTower.offsetLocation(locations[i]), pointValue, time);
        }

        return targets;

    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        EquipmentSlot hand = event.getHand();
        if (hand != null && !hand.equals(EquipmentSlot.HAND))
            return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        Block block = event.getClickedBlock();
        if (block != null && block.getLocation().equals(buttonLocation.toBlockLocation())) {
            try {
                activate();
            } catch (GalleryAlreadyActiveException e) {
//                event.getPlayer().sendMessage(e.getMessage());
                Bukkit.getLogger().warning(e.getMessage());
            }
        }
    }

    public void activate() throws GalleryAlreadyActiveException {
        if (active) {
            throw new GalleryAlreadyActiveException();
        }
        active = true;
        List<GalleryTarget> targets = pickTargets(TOTAL_SCORE);
        for (GalleryTarget target : targets) {
            tasks.add(Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    target.activate();
                } catch (TargetAlreadyActivateException e) {
                    Location location = e.getLocation();
                    String message = String.format("Target activated when already activated: %.2f, %.2f, %.2f", location.getX(), location.getY(), location.getZ());
                    Bukkit.getLogger().warning(message);
                }
            }, RANDOM.nextLong(Math.round(TOTAL_TIME * 20))));
        }
        BukkitTask ticking = Bukkit.getScheduler().runTaskTimer(plugin, () -> buttonLocation.getWorld().playSound(buttonLocation, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 0), 0, 20);
        tasks.add(ticking);
        tasks.add(Bukkit.getScheduler().runTaskLater(plugin, ticking::cancel, Math.round((TOTAL_TIME + ROW_1_TIME - 1) * 20)));
        tasks.add(Bukkit.getScheduler().runTaskLater(plugin, this::timeUp, Math.round((TOTAL_TIME + ROW_1_TIME) * 20)));
    }

    public List<GalleryTarget> pickTargets(int totalPoints) {
        List<GalleryTarget> allTargets = new ArrayList<>();
        allTargets.addAll(Arrays.asList(row1Targets));
        allTargets.addAll(Arrays.asList(row2Targets));
        allTargets.addAll(Arrays.asList(row3Targets));
        allTargets.addAll(Arrays.asList(row4Targets));


        List<GalleryTarget> pickedTargets = new ArrayList<>();

        int remainingPoints = totalPoints;
        while (remainingPoints > 0 && !allTargets.isEmpty()) {
            int randomIndex = RANDOM.nextInt(allTargets.size());
            GalleryTarget pickedTarget = allTargets.get(randomIndex);
            allTargets.remove(randomIndex);
            int pickedPoints = pickedTarget.getPointValue();
            if (pickedPoints > remainingPoints) {
                // The picked target has too many points, try again
                continue;
            }
            pickedTargets.add(pickedTarget);
            remainingPoints -= pickedPoints;
        }

        return pickedTargets;
    }

    public void playerShot(Player player) {
        participants.add(player);
    }

    private void timeUp() {
        int finalPoints = getPoints();
        Audience audience = Audience.audience(participants);
        audience.sendMessage(Component.text("Final Score: ").color(Palette.PRIMARY.toTextColor())
                .append(Component.text(String.format("%d points", finalPoints)).color(NamedTextColor.WHITE)));
        if (finalPoints >= GOAL_SCORE) {
            buttonLocation.getWorld().playSound(buttonLocation, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            dropperLocation.clone().add(0, -1, 0).getBlock().setType(Material.REDSTONE_BLOCK);
            dropper2Location.clone().add(0, -1, 0).getBlock().setType(Material.REDSTONE_BLOCK);
            TowerTeam team = teamManager.getTeam(teamId);
            if (team != null && questManager.getTeamQuest(team).equals(QuestManager.SHOOTING_GALLERY)) {
                team.setInDialogue(true);
                questManager.setTeamQuest(team, QuestManager.PICK_TOWER_ROOM);
                finishGallery.play(team, () -> team.setInDialogue(false));
            }
        } else {
            buttonLocation.getWorld().playSound(buttonLocation, Sound.BLOCK_NOTE_BLOCK_SNARE, 1.0f, 1.0f);
        }
        participants.clear();
        active = false;
        points = 0;
    }

    public void deactivate() {
        for (BukkitTask task : tasks) {
            task.cancel();
            for (GalleryTarget target : row1Targets) {
                target.deactivate();
            }
            for (GalleryTarget target : row2Targets) {
                target.deactivate();
            }
            for (GalleryTarget target : row3Targets) {
                target.deactivate();
            }
            for (GalleryTarget target : row4Targets) {
                target.deactivate();
            }
        }
        participants.clear();
        active = false;
        points = 0;
    }

    @EventHandler
    public void onDisable(final PluginDisableEvent event) {
        if (event.getPlugin().getClass().equals(TowerChallenge.class)) {
            deactivate();
        }
    }

}
