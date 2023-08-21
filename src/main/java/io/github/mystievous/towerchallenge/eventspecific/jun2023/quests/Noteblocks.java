package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.towerchallenge.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the note block puzzle quest for the Jun 2023 event.
 */
public class Noteblocks implements Listener {

    /**
     * The location where the redstone signal is checked to solve the puzzle.
     */
    private static final Location CHECK_TRIGGER_LOCATION = new Location(Worlds.Jun2023_quest(), -5, 63, -1);

    /**
     * The base solution map for the note block puzzle, mapping locations to note values.
     */
    private static final Map<Location, Note> BASE_SOLUTION = new HashMap<>() {{
        put(new Location(Worlds.Jun2023_quest(), 1, 64, 0), new Note(8));
        put(new Location(Worlds.Jun2023_quest(), 1, 64, 1), new Note(15));
        put(new Location(Worlds.Jun2023_quest(), 1, 64, 2), new Note(13));
        put(new Location(Worlds.Jun2023_quest(), 1, 64, 3), new Note(18));
        put(new Location(Worlds.Jun2023_quest(), 1, 64, 4), new Note(15));
        put(new Location(Worlds.Jun2023_quest(), 1, 64, 5), new Note(11));
        put(new Location(Worlds.Jun2023_quest(), 1, 64, 6), new Note(13));
        put(new Location(Worlds.Jun2023_quest(), 0, 64, 6), new Note(20));
        put(new Location(Worlds.Jun2023_quest(), -4, 64, 6), new Note(18));
        put(new Location(Worlds.Jun2023_quest(), -5, 64, 6), new Note(15));
        put(new Location(Worlds.Jun2023_quest(), -5, 64, 5), new Note(13));
        put(new Location(Worlds.Jun2023_quest(), -5, 64, 4), new Note(15));
        put(new Location(Worlds.Jun2023_quest(), -5, 64, 3), new Note(11));
        put(new Location(Worlds.Jun2023_quest(), -5, 64, 2), new Note(13));
        put(new Location(Worlds.Jun2023_quest(), -5, 64, 1), new Note(18));
        put(new Location(Worlds.Jun2023_quest(), -5, 64, 0), new Note(20));
    }};

    private final Jun2023QuestInstance instance;

    private final Door door;

    /**
     * Constructs a new Noteblocks instance.
     *
     * @param plugin   The TowerChallenge plugin instance.
     * @param instance The instance of the Jun2023QuestInstance associated with this puzzle.
     */
    public Noteblocks(Plugin plugin, Jun2023QuestInstance instance) {

        this.instance = instance;

        this.door = new Door(instance, new Vector(0, 0, 0));

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    /**
     * Checks if the note block puzzle has been completed.
     *
     * @return True if the door is open and the puzzle is completed, false otherwise.
     */
    public boolean isCompleted() {
        return door.isOpen();
    }

    /**
     * Checks the solution of the note block puzzle and opens the door if the solution is correct.
     */
    private void checkSolution() {
        boolean incorrect = false;
        for (Map.Entry<Location, Note> entry : BASE_SOLUTION.entrySet()) {
            Location instanceLocation = instance.offsetLocation(entry.getKey());
            Block block = instanceLocation.getBlock();
            if (block.getBlockData() instanceof NoteBlock noteBlock) {
                if (!noteBlock.getNote().equals(entry.getValue())) {
                    incorrect = true;
                    break;
                }
            }
        }

        if (!incorrect) {
            openDoor();
        }
    }

    /**
     * Opens the door and resets the associated Simon Says game/next room.
     */
    public void openDoor() {
        if (!door.isOpen()) {
            door.open(null);
            instance.resetSimonSays();
        }
    }

    /**
     * Closes the door.
     */
    public void closeDoor() {
        door.reset(null);
    }

    /**
     * Handles the BlockRedstoneEvent to trigger the solution check when the redstone signal changes.
     *
     * @param event The BlockRedstoneEvent.
     */
    @EventHandler
    public void onBlockPower(final BlockRedstoneEvent event) {

        Block block = event.getBlock();
        int newCurrent = event.getNewCurrent();

        if (block.getLocation().equals(instance.offsetLocation(CHECK_TRIGGER_LOCATION.toBlockLocation())) && newCurrent == 15) {
            checkSolution();
        }

    }

}
