package io.github.mctowerchallenge.mctcplugin.team;

import io.github.mctowerchallenge.mctcplugin.portal.EndPortal;
import io.github.mctowerchallenge.mctcplugin.quest.Quest;
import io.github.mctowerchallenge.mctcplugin.quest.QuestCompleteEvent;
import io.github.mctowerchallenge.mctcplugin.team.regions.SpawnRegion;
import io.github.mctowerchallenge.mctcplugin.team.regions.TowerRegion;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A team representing participants in the tower challenge event.
 */
public class ParticipantTeam extends TowerTeam {

    /**
     * The anchor location of the base tower.
     */
    public static final Location baseTowerLocation = new Location(Worlds.TOWER(), -61, -63, 2); // Red

    /**
     * The bounds of the base tower.
     */
    public static final Location[] baseTowerBounds = new Location[]{
            new Location(Worlds.TOWER(), -60, -62, 3),
            new Location(Worlds.TOWER(), -62, 319, 1)
    };

    /**
     * The anchor locations for all the team towers.
     */
    public static final Map<Integer, Location> towerLocations = new HashMap<>(Map.ofEntries(
            Map.entry(2, new Location(Worlds.TOWER(), -61, -63, 2)),    // Red
            Map.entry(3, new Location(Worlds.TOWER(), -54, -63, 9)),    // Orange
            Map.entry(4, new Location(Worlds.TOWER(), -61, -63, 16)),   // Yellow
            Map.entry(5, new Location(Worlds.TOWER(), -68, -63, 9)),    // Lime
            Map.entry(6, new Location(Worlds.TOWER(), -69, -63, 1)),    // Green
            Map.entry(7, new Location(Worlds.TOWER(), -64, -63, -4)),   // Cyan
            Map.entry(8, new Location(Worlds.TOWER(), -58, -63, -4)),   // Light_Blue
            Map.entry(9, new Location(Worlds.TOWER(), -53, -63, 1)),    // Blue
            Map.entry(10, new Location(Worlds.TOWER(), -48, -63, 6)),   // Purple
            Map.entry(11, new Location(Worlds.TOWER(), -48, -63, 12)),  // Magenta
            Map.entry(12, new Location(Worlds.TOWER(), -53, -63, 17)),  // Pink
            Map.entry(13, new Location(Worlds.TOWER(), -58, -63, 22)),  // White
            Map.entry(14, new Location(Worlds.TOWER(), -64, -63, 22)),  // Light_Gray
            Map.entry(15, new Location(Worlds.TOWER(), -69, -63, 17)),  // Gray
            Map.entry(16, new Location(Worlds.TOWER(), -74, -63, 12)),  // Black
            Map.entry(17, new Location(Worlds.TOWER(), -74, -63, 7))    // Brown
    ));

    /**
     * The anchor location of the base team spawn.
     */
    public static final Location baseSpawnLocation = new Location(Worlds.Jan2024(), -1389.5, 86, -450.5); // Red

    /**
     * Location in the base team spawn that players
     * will spawn at.
     */
    public static final Location basePlayerSpawn = new Location(Worlds.Jan2024(), -1389.5, 86, -450.5, 90, 0);

    /**
     * Bounds of the base spawn.
     */
    public static final Location[] baseSpawnBounds = new Location[]{
            new Location(Worlds.Jan2024(), -1396, 85, -456),
            new Location(Worlds.Jan2024(), -1384, 319, -446)
    };

