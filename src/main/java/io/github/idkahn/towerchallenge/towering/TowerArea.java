package io.github.idkahn.towerchallenge.towering;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTTileEntity;
import io.github.idkahn.towerchallenge.EventManager;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scoreboard.Score;

import java.util.EnumMap;

public class TowerArea implements Listener {

    private final EnumMap<Material, BlockState> blocks = new EnumMap<>(Material.class);

    private final ProtectedRegion region;
    private final EventManager manager;
    private final TowerTeam team;
    private final Score score;

    public TowerArea(TowerTeam team, EventManager manager, ProtectedRegion region, String name) {
        this.team = team;
        this.manager = manager;
        Bukkit.getServer().getPluginManager().registerEvents(this, manager.getPlugin());
        this.region = region;
        score = manager.getObjective().getScore(name);
        score.setScore(blocks.size());
        scanBlocks();
    }

    public void scanBlocks() {
        BlockVector3 minPoint = region.getMinimumPoint();
        BlockVector3 maxPoint = region.getMaximumPoint();
        for (int y = minPoint.getBlockY(); y <= maxPoint.getBlockY(); y++) {
            for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
                for (int z = minPoint.getBlockZ(); z < maxPoint.getBlockZ(); z++) {
                    Block block = Bukkit.getWorld("world").getBlockAt(x, y, z);
                    if (!block.getType().isAir()) {
                        addBlock(null, block.getState());
                    }
                }
            }
        }

    }

    public void addPlayer(OfflinePlayer player) {
        region.getMembers().addPlayer(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        region.getMembers().removePlayer(player.getUniqueId());
    }

    public void clearPlayers() {
        region.getMembers().clear();
    }

    public void sendBlockCount(Audience audience) {
        if (blocks.size() == 1) {
            audience.sendMessage(
                    team.getDisplayName().color(team.getTextColor()).append(Component.text(" Tower"))
                            .append(Component.text(String.format(" has %d block", blocks.size())).color(NamedTextColor.WHITE))
            );
        } else {
            audience.sendMessage(
                    team.getDisplayName().color(team.getTextColor()).append(Component.text(" Tower"))
                            .append(Component.text(String.format(" has %d block", blocks.size())).color(NamedTextColor.WHITE))
            );
        }
    }

    private boolean exclude(BlockState block) {
        if (block instanceof ShulkerBox box) {
            if (box.customName() != null && PlainTextComponentSerializer.plainText().serialize(box.customName()).equals(TowerTeam.SHULKER_NAME)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stores the block to the tower, as it is at the moment it's added.
     * @param block Block to be added
     * @return true, if the tower already has that block
     */
    private boolean addBlock(Audience audience, BlockState block) {
        Material material = block.getType();

        if (exclude(block)) {
            return false;
        }

        if (blocks.get(material) == null) {
            blocks.put(material, block);
//            sendBlockCount(audience);
            score.setScore(blocks.size());
            return false;
        } else {
            // block is already in tower
            if (audience != null) {
                audience.sendActionBar(Component.text("You've already placed ").append(Component.text(EventManager.formatBlockType(block.getType())).color(NamedTextColor.DARK_RED)));
            }
            return true;
        }
    }

    /**
     * Removes a block from the tower
     * @param block Block to be removed
     */
    private void removeBlock(BlockState block) {
        if (exclude(block)) {
            return;
        }

        Material material = block.getType();
        if (blocks.remove(material) != null) {
            // block did not exist in tower
            score.setScore(blocks.size());
        }
    }

    /**
     * Checks whether a block is in the region
     * @param block block to check
     * @return true, if the block is in the region
     */
    private boolean checkInRegion(BlockState block) {
        return region.contains(BukkitAdapter.adapt(block.getLocation()).toVector().toBlockPoint());
    }
    private boolean checkInRegion(Location location) {
        return region.contains(BukkitAdapter.adapt(location).toVector().toBlockPoint());
    }
    private boolean checkInRegion(Entity entity) {
        return region.contains(BukkitAdapter.adapt(entity.getLocation()).toVector().toBlockPoint());
    }

    private boolean checkFullBlock(BlockState block) {
        return manager.isFullBlock(block.getType());
    }

    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        BlockState block = event.getBlockPlaced().getState();
        if (checkInRegion(block)) {
            if (manager.isTowering()) {
                if (checkFullBlock(block)) {
                    boolean cancelEvent = addBlock(event.getPlayer(), event.getBlockPlaced().getState());
                    event.setCancelled(cancelEvent);
                } else {
                    event.getPlayer().sendActionBar(Component.text(EventManager.formatBlockType(block.getType())).color(NamedTextColor.DARK_RED)
                            .append(Component.text("is not a full block!").color(NamedTextColor.WHITE)));
                    event.setCancelled(true);
                }
            } else {
                event.getPlayer().sendActionBar(Component.text("You cannot build in the tower areas yet!"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreakBlock(final BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        BlockState block = event.getBlock().getState();
        if (checkInRegion(block)) {
            if (manager.isTowering()) {
                removeBlock(event.getBlock().getState());
            } else {
                event.getPlayer().sendActionBar(Component.text("You cannot build in the tower areas yet!"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (event.isCancelled())
            return;
        if (region.contains(BukkitAdapter.adapt(event.getLocation()).toVector().toBlockPoint())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(final BlockFadeEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpongeAbsorb(final SpongeAbsorbEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTNTPrime(final TNTPrimeEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockGrow(final BlockGrowEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(final BlockFormEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerShearBlock(final PlayerShearBlockEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

}
