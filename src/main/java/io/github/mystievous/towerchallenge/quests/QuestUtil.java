package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.utility.BlockSets;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class QuestUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static ItemStack randomBlockBundle() {
        ItemStack bundle = new ItemStack(Material.BUNDLE);
        BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();

        Map<Material, Integer> chosenBlocks = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            Material block = BlockSets.OBTAINABLE_BLOCKS[RANDOM.nextInt(BlockSets.OBTAINABLE_BLOCKS.length)];
            int amount = chosenBlocks.getOrDefault(block, 0);
            chosenBlocks.put(block, amount+1);
        }

        for (Map.Entry<Material, Integer> entry : chosenBlocks.entrySet()) {
            bundleMeta.addItem(new ItemStack(entry.getKey(), entry.getValue()));
        }

        bundle.setItemMeta(bundleMeta);

        return bundle;
    }

}
