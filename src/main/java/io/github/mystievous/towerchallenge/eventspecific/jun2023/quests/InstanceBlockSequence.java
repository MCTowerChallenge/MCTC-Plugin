package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.towerchallenge.quest.instance.QuestInstance;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a sequence of blocks used for animations or redstone contraptions,
 * with block placements being relative to a specific {@link QuestInstance}.
 */
public class InstanceBlockSequence extends BlockSequence {

    private final QuestInstance instance;

    /**
     * Creates a new instance of {@code InstanceBlockSequence} with the specified delay, blocks,
     * and associated {@link QuestInstance}.
     *
     * @param delay    The time in ticks to wait *after* this stage to play the next one.
     * @param blocks   The blocks to use in this stage.
     * @param instance The associated {@link QuestInstance} for which the blocks are placed.
     * @see #putBlock(Location, BlockData)
     */
    public InstanceBlockSequence(long delay, Map<Location, BlockData> blocks, QuestInstance instance) {
        super(delay, blocks);
        this.instance = instance;
    }

    /**
     * Creates a new instance of {@code InstanceBlockSequence} with the specified delay
     * and associated {@link QuestInstance}.
     *
     * @param delay    The time in ticks to wait *after* this stage to play the next one.
     * @param instance The associated {@link QuestInstance} for which the blocks are placed.
     * @see #putBlock(Location, BlockData)
     */
    public InstanceBlockSequence(long delay, QuestInstance instance) {
        this(delay, new HashMap<>(), instance);
    }

    /**
     * Overrides the {@code placeBlocks} method to place blocks using the offset locations
     * based on the associated {@link QuestInstance}.
     */
    @Override
    public void placeBlocks() {
        for (Map.Entry<Location, BlockData> entry : getBlocks().entrySet()) {
            instance.offsetLocation(entry.getKey()).getBlock().setBlockData(entry.getValue());
        }
    }

}
