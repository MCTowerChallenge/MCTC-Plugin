package io.github.mystievous.towerchallenge.eventspecific.feb2023;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.utility.BlockSets;
import io.github.mystievous.mysticore.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Candle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Range;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ValentinesUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final String GALLERY_KEY = "Bow-wielder's Key";
    public static final String GALLERY_TAG = "shooting_gallery";

    public static final String MAZE_KEY = "Historian's Key";
    public static final String MAZE_TAG = "tower_maze";

    public static final String OCEAN_KEY = "Mermaid's Grove Key";
    public static final String OCEAN_TAG = "mermaids_grove";

    public static final String HORSESHOE_NAME = "Diamond Horseshoe";
    public static final String HORSESHOE_TAG = "diamond_horseshoe";

    public static final ItemStack galleryKey = NBTUtils.setNoUse(TowerChallenge.getInstance(), NBTUtils.setBool(TowerChallenge.getInstance(), GALLERY_TAG, new ItemStack(Material.SPECTRAL_ARROW) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(TextUtil.noItalic(GALLERY_KEY));
        meta.setCustomModelData(1);
        TextUtil.appendQuestItemLore(meta);
        setItemMeta(meta);
    }}));

    public static final ItemStack mazeKey = NBTUtils.setNoUse(TowerChallenge.getInstance(), NBTUtils.setBool(TowerChallenge.getInstance(), MAZE_TAG, new ItemStack(Material.FEATHER) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(TextUtil.noItalic(MAZE_KEY));
        meta.setCustomModelData(1);
        TextUtil.appendQuestItemLore(meta);
        setItemMeta(meta);
    }}));

    public static final ItemStack oceanKey = NBTUtils.setNoUse(TowerChallenge.getInstance(), NBTUtils.setBool(TowerChallenge.getInstance(), OCEAN_TAG, new ItemStack(Material.HEART_OF_THE_SEA) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(TextUtil.noItalic(OCEAN_KEY));
        meta.setCustomModelData(12);
        TextUtil.appendQuestItemLore(meta);
        setItemMeta(meta);
    }}));

    public static final ItemStack diamondHorseshoe = NBTUtils.setNoUse(TowerChallenge.getInstance(), NBTUtils.setBool(TowerChallenge.getInstance(), HORSESHOE_TAG, new ItemStack(Material.GOLD_INGOT) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(TextUtil.noItalic(HORSESHOE_NAME));
        meta.setCustomModelData(1);
        TextUtil.appendQuestItemLore(meta);
        setItemMeta(meta);
    }}));

    public static final Map<Integer, Integer> fragmentMap = new HashMap<>() {{
        put(1, 3);
        put(2, 4);
        put(3, 5);
        put(4, 6);
        put(5, 7);
        put(6, 8);
        put(7, 9);
        put(8, 10);
        put(9, 11);
    }};

    public static final String[] oceanKeyFragmentTags = new String[]{
            "mermaids_grove-fragment_1",
            "mermaids_grove-fragment_2",
            "mermaids_grove-fragment_3",
            "mermaids_grove-fragment_4",
            "mermaids_grove-fragment_5",
            "mermaids_grove-fragment_6",
            "mermaids_grove-fragment_7",
            "mermaids_grove-fragment_8",
            "mermaids_grove-fragment_9"
    };

    public static ItemStack oceanKeyFragment(@Range(from = 1, to = 9) int number) {
        return NBTUtils.setNoUse(TowerChallenge.getInstance(), NBTUtils.setBool(TowerChallenge.getInstance(), oceanKeyFragmentTags[number - 1], new ItemStack(Material.HEART_OF_THE_SEA) {{
            ItemMeta meta = getItemMeta();
            meta.displayName(TextUtil.noItalic(String.format("%s Fragment %d", OCEAN_KEY, number)));
            meta.setCustomModelData(fragmentMap.get(number));
            TextUtil.appendQuestItemLore(meta);
            setItemMeta(meta);
        }}));
    }

    public static final ItemStack[] oceanKeyFragments = new ItemStack[]{
            oceanKeyFragment(1),
            oceanKeyFragment(2),
            oceanKeyFragment(3),
            oceanKeyFragment(4),
            oceanKeyFragment(5),
            oceanKeyFragment(6),
            oceanKeyFragment(7),
            oceanKeyFragment(8),
            oceanKeyFragment(9)
    };

    public static final ItemStack[] bundles = {
            new ItemStack(Material.BUNDLE) {{
                BundleMeta bundleMeta = (BundleMeta) getItemMeta();
                bundleMeta.setCustomModelData(4);
                bundleMeta.displayName(TextUtil.noItalic("Random Shell"));
                setItemMeta(bundleMeta);
            }},
            new ItemStack(Material.BUNDLE) {{
                BundleMeta bundleMeta = (BundleMeta) getItemMeta();
                bundleMeta.setCustomModelData(5);
                bundleMeta.displayName(TextUtil.noItalic("Young Merperson’s Bag"));
                setItemMeta(bundleMeta);
            }}
    };

    public static final Material[] dyes = {
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,
            Material.BROWN_DYE,

            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,
            Material.GREEN_DYE,

            Material.RED_DYE,
            Material.ORANGE_DYE,
            Material.YELLOW_DYE,
            Material.LIME_DYE,
            Material.CYAN_DYE,
            Material.LIGHT_BLUE_DYE,
            Material.BLUE_DYE,
            Material.PURPLE_DYE,
            Material.MAGENTA_DYE,
            Material.PINK_DYE,
            Material.WHITE_DYE,
            Material.LIGHT_GRAY_DYE,
            Material.GRAY_DYE,
            Material.BLACK_DYE
    };

    public static ItemStack randomDyeBundle() {
        ItemStack bundle = bundles[RANDOM.nextInt(bundles.length)].clone();
        BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();

        Map<Material, Integer> chosenDyes = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            Material dye = dyes[RANDOM.nextInt(dyes.length)];
            int amount = chosenDyes.getOrDefault(dye, 0);
            chosenDyes.put(dye, amount+1);
        }

        for (Map.Entry<Material, Integer> entry : chosenDyes.entrySet()) {
            bundleMeta.addItem(new ItemStack(entry.getKey(), entry.getValue()));
        }

        bundle.setItemMeta(bundleMeta);

        return bundle;
    }

    public static final ItemStack oceanExploreMap = ItemStack.deserializeBytes(Base64.getDecoder().decode("H4sIAAAAAAAA/0WPTYrCQBCFX9uOEzMuPUFcDojrrARn6yzdamEq0tA/oVOCOgheRU/i0exJFtaq6oPHVy8HNL5+SGjDsTXBA5NFhoGpMHXG8z5SLWVtrOVq66jJoYUOOT4r0zaWzhmGv+QYs79CIvnWknBRFu/A3AV/dOyluGpka2pWwYaIMjLGScz7EEmSt80BKIWhnBsej6BOy/sO/9N/o74Tuzwft46NoGOQZd1fqYJOqm5V+FiFoxeFF9hs"));

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

    public static final Vector[][] entranceBlocks = new Vector[][]{
            {new Vector(42, 60, -2149), new Vector(42, 60, -2146)},
            {new Vector(42, 61, -2150), new Vector(42, 61, -2144)},
            {new Vector(42, 62, -2150), new Vector(42, 62, -2143)},
            {new Vector(42, 63, -2150), new Vector(42, 63, -2148)},
            {new Vector(41, 63, -2147), new Vector(41, 63, -2146)},
            {new Vector(41, 64, -2147), new Vector(41, 64, -2147)},
            {new Vector(41, 64, -2145), new Vector(41, 64, -2145)},
            {new Vector(42, 64, -2149), new Vector(42, 64, -2148)},
            {new Vector(42, 64, -2146), new Vector(42, 64, -2146)},
            {new Vector(42, 64, -2144), new Vector(42, 64, -2143)},
            {new Vector(42, 65, -2149), new Vector(42, 65, -2144)},
            {new Vector(42, 63, -2145), new Vector(42, 63, -2143)},
    };

    public static final Location candleLoc = new Location(Worlds.Feb2023(), 42, 63, -2147);

    public static void openTowerArea() {
        candleLoc.getBlock().setType(Material.AIR);
        for (Vector[] layer : entranceBlocks) {
            for (int x = layer[0].getBlockX(); x <= layer[1].getBlockX(); x++) {
                for (int y = layer[0].getBlockY(); y <= layer[1].getBlockY(); y++) {
                    for (int z = layer[0].getBlockZ(); z <= layer[1].getBlockZ(); z++) {
                        Location location = new Location(Worlds.Feb2023(), x, y, z);
                        Block block = location.getBlock();
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public static void closeTowerArea() {
        candleLoc.getBlock().setType(Material.CANDLE);
        Block candleBlock = candleLoc.getBlock();
        if (candleBlock.getBlockData() instanceof Candle candle) {
            candle.setCandles(3);
            candle.setLit(true);
            candle.setWaterlogged(false);
            candleBlock.setBlockData(candle);
        }
        for (Vector[] layer : entranceBlocks) {
            for (int x = layer[0].getBlockX(); x <= layer[1].getBlockX(); x++) {
                for (int y = layer[0].getBlockY(); y <= layer[1].getBlockY(); y++) {
                    for (int z = layer[0].getBlockZ(); z <= layer[1].getBlockZ(); z++) {
                        Location location = new Location(Worlds.Feb2023(), x, y, z);
                        Block block = location.getBlock();
                        block.setType(Material.STONE);
                    }
                }
            }
        }
    }
}
