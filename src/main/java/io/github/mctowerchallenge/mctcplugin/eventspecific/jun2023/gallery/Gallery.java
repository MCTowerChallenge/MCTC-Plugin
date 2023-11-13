package io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.gallery;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Manages the gallery shooting game for the Jun 2023 event.
 */
public class Gallery implements Listener {

    private static final Random RANDOM = new Random();

    /**
     * Calculate the time taken for a point value.
     *
     * @param points The number of points.
     * @return The time taken for the points.
     */
    public static double pointTime(int points) {
        // (0.7+(-0.2 * z))^(x-4) + 1 + z

        double vary = RANDOM.nextDouble(1);

//        return 5;

        return pointTime(points, vary);
    }

    /**
     * Calculate the time taken for a point value with a modifier.
     *
     * @param points    The number of points.
     * @param modifier  The modifier to apply.
     * @return The time taken for the points with the modifier.
     */
    private static double pointTime(int points, double modifier) {
        return Math.pow(0.7 + (-0.2 * modifier), points - 4) + 1 + modifier;
    }

    public static final int TOTAL_SCORE = 40;
    public static final double TOTAL_TIME = 15;

    public static final Location BUTTON_LOCATION = new Location(Worlds.Jun2023(), 149, 64, -2243);

    private static final Map<Integer, Location[]> targetLocations = new HashMap<>() {{
        put(1, new Location[]{
                new Location(Worlds.Jun2023(), 147, 64, -2253)
        });
        put(2, new Location[]{
                new Location(Worlds.Jun2023(), 146, 64, -2247)
        });
        put(3, new Location[]{
                new Location(Worlds.Jun2023(), 145, 64, -2243),
                new Location(Worlds.Jun2023(), 145, 65, -2244),
                new Location(Worlds.Jun2023(), 145, 65, -2245),
                new Location(Worlds.Jun2023(), 145, 64, -2246)
        });
        put(5, new Location[]{
                new Location(Worlds.Jun2023(), 143, 64, -2248),
                new Location(Worlds.Jun2023(), 143, 64, -2252),
        });
        put(6, new Location[]{
                new Location(Worlds.Jun2023(), 142, 64, -2243),
                new Location(Worlds.Jun2023(), 142, 64, -2249),
                new Location(Worlds.Jun2023(), 142, 65, -2250),
                new Location(Worlds.Jun2023(), 142, 65, -2251),
        });
        put(7, new Location[]{
                new Location(Worlds.Jun2023(), 141, 65, -2241),
                new Location(Worlds.Jun2023(), 141, 64, -2242),
        });
    }};

    private final Plugin plugin;
    private final List<GalleryTarget> targets;
    private int points;
    private final Set<Audience> shooters;

    private boolean active;

