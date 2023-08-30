package io.github.mystievous.towerchallenge.decoration;

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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private final List<ItemStack> blocks;

    public CustomBlockManager(Plugin plugin) {

        this.plugin = plugin;

        blocks = new ArrayList<>();

        for (int i = 0; i < dyeColors.length; i++) {
            DyeColor dyeColor = dyeColors[i];
            int cryingObsidianId = (i + 1) * 2;
            int obsidianId = cryingObsidianId - 1;

            StringBuilder name = new StringBuilder();
            for (String word : dyeColor.name().split("_")) {
                name.append(word.charAt(0)).append(word.substring(1).toLowerCase());
            }

            ItemStack obsidian = GuiUtil.formatItem(name + " Obsidian", Material.CRYING_OBSIDIAN, obsidianId);
            NBTUtils.setBool(plugin, CUSTOM_BLOCK_TAG, obsidian);

            ItemStack cryingObsidian = GuiUtil.formatItem(name + " Crying Obsidian", Material.CRYING_OBSIDIAN, cryingObsidianId);
            NBTUtils.setBool(plugin, CUSTOM_BLOCK_TAG, cryingObsidian);

            blocks.add(obsidian);
            blocks.add(cryingObsidian);

        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public Gui getGui(Player player) {
        Component guiName = Component.text("Custom Blocks:");
        return new TargetListGui<>(plugin, guiName,
                itemStack -> itemStack, blocks,
                (clickingPlayer, itemStack) -> clickingPlayer.getInventory().addItem(itemStack),
                new ButtonElement(Icons.exitItem(), HumanEntity::closeInventory)
        );
    }

    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!item.getType().equals(Material.CRYING_OBSIDIAN) || !NBTUtils.boolState(plugin, CUSTOM_BLOCK_TAG, item)) {
            return;
        }

        Block placedBlock = event.getBlockPlaced();
        placedBlock.setType(Material.BARRIER);
        Location customLocation = placedBlock.getLocation().add(0.5, 0.5, 0.5);
        ItemDisplay itemDisplay = (ItemDisplay) customLocation.getWorld().spawnEntity(customLocation, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(item);
        itemDisplay.addScoreboardTag(CUSTOM_BLOCK_TAG);
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
