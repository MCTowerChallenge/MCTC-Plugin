package io.github.mystievous.towerchallenge.teams;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quests.Quest;
import io.github.mystievous.towerchallenge.quests.QuestChangeEvent;
import io.github.mystievous.towerchallenge.teams.regions.SpawnRegion;
import io.github.mystievous.towerchallenge.teams.regions.TowerRegion;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ParticipantTeam extends TowerTeam {

    // Red
    public static final Location towerLocation = new Location(Worlds.Jun2023_tower(), -61, -63, 2);
    public static final Location[] towerBounds = new Location[]{
            new Location(Worlds.Jun2023_tower(), -60, -62, 3),
            new Location(Worlds.Jun2023_tower(), -62, 319, 1)
    };

    public static final Map<Integer, Location> towerLocations = new HashMap<>(){{
        put(2,new Location(Worlds.Jun2023_tower(), -61, -63, 2));    // Red
        put(3,new Location(Worlds.Jun2023_tower(), -54, -63, 9));    // Orange
        put(4,new Location(Worlds.Jun2023_tower(), -61, -63, 16));    // Yellow
        put(5,new Location(Worlds.Jun2023_tower(), -68, -63, 9));    // Lime
        put(6,new Location(Worlds.Jun2023_tower(), -69, -63, 1));    // Green
        put(7,new Location(Worlds.Jun2023_tower(), -64, -63, -4));    // Cyan
        put(8,new Location(Worlds.Jun2023_tower(), -58, -63, -4));    // Light Blue
        put(9,new Location(Worlds.Jun2023_tower(), -53, -63, 1));    // Blue
        put(10,new Location(Worlds.Jun2023_tower(), -48, -63, 6));   // Purple
        put(11,new Location(Worlds.Jun2023_tower(), -48, -63, 12));   // Magenta
        put(12,new Location(Worlds.Jun2023_tower(), -53, -63, 17));   // Pink
        put(13,new Location(Worlds.Jun2023_tower(), -58, -63, 22));   // White
        put(14,new Location(Worlds.Jun2023_tower(), -64, -63, 22));   // Light Gray
        put(15,new Location(Worlds.Jun2023_tower(), -69, -63, 17));   // Gray
        put(16,new Location(Worlds.Jun2023_tower(), -74, -63, 12));   // Black
        put(17,new Location(Worlds.Jun2023_tower(), -74, -63, 7));   // Brown
    }};

    // block on base spawn that will be selected for all other spawns
    // Red Team
    public static final Location baseSpawnLocation = new Location(Worlds.Jun2023(), 164, 64, -2236, 180, 0);

    // spawnpoint in the base spawn for the team players to spawn
    public static final Location basePlayerSpawn = new Location(Worlds.Jun2023(), 164.5, 65, -2235.5, 0, 16);
    public static final Location[] baseSpawnBounds = new Location[]{
            new Location(Worlds.Jun2023(), 158, 64, -2242),
            new Location(Worlds.Jun2023(), 170, 75, -2230)
    };

    public static final Map<Integer, Location> teamSpawnLocations = new HashMap<>(){{
        put(2,new Location(Worlds.Jun2023(), 164, 64, -2236, 180, 0));    // Red
        put(3,new Location(Worlds.Jun2023(), 186, 64, -2214, -90, 0));    // Orange
        put(4,new Location(Worlds.Jun2023(), 184, 64, -2235, -90, 0));    // Yellow
        put(5,new Location(Worlds.Jun2023(), 206, 64, -2213, 180, 0));    // Lime
        put(6,new Location(Worlds.Jun2023(), 205, 63, -2167, -90, 0));    // Green
        put(7,new Location(Worlds.Jun2023(), 192, 64, -2194, 0, 0));    // Cyan
        put(8,new Location(Worlds.Jun2023(), 225, 63, -2172, 0, 0));    // Light Blue
        put(9,new Location(Worlds.Jun2023(), 216, 63, -2192, 90, 0));    // Blue
        put(10,new Location(Worlds.Jun2023(), 245, 63, -2172, 90, 0));   // Purple
        put(11,new Location(Worlds.Jun2023(), 167, 64, -2216, 90, 0));   // Magenta
        put(12,new Location(Worlds.Jun2023(), 255, 63, -2194, -90, 0));   // Pink
        put(13,new Location(Worlds.Jun2023(), 236, 63, -2192, 180, 0));   // White
        put(14,new Location(Worlds.Jun2023(), 264, 63, -2174, 0, 0));   // Light Gray
        put(15,new Location(Worlds.Jun2023(), 186, 63, -2157, 90, 0));   // Gray
        put(16,new Location(Worlds.Jun2023(), 179, 64, -2255, -90, 0));   // Black
        put(17,new Location(Worlds.Jun2023(), 230, 63, -2212, -90, 0));   // Brown
    }};

    private SpawnRegion spawnRegion;
    private TowerRegion towerRegion;
    private Location frameLocation;

    public ParticipantTeam(TowerChallenge plugin, TeamManager teamManager, int databaseId, String displayName, Color color, String dye) {
        super(plugin, teamManager, databaseId, displayName, color, dye);
        this.loadRegions();
        this.loadPortal();
    }

    public void loadRegions() {

        if (towerLocations.containsKey(getDatabaseId())) {
            Location[] bounds = Arrays.stream(towerBounds).map(location -> {
                Location teamLocation = towerLocations.get(getDatabaseId());
                Vector offset = teamLocation.clone().subtract(towerLocation).toVector();

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

    public void loadPortal() {
        this.frameLocation = teamManager.getPortalFrame(this);
    }

    public void addExtraScore(int score) throws SQLException {
        teamManager.addExtraScore(this, score);
    }
    public int getExtraScore() throws SQLException {
        return teamManager.getExtraScore(this);
    }

    @Override
    public void addTeamPlayer(OfflinePlayer player) {
        try {
            getTeam().addPlayer(player);
            if (spawnRegion != null)
                spawnRegion.addPlayer(player);
        } catch (IllegalArgumentException e) {
            getPlugin().getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
        }
    }

    public Location getSpawnpoint() {
        return spawnRegion.getSpawnpoint();
    }

    public Location getFrameLocation() {
        return frameLocation;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.valueOf(getDye() + "_CONCRETE"));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getDisplayName().decoration(TextDecoration.ITALIC, false));
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        return item;
    }

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

            if (remainingEyes == 0) {
                teamManager.openEndPortal();
            }
        });

    }

    public void resetFrame() {
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(false);
        teamManager.setPortalFrameFilled(this, false);
        frame.setBlockData(frameData);
        Bukkit.getLogger().info("Reset frame for " + getTextName());
    }

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

    @EventHandler
    public void onQuestChange(final QuestChangeEvent event) {
        if (event.isCancelled())
            return;
        if (event.getTeam().getDatabaseId() != getDatabaseId())
            return;

        Quest quest = event.getQuest();
        if (quest != null) {
            sendMessage(TextUtil.formatText("New Quest: ").append(Component.text(quest.getFriendlyName()).color(NamedTextColor.WHITE)));
        } else {
            sendMessage(TextUtil.formatText("No more quests!"));
        }
        playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.RECORD, 1f, 1f));
    }

    @Override
    public void unregisterEvents() {
        QuestChangeEvent.getHandlerList().unregister(this);
        if (spawnRegion != null) {
            spawnRegion.unregisterEvents();
        }
        if (towerRegion != null) {
            towerRegion.unregisterEvents();
        }
    }
}