    public Gallery(Plugin plugin) {
        this.plugin = plugin;

        targets = new ArrayList<>();
        points = 0;
        shooters = new HashSet<>();

        for (Map.Entry<Integer, Location[]> entry : targetLocations.entrySet()) {
            for (Location location : entry.getValue()) {
                targets.add(new GalleryTarget(plugin, this, location, entry.getKey()));
            }
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    /**
     * Add points to the gallery game's current score.
     *
     * @param points The points to add.
     * @return The updated total points.
     */
    public int addPoints(int points) {
        this.points += points;
        return this.points;
    }

    /**
     * Get the current score of the gallery game.
     *
     * @return The current score.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Get the audience (players) participating in the gallery game.
     *
     * @return The audience.
     */
    public Audience getShooters() {
        return Audience.audience(shooters);
    }

    /**
     * Add a player to the list of participants in the gallery game.
     *
     * @param audience The player to add.
     */
    public void addShooter(Audience audience) {
        shooters.add(audience);
    }

    /**
     * Pick targets based on the total points to achieve and their respective point values.
     *
     * @param totalPoints The total points to achieve.
     * @return A list of selected GalleryTargets.
     */
    public List<GalleryTarget> pickTargets(int totalPoints) {
        List<GalleryTarget> allTargets = new ArrayList<>(targets);
        List<GalleryTarget> pickedTargets = new ArrayList<>();
        List<GalleryTarget> negatives = new ArrayList<>();

        int remainingPoints = totalPoints;

        while (remainingPoints > 0 && !allTargets.isEmpty()) {
//            Bukkit.getLogger().info(String.format("Remaining Points: %d", remainingPoints));
            int randomIndex = RANDOM.nextInt(allTargets.size());
            GalleryTarget pickedTarget = allTargets.get(randomIndex);
            int pickedPoints = pickedTarget.getPointValue();
            allTargets.remove(randomIndex);
            if (pickedPoints > remainingPoints) {
                // The target has too many points, try again
                negatives.add(pickedTarget);
                continue;
            }
            pickedTarget.setPositive(true);
            pickedTargets.add(pickedTarget);
            remainingPoints -= pickedPoints;

        }

        negatives.addAll(allTargets);

        if (!negatives.isEmpty()) {
            int negativeCount;

            if (negatives.size() > 3) {
                negativeCount = 1 + (RANDOM.nextInt(negatives.size() - 3)) + 3;
            } else {
                negativeCount = negatives.size();
            }

            for (int i = 0; i < negativeCount; i++) {
                int randomIndex = RANDOM.nextInt(negatives.size());
                GalleryTarget pickedTarget = negatives.get(randomIndex);
                pickedTarget.setPositive(false);
                negatives.remove(pickedTarget);
                pickedTargets.add(pickedTarget);
            }
        }


        return pickedTargets;
    }

    /**
     * Activate the gallery game.
     *
     * @throws GalleryAlreadyActiveException If the gallery is already active.
     */
    public void activate() throws GalleryAlreadyActiveException {
        if (active) {
            throw new GalleryAlreadyActiveException();
        }
        active = true;

        // Asynchronously pick targets and activate them with random delays
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<GalleryTarget> pickedTargets = pickTargets(TOTAL_SCORE);
            for (GalleryTarget target : pickedTargets) {
                long time = RANDOM.nextLong(Math.round(TOTAL_TIME * 20));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    try {
                        target.activate(pointTime(target.getPointValue()));
                    } catch (TargetAlreadyActivateException e) {
                        Location location = e.getLocation();
                        String message = String.format("Target activated when already activated: %.2f, %.2f, %.2f", location.getX(), location.getY(), location.getZ());
                        Bukkit.getLogger().warning(message);
                    }
                }, time);
                BukkitTask ticking = Bukkit.getScheduler().runTaskTimer(plugin, () -> BUTTON_LOCATION.getWorld().playSound(BUTTON_LOCATION, Sound.BLOCK_NOTE_BLOCK_HAT, 0.6f, 0), 0, 20);
                Bukkit.getScheduler().runTaskLater(plugin, ticking::cancel, Math.round((TOTAL_TIME + pointTime(7, 1) - 1) * 20));
                Bukkit.getScheduler().runTaskLater(plugin, this::timeUp, Math.round((TOTAL_TIME + pointTime(7, 1)) * 20));
            }
        });


    }

    /**
     * Deactivate the gallery game.
     */
    public void deactivate() {
        for (GalleryTarget target : targets) {
            target.deactivate();
        }
        active = false;
        points = 0;
        shooters.clear();
    }

    /**
     * Handle the time up event.
     */
    private void timeUp() {
        int finalPoints = getPoints();
        Audience audience = getShooters();
        audience.sendMessage(TextUtil.formatText("Final Score: ")
                .append(Component.text(String.format("%d", finalPoints)).color(NamedTextColor.WHITE)));

        BUTTON_LOCATION.getWorld().playSound(BUTTON_LOCATION, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
        deactivate();
    }

    /**
     * Handle player interaction with the gallery button.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onPushButton(final PlayerInteractEvent event) {
        EquipmentSlot hand = event.getHand();
        if (hand != null && !hand.equals(EquipmentSlot.HAND))
            return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        Block block = event.getClickedBlock();
        if (block != null && block.getLocation().equals(BUTTON_LOCATION.toBlockLocation())) {
            try {
                activate();
            } catch (GalleryAlreadyActiveException e) {
                Bukkit.getLogger().warning(e.getMessage());
                event.getPlayer().sendMessage(CommandUtils.errorMessage("Gallery is already running!"));
                shooters.add(event.getPlayer());
            }
        }
    }

    /**
     * Handle plugin disable event by deactivating the gallery.
     *
     * @param event The PluginDisableEvent.
     */
    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        deactivate();
    }

}
