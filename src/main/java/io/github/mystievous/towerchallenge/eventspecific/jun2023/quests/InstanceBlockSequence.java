package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.towerchallenge.quests.instances.QuestInstance;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class InstanceBlockSequence extends BlockSequence {

    private final QuestInstance instance;

    public InstanceBlockSequence(long delay, Map<Location, BlockData> blocks, QuestInstance instance) {
        super(delay, blocks);
        this.instance = instance;
    }

    public InstanceBlockSequence(long delay, QuestInstance instance) {
        this(delay, new HashMap<>(), instance);
    }

    @Override
    public void placeBlocks() {
        for (Map.Entry<Location, BlockData> entry : getBlocks().entrySet()) {
            instance.offsetLocation(entry.getKey()).getBlock().setBlockData(entry.getValue());
        }
    }

}
