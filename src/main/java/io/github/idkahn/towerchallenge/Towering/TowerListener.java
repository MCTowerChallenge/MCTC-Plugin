package io.github.idkahn.towerchallenge.Towering;

import io.github.idkahn.towerchallenge.TeamColors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class TowerListener implements Listener {

    private ArrayList<BlockState>[] blocks;
    private boolean towering;

    public TowerListener() {
        this.towering = false;
        this.blocks = new ArrayList[TeamColors.values().length];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new ArrayList<BlockState>();
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        BlockState placedBlockState = event.getBlockPlaced().getState();

        if (towering) {

            if (placedBlockState instanceof ShulkerBox) {
//                event.getPlayer().sendMessage("Shulker Box!");
                ShulkerBox shulkerBox = (ShulkerBox) placedBlockState;

                if (shulkerBox.customName() != null) {
                    String shulkerBoxName = PlainTextComponentSerializer.plainText().serialize(shulkerBox.customName());
    //                player.sendMessage(shulkerBoxName);

                    if (shulkerBoxName.equals("Starting Shulker")) {
//                        player.sendMessage(shulkerBox.customName());
                        return;
                    }
                }
            }

            for (ArrayList<BlockState> array : blocks) {
                for (BlockState blockState : array) {
                    if (placedBlockState.getType() == blockState.getType()) {
                        event.setCancelled(true);

                        TextComponent text = Component.text("You have already placed ").
                                append(Component.text(placedBlockState.getType().name(), NamedTextColor.RED))
                                .append(Component.text("!"));

                        player.sendActionBar(text);
                        return;
                    }
                }
            }

            blocks[0].add(placedBlockState);
            player.sendMessage("Blocks Placed: " + blocks[0].size());
        }

    }



    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {

        Player player = event.getPlayer();
        BlockState brokenBlockState = event.getBlock().getState();

        if (towering) {
            for (ArrayList<BlockState> list : blocks) {
                Iterator<BlockState> listIterator = list.iterator();
                while (listIterator.hasNext()) {
                    BlockState blockState = listIterator.next();

                    Location blockLocation = blockState.getLocation();

                    if (blockLocation.getX() == brokenBlockState.getX() &&
                            blockLocation.getY() == brokenBlockState.getY() &&
                            blockLocation.getZ() == brokenBlockState.getZ()) {

    //                    event.getPlayer().sendMessage(event.getBlock().getType().name());
                        listIterator.remove();
                        player.sendMessage("Blocks Placed: " + blocks[0].size());
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
        for (ArrayList<BlockState> list : blocks) {
            Iterator<BlockState> listIterator = list.iterator();
            while (listIterator.hasNext()) {
                BlockState blockState = listIterator.next();
                blockState.getBlock().setType(Material.AIR);
                listIterator.remove();
            }
        }
    }

}
