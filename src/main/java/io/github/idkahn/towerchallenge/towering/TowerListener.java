package io.github.idkahn.towerchallenge.towering;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import io.github.idkahn.towerchallenge.BlockSets;
import io.github.idkahn.towerchallenge.TeamColors;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;

public class TowerListener implements Listener {

    private ArrayList<BlockState>[] blocks;

    BlockSets blockSets;

    // Whether server is currently in Towering State
    private boolean towering;
    private boolean cancelEvents;

    JavaPlugin plugin;

    public TowerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.blockSets = new BlockSets();
        this.towering = false;
        this.cancelEvents = false;
        this.blocks = new ArrayList[TeamColors.values().length];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new ArrayList<BlockState>();
        }
    }

    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent event) {

        Player player = event.getPlayer();
        BlockState placedBlockState = event.getBlockPlaced().getState();

        if (towering) {

            if (!blockSets.fullBlocks.contains(event.getBlockPlaced().getType())) {
                TextComponent text = Component.text(placedBlockState.getType().name(), NamedTextColor.RED)
                        .append(Component.text(" is not a full block!", NamedTextColor.WHITE));
                player.sendActionBar(text);
                event.setCancelled(true);
                return;
            }

//            if (blockSets.fallingBlocks.contains(event.getBlockPlaced().getType())) {
//                if (!event.getBlockPlaced().getRelative(BlockFace.DOWN).getType().isSolid()) {
//                    event.getPlayer().sendActionBar(Component.text("Don't place falling block over air!"));
//                    event.setCancelled(true);
//                    return;
//                }
//            }

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

        if (towering || cancelEvents && !event.isCancelled()) {
            if (event.getItemInHand().getType() == Material.WET_SPONGE) {

                BlockData wetSponge = Material.WET_SPONGE.createBlockData();

                if (event.getBlockPlaced().getWorld().getEnvironment() == World.Environment.NETHER) {
                    event.setCancelled(true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            event.getBlock().setBlockData(wetSponge, false);
                        }
                    }.runTask(plugin);
                }
            }
        }

    }



    @EventHandler
    public void onBreakBlock(final BlockBreakEvent event) {

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

    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.getLogger().info("Cancelled BlockBurn");
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.getLogger().info("Cancelled BlockSpread");
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.getLogger().info("Cancelled BlockExplode");
//        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.getLogger().info("Cancelled EntityExplode");
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(final BlockFadeEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockFade"));
//        event.setCancelled(true);
    }

//    @EventHandler
//    public void onBlockIgnite(final BlockIgniteEvent event) {
//        if (towering) event.setCancelled(true);
////        Bukkit.broadcast(Component.text("Cancelled BlockIgnite"));
////        event.setCancelled(true);
//    }

    @EventHandler
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockPistonExtend"));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockPistonRetract"));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onSpongeAbsorb(final SpongeAbsorbEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled SpongeAbsorb"));
//        event.setCancelled(true);
    }

//    @EventHandler
//    public void onBlockDestroy(final BlockDestroyEvent event) {
//        if (towering) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockDestroy"));
//        event.setCancelled(true);
//    }

    @EventHandler
    public void onTNTPrime(final TNTPrimeEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled TNTPrime"));
//        event.setCancelled(true);
    }

//    @EventHandler
//    public void onBlockPhysics(final BlockPhysicsEvent event) {
//        if (fallingBlocks.contains(event.getBlock().getRelative(BlockFace.UP).getType())) {
//            if (towering) event.setCancelled(true);
////            Bukkit.broadcast(Component.text("Cancelled BlockPhysics ").append(Component.text(event.getBlock().getType().name())));
////            event.setCancelled(true);
//        }
//    }

    @EventHandler
    public void onBlockGrow(final BlockGrowEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockGrow ").append(Component.text(event.getBlock().getType().name())));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(final BlockFormEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockForm ").append(Component.text(event.getBlock().getType().name())));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled EntityChangeBlock ").append(Component.text(event.getEntity().getType().name())));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerShearBlock(final PlayerShearBlockEvent event) {
        if (towering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled EntityChangeBlock ").append(Component.text(event.getEntity().getType().name())));
//        event.setCancelled(true);
    }

    public void enableTower() {
        towering = true;
    }

    public void disableTower() {
        towering = false;
    }

    public void enableEvents() {
        cancelEvents = false;
    }

    public void disableEvents() {
        cancelEvents = true;
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
