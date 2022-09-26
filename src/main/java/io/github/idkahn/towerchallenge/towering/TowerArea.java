package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.idkahn.towerchallenge.EventManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;

public class TowerArea implements Listener {

    private final EnumMap<Material, BlockState> blocks = new EnumMap<>(Material.class);

    private final ProtectedRegion region;
    private final EventManager manager;

    public TowerArea(EventManager manager, ProtectedRegion region) {
        this.manager = manager;
        Bukkit.getServer().getPluginManager().registerEvents(this, manager.getPlugin());
        this.region = region;
    }

    public void addPlayer(OfflinePlayer player) {
        region.getMembers().addPlayer(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        region.getMembers().removePlayer(player.getUniqueId());
    }

    /**
     * Stores the block to the tower, as it is at the moment it's added.
     * @param block Block to be added
     * @return whether the event should be cancelled
     */
    private boolean addBlock(Audience audience, Block block) {
        if (region.contains(BukkitAdapter.adapt(block.getLocation()).toVector().toBlockPoint())) {
            Material material = block.getType();
            BlockState blockState = block.getState();
            if (blocks.get(material) == null) {
                blocks.put(material, blockState);
                audience.sendMessage(Component.text(String.format("Tower has %d blocks", blocks.size())));
                return false;
            } else {
                // block is already in tower
                return true;
            }
        } else {
            // block is outside this region
            return false;
        }
    }

    /**
     * Removes a block from the tower
     * @param block Block to be removed
     * @return whether the event should be cancelled
     */
    private boolean removeBlock(Audience audience, Block block) {
        if (region.contains(BukkitAdapter.adapt(block.getLocation()).toVector().toBlockPoint())) {
            Material material = block.getType();
            blocks.remove(material);
            audience.sendMessage(Component.text(String.format("Tower has %d blocks", blocks.size())));
        }
        return false;
    }

    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        if (manager.getEventPhase().equals(EventManager.Phase.TOWERING)) {
            boolean cancelEvent = addBlock(event.getPlayer(), event.getBlockPlaced());
            event.setCancelled(cancelEvent);
        }
    }

    @EventHandler
    public void onBreakBlock(final BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        if (manager.getEventPhase().equals(EventManager.Phase.TOWERING)) {
            boolean cancelEvent = removeBlock(event.getPlayer(), event.getBlock());
            event.setCancelled(cancelEvent);
        }
    }


}
