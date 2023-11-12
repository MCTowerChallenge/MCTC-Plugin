package io.github.mctowerchallenge.towerchallenge.quest;

import io.github.mctowerchallenge.towerchallenge.utility.BlockSets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class QuestUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static ItemStack randomBlockBundle(int blocks) {
        ItemStack bundle = new ItemStack(Material.BUNDLE);
        BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();

        Map<Material, Integer> chosenBlocks = new HashMap<>();
        for (int i = 0; i < blocks; i++) {
            Material block = BlockSets.OBTAINABLE_BLOCKS[RANDOM.nextInt(BlockSets.OBTAINABLE_BLOCKS.length)];
            int amount = chosenBlocks.getOrDefault(block, 0);
            chosenBlocks.put(block, amount + 1);
        }

        for (Map.Entry<Material, Integer> entry : chosenBlocks.entrySet()) {
            bundleMeta.addItem(new ItemStack(entry.getKey(), entry.getValue()));
        }

        bundle.setItemMeta(bundleMeta);

        return bundle;
    }

    public static void fillArea(Location[] area, Material blockType) {
        fillArea(area, Bukkit.createBlockData(blockType));
    }

    public static void fillArea(Location[] area, BlockData blockData) {
        for (int x = area[0].getBlockX(); x <= area[1].getBlockX(); x++) {
            for (int y = area[0].getBlockY(); y <= area[1].getBlockY(); y++) {
                for (int z = area[0].getBlockZ(); z <= area[1].getBlockZ(); z++) {
                    Location location = new Location(area[0].getWorld(), x, y, z);
                    Block block = location.getBlock();
                    block.setBlockData(blockData);
                }
            }
        }
//        Location center = area[0].clone().add(area[1]).multiply(0.5);
//        center.getWorld().playSound(center, Sound.BLOCK_ANVIL_LAND, SoundCategory.RECORDS, 1f, 1f);
    }

}
