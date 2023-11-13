package io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.quests;

import io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.gallery.GalleryTarget;
import io.github.mctowerchallenge.mctcplugin.quest.instance.QuestInstance;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the Simon Says puzzle quest for the Jun 2023 event.
 */
public class SimonSays implements Listener {

    /**
     * Enum representing different Simon Says options along with their display, input, and sound.
     */
    public enum Option {
        KICK(new Location(Worlds.Jun2023_quest(), 7, 66, 25),
                new Location(Worlds.Jun2023_quest(), 0, 66, 25),
                Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.basedrum"), Sound.Source.RECORD, 1f, 0.793701f)),
        SNARE(new Location(Worlds.Jun2023_quest(), 7, 67, 26),
                new Location(Worlds.Jun2023_quest(), 1, 66, 26),
                Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.snare"), Sound.Source.RECORD, 1f, 0.594604f)),
        HAT(new Location(Worlds.Jun2023_quest(), 7, 67, 28),
                new Location(Worlds.Jun2023_quest(), 1, 66, 28),
                Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.hat"), Sound.Source.RECORD, 1f, 0.529732f)),
        COWBELL(new Location(Worlds.Jun2023_quest(), 7, 66, 29),
                new Location(Worlds.Jun2023_quest(), 0, 66, 29),
                Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.cow_bell"), Sound.Source.RECORD, 1f, 0.793701f));

        public static @Nullable Option getInputOption(@NotNull QuestInstance instance, @NotNull Location location) {
            for (Option option : values()) {
                if (location.equals(option.getInput(instance))) {
                    return option;
                }
            }
            return null;
        }

        private final Location display;
        private final Location input;
        private final Sound sound;
        private final Map<Integer, BukkitTask> inputTasks;
        private final Map<Integer, BukkitTask> displayTasks;

        Option(Location display, Location input, Sound sound) {
            this.display = display;
            this.input = input;
            this.sound = sound;
            this.inputTasks = new HashMap<>();
            this.displayTasks = new HashMap<>();
        }

        public Location getDisplay(QuestInstance instance) {
            return instance.offsetLocation(display);
        }

        public Location getInput(QuestInstance instance) {
            return instance.offsetLocation(input);
        }

        public void errorInput(Plugin plugin, QuestInstance instance) {
            Location instanceInput = getInput(instance);
            GalleryTarget.errorNoise(Bukkit.getServer(), instanceInput);
            Block block = instanceInput.getBlock();
            BlockData redstone = Bukkit.createBlockData(Material.REDSTONE_BLOCK);
            block.setBlockData(redstone);
            int teamDatabaseId = instance.getTeam().getDatabaseId();
            BukkitTask currentTask = inputTasks.get(teamDatabaseId);
            if (currentTask != null && !currentTask.isCancelled()) {
                currentTask.cancel();
            }
            inputTasks.put(teamDatabaseId, Bukkit.getScheduler().runTaskLater(plugin, () -> {
                BlockData lamp = Bukkit.createBlockData(Material.REDSTONE_LAMP);
                block.setBlockData(lamp);
            }, 10));
        }

        public void playInput(Plugin plugin, QuestInstance instance) {
            Location instanceInput = getInput(instance);
            instanceInput.getWorld().playSound(sound, instanceInput.getX(), instanceInput.getY(), instanceInput.getZ());
            Block block = instanceInput.getBlock();
            Lightable lamp = (Lightable) Bukkit.createBlockData(Material.REDSTONE_LAMP);
            lamp.setLit(true);
            block.setBlockData(lamp);
            int teamDatabaseId = instance.getTeam().getDatabaseId();
            BukkitTask currentTask = inputTasks.get(teamDatabaseId);
            if (currentTask != null && !currentTask.isCancelled()) {
                currentTask.cancel();
            }
            inputTasks.put(teamDatabaseId, Bukkit.getScheduler().runTaskLater(plugin, () -> {
                lamp.setLit(false);
                block.setBlockData(lamp);
            }, 10));
        }

        public void playDisplay(Plugin plugin, QuestInstance instance) {
            Location instanceDisplay = getDisplay(instance);
            Block block = instanceDisplay.getBlock();
            block.setType(Material.REDSTONE_BLOCK);
            int teamDatabaseId = instance.getTeam().getDatabaseId();
            BukkitTask currentTask = displayTasks.get(teamDatabaseId);
            if (currentTask != null && !currentTask.isCancelled()) {
                currentTask.cancel();
            }
            displayTasks.put(teamDatabaseId, Bukkit.getScheduler().runTaskLater(plugin, () -> {
                block.setType(Material.AIR);
            }, 10));
        }
    }

    /**
     * The solution to the Simon Says puzzle.
     */
    public static final Option[] SOLUTION = {
            Option.KICK,
            Option.HAT,
            Option.SNARE,
            Option.HAT,
            Option.COWBELL,
            Option.KICK
    };

    private final Plugin plugin;
    private final Jun2023QuestInstance instance;

    private boolean playing;
    private int currentStage;
    private int nextToPress;
    private boolean complete;

    private BukkitTask playTask;

    private final Door door;

    /**
     * Constructs a new SimonSays instance.
     *
     * @param plugin   The TowerChallenge plugin instance.
     * @param instance The instance of the Jun2023QuestInstance associated with this puzzle.
     */
    public SimonSays(Plugin plugin, Jun2023QuestInstance instance) {
        this.plugin = plugin;
        this.instance = instance;

        this.door = new Door(instance, new Vector(6, 0, 27));

        reset();

        play(currentStage, null, true);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Checks if the Simon Says puzzle has been completed.
     *
     * @return True if the puzzle is completed, false otherwise.
     */
    public boolean isCompleted() {
        return complete;
    }

    /**
     * Completes the room by opening the door and resetting the next one (Minesweeper).
     */
    public void completeRoom() {
        complete = true;

        instance.resetMineSweeper();

        door.open(null);

        if (playTask != null && !playTask.isCancelled()) {
            playTask.cancel();
        }

    }

    /**
     * Resets the Simon Says puzzle.
     */
    public void reset() {
        currentStage = 1;
        nextToPress = 0;
        complete = false;

        door.reset(null);

        play(currentStage, null, true);
    }

    /**
     * The number of ticks for each light to stay on in the Simon Says display.
     */
    public static final Long TICKS_PER_LIGHT = 12L;

    /**
     * Plays the Simon Says puzzle.
     *
     * @param stagesToPlay The number of stages to play.
     * @param callback     The callback to run after playing.
     * @param repeat       Whether to repeat the puzzle.
     */
    public void play(int stagesToPlay, Runnable callback, boolean repeat) {
        if (!playing) {
            playing = true;
            int finalIndex = Math.min(stagesToPlay, SOLUTION.length);
            for (int i = 0; i < finalIndex; i++) {
                Option option = SOLUTION[i];
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (instance.noteblocksCompleted()) {
                        option.playDisplay(plugin, instance);
                    }
                }, TICKS_PER_LIGHT * i);
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playing = false;
                if (callback != null) {
                    callback.run();
                }
            }, TICKS_PER_LIGHT * finalIndex);
            if (repeat && !complete) {
                if (playTask != null && !playTask.isCancelled()) {
                    playTask.cancel();
                }
                playTask = Bukkit.getScheduler().runTaskLater(plugin, () -> play(currentStage, null, true), TICKS_PER_LIGHT * finalIndex + TICKS_PER_LIGHT * 6);
            }
        }
    }

    /**
     * Handles the PlayerInteractEvent to detect player interactions with the Simon Says puzzle.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (Objects.equals(event.getHand(), EquipmentSlot.HAND) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block != null) {
            Location location = block.getLocation();
            Option option = Option.getInputOption(instance, location);
            if (option != null) {
                if (complete || option.equals(SOLUTION[nextToPress])) {
                    option.playInput(plugin, instance);
                    if (!complete) {
                        nextToPress++;
                        Location instanceInput = option.getInput(instance);
                        if (nextToPress == currentStage) {
                            instanceInput.getWorld().playSound(instanceInput, org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
                            nextToPress = 0;
                            currentStage++;
                            if (currentStage - 1 == SOLUTION.length) {
                                completeRoom();
                            }
                        }
                    }
                } else {
                    option.errorInput(plugin, instance);
                    reset();
                }
            }
        }
    }

}
