package io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.quests;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a sequence of blocks used for animations or redstone contraptions.
 */
public class BlockSequence {

    private BlockSequence next;
    private final long delay;
    private final Map<Location, BlockData> blocks;

    /**
     * Creates a new block sequence stage with the specified delay and blocks.
     *
     * @param delay  The time in ticks to wait *after* this stage to play the next one.
     * @param blocks The blocks to use in this stage.
     * @see #putBlock(Location, BlockData)
     */
    public BlockSequence(long delay, Map<Location, BlockData> blocks) {
        this.delay = delay;
        this.blocks = blocks;
    }

    /**
     * Creates a new block sequence stage with the specified delay.
     *
     * @param delay The time in ticks to wait *after* this stage to play the next one.
     * @see #putBlock(Location, BlockData)
     */
    public BlockSequence(long delay) {
        this(delay, new HashMap<>());
    }

    /**
     * Returns the map of blocks associated with this block sequence stage.
     *
     * @return The map of blocks.
     */
    public Map<Location, BlockData> getBlocks() {
        return blocks;
    }

    /**
     * Puts a block in this stage.
     *
     * @param location  The location for the block.
     * @param blockData The block data to use.
     */
    public void putBlock(Location location, BlockData blockData) {
        blocks.put(location, blockData);
    }

    /**
     * Puts multiple blocks into this stage.
     *
     * @param blocks The map of block locations and block data to add.
     */
    public void putAll(@NotNull Map<Location, BlockData> blocks) {
        this.blocks.putAll(blocks);
    }

    /**
     * Sets the next stage in this sequence.
     *
     * @param next The next set of blocks to go to.
     */
    public void setNext(BlockSequence next) {
        this.next = next;
    }

    /**
     * Places all blocks in this stage.
     */
    public void placeBlocks() {
        for (Map.Entry<Location, BlockData> entry : blocks.entrySet()) {
            entry.getKey().getBlock().setBlockData(entry.getValue());
        }
    }

    /**
     * Plays this block sequence and all subsequent stages.
     *
     * @param callback A {@link Runnable} to run after the sequence is completed.
     */
    public void play(Runnable callback) {
        placeBlocks();
        Bukkit.getScheduler().scheduleSyncDelayedTask(MCTCPlugin.getInstance(), () -> {
            if (next != null) {
                next.play(callback);
            } else {
                if (callback != null) {
                    callback.run();
                }
            }
        }, delay);
    }

    /**
     * Plays this block sequence and all subsequent stages.
     */
    public void play() {
        play(null);
    }

}
