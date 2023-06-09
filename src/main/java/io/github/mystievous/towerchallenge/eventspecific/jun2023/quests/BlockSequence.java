package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A sequence of blocks, for "animation",
 * or redstone contraptions.
 */
public class BlockSequence {

    private BlockSequence next;
    private final long delay;
    private final Map<Location, BlockData> blocks;

    /**
     * Creates a new block sequence stage.
     *
     * @param delay  The time in ticks to wait *after*
     *               this stage to play the next one.
     * @param blocks The blocks to use in this stage.
     * @see #putBlock(Location, BlockData)
     */
    public BlockSequence(long delay, Map<Location, BlockData> blocks) {
        this.delay = delay;
        this.blocks = blocks;
    }

    /**
     * Creates a new block sequence stage.
     *
     * @param delay  The time in ticks to wait *after*
     *               this stage to play the next one.
     * @see #putBlock(Location, BlockData)
     */
    public BlockSequence(long delay) {
        this(delay, new HashMap<>());
    }

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

    public void putAll(@NotNull Map<Location, BlockData> blocks) {
        this.blocks.putAll(blocks);
    }

    /**
     * Sets the next stage in
     * this sequence.
     *
     * @param next The next set of blocks
     *             to go to.
     */
    public void setNext(BlockSequence next) {
        this.next = next;
    }

    /**
     * Places all blocks in this frame.
     */
    public void placeBlocks() {
        for (Map.Entry<Location, BlockData> entry : blocks.entrySet()) {
            entry.getKey().getBlock().setBlockData(entry.getValue());
        }
    }

    /**
     * Plays this block sequence and
     * all ones after it.
     *
     * @param callback A {@link Runnable} to
     *                 run after the dialogue
     *                 sequence is done.
     */
    public void play(Runnable callback) {
        placeBlocks();
        Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), () -> {
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
     * Plays this block sequence and
     * all ones after it.
     */
    public void play() {
        play(null);
    }

}
