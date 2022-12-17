package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.gui.element.Element;
import org.bukkit.inventory.ItemStack;

public class QuestReward implements Representable {

    private final ItemStack representation;
    private final ItemStack rewardItem;

    public QuestReward(ItemStack representation, ItemStack rewardItem) {
        this.representation = representation;
        this.rewardItem = rewardItem;
    }

    public QuestReward(ItemStack rewardItem) {
        this(rewardItem, rewardItem);
    }

    public ItemStack getRewardItem() {
        return rewardItem;
    }

    @Override
    public Element getRepresentation() {
        return new Element(representation);
    }
}
