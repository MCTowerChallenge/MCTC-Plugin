package io.github.mystievous.towerchallenge.decoration.customblock;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.TargetListGui;
import io.github.mystievous.towerchallenge.gui.Icons;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.*;

public class CustomBlockManager implements Listener, Openable {

    public static final String CUSTOM_BLOCK_TAG = "custom-block";

    private static final DyeColor[] dyeColors = {
            DyeColor.RED,
            DyeColor.ORANGE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.GREEN,
            DyeColor.CYAN,
            DyeColor.LIGHT_BLUE,
            DyeColor.BLUE,
            DyeColor.PURPLE,
            DyeColor.MAGENTA,
            DyeColor.PINK,
            DyeColor.WHITE,
            DyeColor.LIGHT_GRAY,
            DyeColor.GRAY,
            DyeColor.BLACK,
            DyeColor.BROWN
    };

    private final Plugin plugin;

    private final Map<String, CustomBlock> blocks;

    public CustomBlockManager(Plugin plugin) {

        this.plugin = plugin;

        blocks = new HashMap<>();

        for (int i = 0; i < dyeColors.length; i++) {
            DyeColor dyeColor = dyeColors[i];
            int cryingObsidianId = (i + 1) * 2;
            int obsidianId = cryingObsidianId - 1;

            StringBuilder name = new StringBuilder();
            for (String word : dyeColor.name().split("_")) {
                name.append(word.charAt(0)).append(word.substring(1).toLowerCase());
            }

            String obsidianName = name + " Obsidian";
            ItemStack obsidian = GuiUtil.formatItem(obsidianName, Material.CRYING_OBSIDIAN, obsidianId);
            NBTUtils.setString(plugin, CUSTOM_BLOCK_TAG, obsidian, obsidianName);
            blocks.put(obsidianName, new CustomBlock(obsidian));

            String cryingObsidianName = name + " Crying Obsidian";
            ItemStack cryingObsidian = GuiUtil.formatItem(cryingObsidianName, Material.CRYING_OBSIDIAN, cryingObsidianId);
            NBTUtils.setString(plugin, CUSTOM_BLOCK_TAG, cryingObsidian, cryingObsidianName);
            blocks.put(cryingObsidianName, new CustomBlock(cryingObsidian));

        }

        String witherSkullName = "Wither Skull";
        ItemStack witherSkull = GuiUtil.formatItem(witherSkullName, Material.WITHER_SKELETON_SKULL, 0);
        NBTUtils.setString(plugin, CUSTOM_BLOCK_TAG, witherSkull, witherSkullName);
        CustomBlock witherCustomBlock = new CustomBlock(witherSkull);
        witherCustomBlock.setScale(new Vector3f(2, 2 , 2));
        witherCustomBlock.setTranslation(new Vector3f(0, 0.5f, 0));
        blocks.put(witherSkullName, witherCustomBlock);

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public Gui getGui(Player player) {
        Component guiName = Component.text("Custom Blocks:");
        return new TargetListGui<>(plugin, guiName,
                CustomBlock::getModel, blocks.values().stream().toList(),
                (clickingPlayer, customBlock) -> clickingPlayer.getInventory().addItem(customBlock.getModel()),
                new ButtonElement(Icons.exitItem(), HumanEntity::closeInventory)
        );
    }

    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        String customBlockName = NBTUtils.getString(plugin, CUSTOM_BLOCK_TAG, item);
        if (customBlockName == null) {
            return;
        }

        CustomBlock customBlock = blocks.get(customBlockName);
        if (customBlock != null) {
            BlockFace facing = event.getPlayer().getFacing();
            customBlock.placeBlock(event.getBlock().getLocation().setDirection(facing.getDirection()));
        }
    }

    @EventHandler
    public void onBreakBlock(final BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.BARRIER)) {
            return;
        }

        Collection<Entity> entities = block.getLocation().add(0.5, 0.5, 0.5).getNearbyEntities(0.5, 0.5, 0.5);
        for (Entity entity : entities) {
            if (entity.getScoreboardTags().contains(CUSTOM_BLOCK_TAG)) {
                entity.remove();
            }
        }
    }

}