    /**
     * The anchor locations of all the team spawns.
     */
    public static final Map<Integer, Location> teamSpawnLocations = new HashMap<>(Map.ofEntries(
            Map.entry(2, new Location(Worlds.Jan2024(), -1390+0.5, 86, -451+0.5, -90f, 16f)),       // Red
            Map.entry(3, new Location(Worlds.Jan2024(), -1390+0.5, 88, -414+0.5, -90f, 16f)),       // Orange
            Map.entry(4, new Location(Worlds.Jan2024(), -1390+0.5, 85, -426+0.5, -90f, 16f)),       // Yellow
            Map.entry(5, new Location(Worlds.Jan2024(), -1414+0.5, 85, -401+0.5, 90f, 16f)),        // Lime
            Map.entry(6, new Location(Worlds.Jan2024(), -1414+0.5, 85, -450+0.5, 90f, 16f)),        // Green
            Map.entry(7, new Location(Worlds.Jan2024(), -1414+0.5, 88, -437+0.5, 90f, 16f)),        // Cyan
            Map.entry(8, new Location(Worlds.Jan2024(), -1414+0.5, 88, -377+0.5, 90f, 16f)),        // Light Blue
            Map.entry(9, new Location(Worlds.Jan2024(), -1414+0.5, 85, -365+0.5, 90f, 16f)),        // Blue
            Map.entry(10, new Location(Worlds.Jan2024(), -1390+0.5, 85, -387+0.5, -90f, 16f)),      // Purple
            Map.entry(11, new Location(Worlds.Jan2024(), -1390+0.5, 85, -439+0.5, -90f, 16f)),      // Magenta
            Map.entry(12, new Location(Worlds.Jan2024(), -1390+0.5, 85, -376+0.5, -90f, 16f)),      // Pink
            Map.entry(13, new Location(Worlds.Jan2024(), -1414+0.5, 85, -412+0.5, 90f, 16f)),       // White
            Map.entry(14, new Location(Worlds.Jan2024(), -1414+0.5, 86, -424+0.5, 90f, 16f)),       // Light Gray
            Map.entry(15, new Location(Worlds.Jan2024(), -1390+0.5, 88, -364+0.5, 90f, 16f)),       // Gray
            Map.entry(16, new Location(Worlds.Jan2024(), -1414+0.5, 85, -389+0.5, 90f, 16f)),       // Black
            Map.entry(17, new Location(Worlds.Jan2024(), -1390+0.5, 86, -401+0.5, -90f, 16f))       // Brown
    ));

    private SpawnRegion spawnRegion;
    private TowerRegion towerRegion;
    private Location frameLocation;

    /**
     * Constructs a new ParticipantTeam instance.
     *
     * @param plugin      The TowerChallenge plugin instance.
     * @param teamManager The TeamManager instance.
     * @param databaseId  The unique ID for the team in the database.
     * @param displayName The display name of the team.
     * @param color       The color of the team.
     * @param dye         The dye color name associated with the team.
     */
    public ParticipantTeam(MCTCPlugin plugin, TeamManager teamManager, int databaseId, String displayName, Color color, String dye) {
        super(plugin, teamManager, databaseId, displayName, color, dye);
        this.loadRegions();
        this.loadPortal();
    }

    /**
     * Loads all regions for this team, at the proper locations.
     */
    public void loadRegions() {

        if (towerLocations.containsKey(getDatabaseId())) {
            Location[] bounds = Arrays.stream(baseTowerBounds).map(location -> {
                Location teamLocation = towerLocations.get(getDatabaseId());
                Vector offset = teamLocation.clone().subtract(baseTowerLocation).toVector();

                return location.clone().add(offset).setDirection(teamLocation.getDirection());
            }).toArray(Location[]::new);
            this.towerRegion = new TowerRegion(getPlugin(), bounds, this, getTextName());
        }

        if (teamSpawnLocations.containsKey(getDatabaseId())) {
            Location teamLocation = teamSpawnLocations.get(getDatabaseId());
            Vector spawnOffset = teamLocation.clone().subtract(baseSpawnLocation).toVector();
            Location[] bounds = Arrays.stream(baseSpawnBounds).map(location -> location.clone().add(spawnOffset).setDirection(teamLocation.getDirection())).toArray(Location[]::new);

            Location spawnpointLocation = basePlayerSpawn.clone().add(spawnOffset).setDirection(teamLocation.getDirection());

            this.spawnRegion = new SpawnRegion(getPlugin(), bounds, spawnpointLocation, this);
        }


    }

    public void highlightSpawn(Player player) {
        spawnRegion.showHighlight(player);
    }

    public SpawnRegion getSpawnRegion() {
        return spawnRegion;
    }

