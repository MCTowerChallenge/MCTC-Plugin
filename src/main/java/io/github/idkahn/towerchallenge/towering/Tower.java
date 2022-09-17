package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Tower {

    ArrayList<BlockState> blocks;
    ProtectedRegion region;



    public Tower() {
        blocks = new ArrayList<>();
    }

    public Tower(ProtectedRegion region) {
        this();
        this.region = region;
    }

    public void isMember(Player player) {
        region.isMember((LocalPlayer) BukkitAdapter.adapt(player));
    }

    public void addPlayer(Player player) {
        region.getMembers().addPlayer(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        region.getMembers().removePlayer(player.getUniqueId());
    }

}
