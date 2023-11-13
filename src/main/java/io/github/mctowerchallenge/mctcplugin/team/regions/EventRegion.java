package io.github.mctowerchallenge.mctcplugin.team.regions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mctowerchallenge.mctcplugin.utility.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public abstract class EventRegion implements Listener {

    protected final Plugin plugin;
    private final TowerTeam team;
    private ProtectedRegion region;
    private final Location[] area;
    private final String tag;

    /**
     * Creates and/or updates a team's region for the event.
     * <p></p>
     * After region is created, {@link #setFlags(ProtectedRegion)}
     * should be called on it.
     *
     * @param plugin The current plugin instance.
     * @param area   The bounds of the region to create.
     * @param team   The team it is associated with.
     * @param tag    The "base" name for the region.
     * @see #getRegionName()
     */
    public EventRegion(Plugin plugin, Location[] area, TowerTeam team, String tag) {
        this.plugin = plugin;
        this.team = team;
        this.area = area;
        this.tag = tag;
        loadRegion();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public abstract void unregisterEvents();

    /**
     * Gets the region name, which
     * is the team name and the tag.
     *
     * @return The full name.
     */
    public String getRegionName() {
        return String.format("%s-%s", getTeam().getServerTeamName(), tag);
    }

    public abstract String parentRegionName();

    /**
     * Sets all proper region
     * protection flags.
     *
     * @param region The region to set the flags of.
     */
    protected abstract void setFlags(ProtectedRegion region);

    /**
     * Loads/reloads this region, creating
     * and/or updating its bounds and parent.
     */
    public void loadRegion() {

        String name = getRegionName();

        region = WorldUtils.upsertRegion(name, area[0], area[1], parentRegionName());

    }

    public TowerTeam getTeam() {
        return team;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    /**
     * Checks whether a player is
     * a member of this region.
     *
     * @param player The player to check.
     * @return True, if the player is a member.
     */
    public boolean isMember(Player player) {
        return region.isMember(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    /**
     * Adds a player as a member
     * of this region.
     *
     * @param player The player to add.
     */
    public void addPlayer(OfflinePlayer player) {
        region.getMembers().addPlayer(player.getUniqueId());
    }

    /**
     * Adds a list of players as
     * members of this region.
     *
     * @param players The players to add.
     */
    public void addPlayers(Collection<OfflinePlayer> players) {
        for (OfflinePlayer player : players) {
            region.getMembers().addPlayer(player.getUniqueId());
        }
    }

    /**
     * Clears the members of
     * this region.
     */
    public void clearPlayers() {
        region.getMembers().clear();
    }

    /**
     * Checks if a location is
     * within the bounds of this
     * region.
     *
     * @param location The location.
     * @return True, if it is within the region.
     */
    public boolean checkInRegion(Location location) {
        return location.getWorld().equals(area[0].getWorld()) && region.contains(BukkitAdapter.adapt(location).toVector().toBlockPoint());
    }

    /**
     * Checks if a block is
     * within the bounds of this
     * region.
     *
     * @param block The block.
     * @return True, if it is within the region.
     */
    public boolean checkInRegion(BlockState block) {
        return checkInRegion(block.getLocation());
    }

    /**
     * Checks if an entity is
     * within the bounds of this
     * region.
     *
     * @param entity The entity.
     * @return True, if it is within the region.
     */
    public boolean checkInRegion(Entity entity) {
        return checkInRegion(entity.getLocation());
    }

}
