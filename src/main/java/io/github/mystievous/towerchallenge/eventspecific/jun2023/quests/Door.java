package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quest.instance.QuestInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.util.Vector;

import java.util.HashMap;

/**
 * Represents a door with opening and resetting mechanisms using sequences of blocks.
 * The door's state can be changed to open or reset using the provided methods.
 */
public class Door {

    private final BlockSequence open;
    private final BlockSequence reset;

    private boolean openState;

    /**
     * Creates a new instance of {@code Door} with opening and resetting sequences based on the given
     * {@link QuestInstance} and offset {@link Vector}.
     *
     * @param instance The associated {@link QuestInstance} for which the door operates.
     * @param offset   The offset {@link Vector} to adjust the door's position.
     */
    public Door(QuestInstance instance, Vector offset) {

        Piston northPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        northPiston.setFacing(BlockFace.NORTH);

        Piston upPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        upPiston.setFacing(BlockFace.UP);

        Piston eastPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        eastPiston.setFacing(BlockFace.EAST);

        Piston westPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        westPiston.setFacing(BlockFace.WEST);

        Piston downPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        downPiston.setFacing(BlockFace.DOWN);

        BlockData redstoneBlock = Bukkit.createBlockData(Material.REDSTONE_BLOCK);

        BlockData glowstone = Bukkit.createBlockData(Material.GLOWSTONE);

        BlockData air = Bukkit.createBlockData(Material.AIR);

        open = new InstanceBlockSequence(4, instance);
        open.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -1, 66, 7).add(offset), glowstone);
            put(new Location(Worlds.Jun2023_quest(), -3, 66, 7).add(offset), glowstone);
            put(new Location(Worlds.Jun2023_quest(), -3, 63, 7).add(offset), upPiston);
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 7).add(offset), upPiston);
            put(new Location(Worlds.Jun2023_quest(), -1, 63, 7).add(offset), upPiston);
            put(new Location(Worlds.Jun2023_quest(), -3, 62, 7).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -2, 62, 7).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -1, 62, 7).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -3, 66, 9).add(offset), northPiston);
            put(new Location(Worlds.Jun2023_quest(), -2, 66, 9).add(offset), northPiston);
            put(new Location(Worlds.Jun2023_quest(), -1, 66, 9).add(offset), northPiston);
            put(new Location(Worlds.Jun2023_quest(), -3, 67, 9).add(offset), northPiston);
            put(new Location(Worlds.Jun2023_quest(), -2, 67, 9).add(offset), northPiston);
            put(new Location(Worlds.Jun2023_quest(), -1, 67, 9).add(offset), northPiston);
            put(new Location(Worlds.Jun2023_quest(), -3, 66, 10).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -2, 66, 10).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -1, 66, 10).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -3, 67, 10).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -2, 67, 10).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -1, 67, 10).add(offset), redstoneBlock);
        }});

        BlockSequence pullBack = new InstanceBlockSequence(4, instance);
        pullBack.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -3, 62, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 62, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 62, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 66, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 66, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 66, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 67, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 67, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 67, 10).add(offset), air);
        }});
        open.setNext(pullBack);

        BlockSequence setupSplit = new InstanceBlockSequence(4, instance);
        setupSplit.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -3, 63, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 63, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 66, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 66, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 66, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 67, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 67, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 67, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -5, 66, 8).add(offset), eastPiston);
            put(new Location(Worlds.Jun2023_quest(), -6, 66, 8).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), 1, 66, 8).add(offset), westPiston);
            put(new Location(Worlds.Jun2023_quest(), 2, 66, 8).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -2, 64, 8).add(offset), upPiston);
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 8).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -3, 69, 8).add(offset), downPiston);
            put(new Location(Worlds.Jun2023_quest(), -2, 69, 8).add(offset), downPiston);
            put(new Location(Worlds.Jun2023_quest(), -1, 69, 8).add(offset), downPiston);
            put(new Location(Worlds.Jun2023_quest(), -3, 69, 9).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -2, 69, 9).add(offset), redstoneBlock);
            put(new Location(Worlds.Jun2023_quest(), -1, 69, 9).add(offset), redstoneBlock);
        }});
        pullBack.setNext(setupSplit);

        BlockSequence split = new InstanceBlockSequence(4, instance);
        split.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -6, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), 2, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 69, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 69, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 69, 9).add(offset), air);
        }});
        setupSplit.setNext(split);

        BlockSequence clearSplit = new InstanceBlockSequence(4, instance);
        clearSplit.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -5, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), 1, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 64, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 69, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 69, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 69, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 8).add(offset), upPiston);
        }});
        split.setNext(clearSplit);

        BlockSequence setupFloor = new InstanceBlockSequence(4, instance);
        setupFloor.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -2, 62, 8).add(offset), redstoneBlock);
        }});

        BlockSequence floor = new InstanceBlockSequence(4, instance);
        floor.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -2, 62, 8).add(offset), air);
        }});
        clearSplit.setNext(floor);

        BlockSequence setupFlush = new InstanceBlockSequence(4, instance);
        setupFlush.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -2, 64, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 8).add(offset), upPiston);
            put(new Location(Worlds.Jun2023_quest(), -2, 62, 8).add(offset), redstoneBlock);
        }});
        floor.setNext(setupFlush);

        BlockSequence flush = new InstanceBlockSequence(4, instance);
        flush.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -2, 62, 8).add(offset), air);
        }});
        setupFlush.setNext(flush);

        BlockSequence clear = new InstanceBlockSequence(4, instance);
        clear.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 8).add(offset), air);
        }});
        flush.setNext(clear);

        BlockData cutSandstone = Bukkit.createBlockData(Material.CUT_SANDSTONE);
        BlockData redstoneLamp = Bukkit.createBlockData(Material.REDSTONE_LAMP);
        BlockData smoothSandstone = Bukkit.createBlockData(Material.SMOOTH_SANDSTONE);
        BlockData sandstone = Bukkit.createBlockData(Material.SANDSTONE);

        reset = new InstanceBlockSequence(0, instance);
        reset.putAll(new HashMap<>(){{
            put(new Location(Worlds.Jun2023_quest(), -3, 63, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 63, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 62, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 62, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 62, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 66, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 66, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 66, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 67, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 67, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 67, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 66, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 66, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 66, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 67, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 67, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 67, 10).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -5, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -6, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), 1, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), 2, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 63, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 69, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 69, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 69, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 69, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 69, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 69, 9).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 62, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 64, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), 0, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 64, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 64, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 64, 7).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -4, 66, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 68, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -2, 68, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -1, 68, 8).add(offset), air);
            put(new Location(Worlds.Jun2023_quest(), -3, 67, 7).add(offset), cutSandstone);
            put(new Location(Worlds.Jun2023_quest(), -2, 67, 7).add(offset), cutSandstone);
            put(new Location(Worlds.Jun2023_quest(), -1, 67, 7).add(offset), cutSandstone);
            put(new Location(Worlds.Jun2023_quest(), -3, 66, 7).add(offset), redstoneLamp);
            put(new Location(Worlds.Jun2023_quest(), -2, 66, 7).add(offset), smoothSandstone);
            put(new Location(Worlds.Jun2023_quest(), -1, 66, 7).add(offset), redstoneLamp);
            put(new Location(Worlds.Jun2023_quest(), -3, 65, 7).add(offset), sandstone);
            put(new Location(Worlds.Jun2023_quest(), -2, 65, 7).add(offset), sandstone);
            put(new Location(Worlds.Jun2023_quest(), -1, 65, 7).add(offset), sandstone);
        }});

        reset(null);

    }

    /**
     * Checks whether the door is in the open state.
     *
     * @return {@code true} if the door is open, {@code false} otherwise.
     */
    public boolean isOpen() {
        return openState;
    }

    /**
     * Opens the door using the associated opening sequence and executes the provided callback when done.
     *
     * @param callback A {@link Runnable} to run after the door opening sequence is complete.
     */
    public void open(Runnable callback) {
        openState = true;
        open.play(callback);
    }

    /**
     * Resets the door to its closed state using the associated resetting sequence
     * and executes the provided callback when done.
     *
     * @param callback A {@link Runnable} to run after the door resetting sequence is complete.
     */
    public void reset(Runnable callback) {
        openState = false;
        reset.play(callback);
    }

}
