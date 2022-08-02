package io.github.idkahn.towerchallenge.Towering;

import io.github.idkahn.towerchallenge.TeamColors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

public class TowerListener implements Listener {

    private ArrayList<TowerBlock>[] blocks;
    private boolean towering;

    public TowerListener() {
        this.towering = false;
        this.blocks = new ArrayList[TeamColors.values().length];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new ArrayList<TowerBlock>();
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {

        Block placedBlock = event.getBlockPlaced();
        BlockState placedBlockState = placedBlock.getState();

        if (placedBlockState instanceof ShulkerBox) {
            event.getPlayer().sendMessage("Shulker Box!");
            ShulkerBox shulkerBox = (ShulkerBox) placedBlockState;

            if (shulkerBox.customName() != null) {
                event.getPlayer().sendMessage(shulkerBox.customName());
            }
        }

        if (towering) {

            for (ArrayList<TowerBlock> array : blocks) {
                for (TowerBlock block : array) {
                    if (placedBlock.getType() == block.getBlockType()) {
                        event.setCancelled(true);

                        TextComponent text = Component.text("You have already placed ").
                                append(Component.text(placedBlock.getType().name(), NamedTextColor.RED))
                                .append(Component.text("!"));

                        event.getPlayer().sendActionBar(text);
//                        event.getPlayer().sendActionBar("You have already placed that block!");
                        return;
                    }
                }
            }

            blocks[0].add(new TowerBlock(event.getBlockPlaced()));
            event.getPlayer().sendMessage("Blocks Placed: " + blocks[0].size());
        }


    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {

        if (towering) {
            for (ArrayList<TowerBlock> list : blocks) {
                Iterator<TowerBlock> listIterator = list.iterator();
                while (listIterator.hasNext()) {
                    TowerBlock block = listIterator.next();

                    Location blockLocation = block.getLocation();

                    if (blockLocation.getX() == event.getBlock().getX() &&
                            blockLocation.getY() == event.getBlock().getY() &&
                            blockLocation.getZ() == event.getBlock().getZ()) {

    //                    event.getPlayer().sendMessage(event.getBlock().getType().name());
                        listIterator.remove();
                        event.getPlayer().sendMessage("Blocks Placed: " + blocks[0].size());
                        return;
                    }

                }
            }
        }

    }

    public void enableTower() {
        towering = true;
    }

    public void disableTower() {
        towering = false;
    }

    public void removeBlocks() {
        for (ArrayList<TowerBlock> list : blocks) {
            Iterator<TowerBlock> listIterator = list.iterator();
            while (listIterator.hasNext()) {
                TowerBlock block = listIterator.next();
                block.getBlock().setType(Material.AIR);
                listIterator.remove();
            }
        }
    }

}
