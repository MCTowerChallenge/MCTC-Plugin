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

    public static final Location towerLocation = new Location(Worlds.Feb2023_tower(), -60, -63, 3);
    public static final Location[] towerBounds = new Location[]{
            new Location(Worlds.Feb2023_tower(), -60, -62, 3),
            new Location(Worlds.Feb2023_tower(), -62, 319, 1)
    };

    public static final Map<Integer, Location> towerLocations = new HashMap<>();

    public static final Location spawnLocation = new Location(Worlds.Feb2023(), 136, 63, -2100, 0, 0);

    public static final Location baseSpawn = new Location(Worlds.Feb2023(), 142, 64, -2094);
    public static final Location[] spawnBounds = new Location[]{
            new Location(Worlds.Feb2023(), 133, 64, -2103),
            new Location(Worlds.Feb2023(), 150, 319, -2086)
    };

    public static final Map<Integer, Location> spawnLocations = new HashMap<>();

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

        if (spawnLocations.containsKey(getDatabaseId())) {
            Location teamLocation = spawnLocations.get(getDatabaseId());
            Vector offset = teamLocation.clone().subtract(spawnLocation).toVector();
            Location[] bounds = Arrays.stream(spawnBounds).map(location -> location.clone().add(offset).setDirection(teamLocation.getDirection())).toArray(Location[]::new);

            Location spawnLocation = baseSpawn.clone().add(offset).setDirection(teamLocation.getDirection());
            this.spawnRegion = new SpawnRegion(getPlugin(), bounds, spawnLocation, this);
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
