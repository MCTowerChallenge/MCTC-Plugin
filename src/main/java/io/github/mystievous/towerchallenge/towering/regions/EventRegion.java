package io.github.mystievous.towerchallenge.towering.regions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class EventRegion {

    private final ParticipantTeam team;
    private final ProtectedRegion region;

    public EventRegion(ParticipantTeam team, ProtectedRegion region) {
        this.team = team;
        this.region = region;
    }

    public ParticipantTeam getTeam() {
        return team;
    }

    public BlockVector3 getCenter() {
        return region.getMaximumPoint().subtract(region.getMinimumPoint()).divide(2).add(region.getMinimumPoint());
    }

    public void setSpawnCenter(double y) {
        BlockVector3 center = getCenter();
        com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(new org.bukkit.Location(ParticipantTeam.getSpawnWorld(), center.getX()+1, y, center.getZ()+1));
        region.setFlag(Flags.SPAWN_LOC, location);
    }

    public void setTeleportCenter(double y) {
        BlockVector3 center = getCenter();
        com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(new org.bukkit.Location(ParticipantTeam.getSpawnWorld(), center.getX()+1, y, center.getZ()+1));
        region.setFlag(Flags.TELE_LOC, location);
    }

    public String getId() {
        return region.getId();
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