    /**
     * Loads the end portal frame for this team.
     */
    public void loadPortal() {
        this.frameLocation = teamManager.getPortalFrame(this);
    }

    /**
     * Adds extra score to the team's total score.
     *
     * @param score The additional score to be added.
     * @throws SQLException If a database error occurs.
     */
    public void addExtraScore(int score) throws SQLException {
        teamManager.addExtraScore(this, score);
    }

    /**
     * Retrieves the extra score of the team.
     *
     * @return The extra score of the team.
     * @throws SQLException If a database error occurs.
     */
    public int getExtraScore() throws SQLException {
        return teamManager.getExtraScore(this);
    }

    @Override
    public void addTeamPlayer(OfflinePlayer player) {
        super.addTeamPlayer(player);
        if (spawnRegion != null) {
            spawnRegion.addPlayer(player);
        }
    }

    /**
     * Retrieves the spawn point for this team.
     *
     * @return The spawn point location.
     */
    public Location getSpawnpoint() {
        return spawnRegion.getSpawnpoint();
    }

    @Override
    public void teleportToSpawn(Player player) {
        Location spawnpoint = getSpawnpoint();
        player.teleport(spawnpoint);
    }

    /**
     * Retrieves the location of the end portal frame for this team.
     *
     * @return The location of the end portal frame.
     */
    public Location getFrameLocation() {
        return frameLocation;
    }

    @Override
    public ItemStack getRepresentation() {
        ItemStack item = new ItemStack(Material.valueOf(getDye() + "_CONCRETE"));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getDisplayName().decoration(TextDecoration.ITALIC, false));
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        return item;
    }

    /**
     * Updates the database and the in-game frame to place
     * an ender eye inside, and notify the server.
     */
    public void placeEye(EndPortal endPortal) {
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(true);
        frame.setBlockData(frameData);

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            teamManager.setPortalFrameFilled(this, true);

            int remainingEyes = teamManager.getRemainingPortalFrames();

            final Component chatMessage = getDisplayName().color(getColor().toTextColor())
                    .append(Component.text(" has contributed to the End Portal! ").color(NamedTextColor.WHITE))
                    .append(Component.text(remainingEyes + " remain... ").color(Palette.PRIMARY.toTextColor()));

            // Send the title to your audience
            Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.MASTER, 100, 1));
            Bukkit.getServer().sendMessage(chatMessage);

            Bukkit.getServer().sendMessage(Component.text(teamManager.getRemainingPortalFrames()));
            if (teamManager.getRemainingPortalFrames() <= 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        endPortal.openPortal();
                    }
                }.runTask(getPlugin());
            }
        });

    }

    /**
     * Resets this team's end frame to be empty.
     */
    public void resetFrame() {
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(false);
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> teamManager.setPortalFrameFilled(this, false));
        frame.setBlockData(frameData);
        Bukkit.getLogger().info("Reset frame for " + getTextName());
    }

    /**
     * Clears the players on this
     * team, and the members of
     * the team regions.
     */
    @Override
    public void clearPlayers() {
        super.clearPlayers();
        if (towerRegion != null) {
            towerRegion.clearPlayers();
        }
        if (spawnRegion != null) {
            spawnRegion.clearPlayers();
        }
    }

    /**
     * Handles the event when a quest is completed for this team.
     *
     * @param event The quest change event.
     */
    @EventHandler
    public void onQuestComplete(final QuestCompleteEvent event) {
        if (event.isCancelled())
            return;
        if (event.getTeam().getDatabaseId() != getDatabaseId())
            return;

        Quest quest = event.getQuest();
        sendMessage(TextUtil.formatText("Quest Complete: ").append(Component.text(quest.getFriendlyName()).color(NamedTextColor.WHITE)));
        playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.RECORD, 1f, 1f));
    }

    @Override
    public void unregisterEvents() {
        super.unregisterEvents();
        if (spawnRegion != null) {
            spawnRegion.unregisterEvents();
        }
        if (towerRegion != null) {
            towerRegion.unregisterEvents();
        }
    }
}
