package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.idkahn.towerchallenge.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class TowerArea implements Listener {

    private HashMap<Material, BlockState> blocks;

    private ProtectedRegion region;
    private JavaPlugin plugin;

    public TowerArea(JavaPlugin plugin, ProtectedRegion region) {
        this.plugin = plugin;
        this.blocks = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        this.region = region;
    }

    public void isMember(Player player) {
        region.isMember((LocalPlayer) BukkitAdapter.adapt(player));
    }

    public void addPlayer(OfflinePlayer player) {
        region.getMembers().addPlayer(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        region.getMembers().removePlayer(player.getUniqueId());
    }

    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent event) {

        if (!event.isCancelled()) {
            Block block = event.getBlockPlaced();
            try {
                if (region.contains(BukkitAdapter.adapt(block.getLocation()).toVector().toBlockPoint())) {
                    if (blocks.get(block.getType()) != null) {
                        event.getPlayer().sendMessage("You've already placed that block!");
                        event.setCancelled(true);
                    } else {
                        blocks.put(block.getType(), block.getState());
                        event.getPlayer().sendMessage("You placed " + block.getType() + "!");
                        event.getPlayer().sendMessage("You have placed " + blocks.size() + " blocks!");
                    }
                }
            } catch (NullPointerException e) {
            }
        }

    }

}
