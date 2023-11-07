package io.github.mystievous.towerchallenge.team;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quest.Quest;
import io.github.mystievous.towerchallenge.quest.QuestChangeEvent;
import io.github.mystievous.towerchallenge.quest.QuestCompleteEvent;
import io.github.mystievous.towerchallenge.team.regions.SpawnRegion;
import io.github.mystievous.towerchallenge.team.regions.TowerRegion;
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
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    public static final Map<Integer, Location> towerLocations = new HashMap<>() {{
        put(2, new Location(Worlds.TOWER(), -61, -63, 2));    // Red
        put(3, new Location(Worlds.TOWER(), -54, -63, 9));    // Orange
        put(4, new Location(Worlds.TOWER(), -61, -63, 16));    // Yellow
        put(5, new Location(Worlds.TOWER(), -68, -63, 9));    // Lime
        put(6, new Location(Worlds.TOWER(), -69, -63, 1));    // Green
        put(7, new Location(Worlds.TOWER(), -64, -63, -4));    // Cyan
        put(8, new Location(Worlds.TOWER(), -58, -63, -4));    // Light Blue
        put(9, new Location(Worlds.TOWER(), -53, -63, 1));    // Blue
        put(10, new Location(Worlds.TOWER(), -48, -63, 6));   // Purple
        put(11, new Location(Worlds.TOWER(), -48, -63, 12));   // Magenta
        put(12, new Location(Worlds.TOWER(), -53, -63, 17));   // Pink
        put(13, new Location(Worlds.TOWER(), -58, -63, 22));   // White
        put(14, new Location(Worlds.TOWER(), -64, -63, 22));   // Light Gray
        put(15, new Location(Worlds.TOWER(), -69, -63, 17));   // Gray
        put(16, new Location(Worlds.TOWER(), -74, -63, 12));   // Black
        put(17, new Location(Worlds.TOWER(), -74, -63, 7));   // Brown
    }};

    /**
     * The anchor location of the base team spawn.
     */
    public static final Location baseSpawnLocation = new Location(Worlds.Oct2023_the_end(), 0.5f, 80f, -59.5f, 180f, 16f); // Red

    /**
     * Location in the base team spawn that players
     * will spawn at.
     */
    public static final Location basePlayerSpawn = new Location(Worlds.Oct2023_the_end(), 0.5f, 80f, -59.5f, 180f, 16f);

    /**
     * Bounds of the base spawn.
     */
    public static final Location[] baseSpawnBounds = new Location[]{
            new Location(Worlds.Oct2023_the_end(), -8, 80, -68),
            new Location(Worlds.Oct2023_the_end(), 8, 255, -52)
    };

    /**
     * The anchor locations of all the team spawns.
     */
    public static final Map<Integer, Location> teamSpawnLocations = new HashMap<>() {{
        put(2, new Location(Worlds.Oct2023_the_end(), 0.5f, 80f, -59.5f, 180f, 16f));    // Red
        put(3, new Location(Worlds.Oct2023_the_end(), 23.5f, 80f, -54.5f, 180f, 16f));    // Orange
        put(4, new Location(Worlds.Oct2023_the_end(), 42.5f, 80f, -41.5f, 180f, 16f));    // Yellow
        put(5, new Location(Worlds.Oct2023_the_end(), 55.5f, 80f, -22.5f, 180f, 16f));    // Lime
        put(6, new Location(Worlds.Oct2023_the_end(), 60.5f, 80f, 0.5f, -90f, 16f));    // Green
        put(7, new Location(Worlds.Oct2023_the_end(), 55.5f, 80f, 23.5f, -90f, 16f));    // Cyan
        put(8, new Location(Worlds.Oct2023_the_end(), 42.5f, 80f, 42.5f, -90f, 16f));    // Light Blue
        put(9, new Location(Worlds.Oct2023_the_end(), 23.5f, 80f, 55.5f, -90f, 16f));    // Blue
        put(10, new Location(Worlds.Oct2023_the_end(), 0.5f, 80f, 60.5f, 0.0f, 16f));   // Purple
        put(11, new Location(Worlds.Oct2023_the_end(), -22.5f, 80f, 56.5f, 0.0f, 16f));   // Magenta
        put(12, new Location(Worlds.Oct2023_the_end(), -41.5f, 80f, 42.5f, 0.0f, 16f));   // Pink
        put(13, new Location(Worlds.Oct2023_the_end(), -54.5f, 80f, 23.5f, 0.0f, 16f));   // White
        put(14, new Location(Worlds.Oct2023_the_end(), -59.5f, 80f, 0.5f, 90.0f, 16f));   // Light Gray
        put(15, new Location(Worlds.Oct2023_the_end(), -54.5f, 80f, -22.5f, 90.0f, 16f));   // Gray
        put(16, new Location(Worlds.Oct2023_the_end(), -41.5f, 80f, -41.5f, 90.0f, 16f));   // Black
        put(17, new Location(Worlds.Oct2023_the_end(), -22.5f, 80f, -54.5f, 90.0f, 16f));   // Brown
    }};

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
    public ParticipantTeam(TowerChallenge plugin, TeamManager teamManager, int databaseId, String displayName, Color color, String dye) {
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
    public void placeEye() {
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

//    /**
//     * Handles the event when quests are completed/changed for this team.
//     *
//     * @param event The quest change event.
//     */
//    @EventHandler
//    public void onQuestChange(final QuestChangeEvent event) {
//        if (event.isCancelled())
//            return;
//        if (event.getTeam().getDatabaseId() != getDatabaseId())
//            return;
//
//        Quest quest = event.getQuest();
//        if (quest != null) {
//            sendMessage(TextUtil.formatText("New Quest: ").append(Component.text(quest.getFriendlyName()).color(NamedTextColor.WHITE)));
//        } else {
//            sendMessage(TextUtil.formatText("No more quests!"));
//        }
//        playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.RECORD, 1f, 1f));
//    }

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
