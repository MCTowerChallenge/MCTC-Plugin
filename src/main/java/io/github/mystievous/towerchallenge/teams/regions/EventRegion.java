package io.github.mystievous.towerchallenge.teams.regions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;

public abstract class EventRegion implements Listener {

    protected final TowerChallenge plugin;
    private final TowerTeam team;
    private ProtectedRegion region;
    private final Location[] area;

    public EventRegion(TowerChallenge plugin, Location[] area, TowerTeam team) {
        this.plugin = plugin;
        this.team = team;
        this.area = area;
        loadRegion();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public abstract void unregisterEvents();

    public abstract String getRegionName();

    public abstract String parentRegionName();

    protected abstract void setFlags(ProtectedRegion region);

    public void loadRegion() {

        String name = getRegionName();

        region = RegionUtils.upsertRegion(name, area[0], area[1], parentRegionName());

        setFlags(region);

    }

    public TowerTeam getTeam() {
        return team;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public boolean isMember(Player player) {
        return region.isMember(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    public void addPlayer(OfflinePlayer player) {
        region.getMembers().addPlayer(player.getUniqueId());
    }

    public void addPlayers(Collection<OfflinePlayer> players) {
        for (OfflinePlayer player : players) {
            region.getMembers().addPlayer(player.getUniqueId());
        }
    }

    public void clearPlayers() {
        region.getMembers().clear();
    }

    public boolean checkInRegion(Location location) {
        return region.contains(BukkitAdapter.adapt(location).toVector().toBlockPoint());
    }

    public boolean checkInRegion(BlockState block) {
        return checkInRegion(block.getLocation());
    }

    public boolean checkInRegion(Entity entity) {
        return checkInRegion(entity.getLocation());
    }

}
