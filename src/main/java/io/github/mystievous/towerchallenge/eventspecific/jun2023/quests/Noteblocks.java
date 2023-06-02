package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quests.instances.QuestInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Piston;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Noteblocks implements Listener {

    private static final Location CHECK_TRIGGER_LOCATION = new Location(Worlds.Jun2023_quest(), -5, 63, -1);

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

    private final Plugin plugin;
    private final QuestInstance instance;

    private final Door door;

    public Noteblocks(Plugin plugin, QuestInstance instance) {

        this.plugin = plugin;
        this.instance = instance;

        this.door = new Door(instance, new Vector(0, 0, 0));

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

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

    public void openDoor() {
        door.open(null);
    }

    public void closeDoor() {
        door.reset(null);
    }

    @EventHandler
    public void onBlockPower(final BlockRedstoneEvent event) {

        Block block = event.getBlock();
        int newCurrent = event.getNewCurrent();

        if (block.getLocation().equals(CHECK_TRIGGER_LOCATION.toBlockLocation()) && newCurrent == 15) {
            checkSolution();
        }

    }

}
