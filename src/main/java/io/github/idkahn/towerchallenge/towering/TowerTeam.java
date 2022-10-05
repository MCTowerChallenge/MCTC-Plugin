package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.TowerChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.*;

public class TowerTeam {

    public static final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    // Server's scoreboard
    public static Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

    private final Team team;
    private ProtectedRegion teamArea;
    private JavaPlugin plugin;
    private EventManager manager;
    private String name;
    private String displayName;
    private String color;
    private String dye;
    private HatGUI hatGUI;

    private SpawnArea spawnArea;
    private TowerArea towerArea;

    private Location frameLocation;

    public TowerTeam(EventManager manager, String displayName, String color, String dye) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.displayName = displayName;
        this.name = displayName.replaceAll("\\s", "");
        this.color = color;
        this.dye = dye.toUpperCase();
        Team team = scoreboard.getTeam(this.name);
        if (team != null) {
            this.team = team;
        } else {
            this.team = scoreboard.registerNewTeam(this.name);
            this.team.displayName(Component.text(displayName));
        }
        this.team.prefix(Component.text("[").append(Component.text(displayName, TextColor.fromHexString(color))).append(Component.text("] ")));
        this.hatGUI = new HatGUI(plugin, Color.fromRGB(Integer.parseInt(this.color.replaceAll("#", ""), 16)));
        this.loadRegions();
        this.loadPortal();
    }

    public TowerTeam(EventManager manager, String displayName) {
        this(manager, displayName, "#FFFFFF", "white");
    }

    public void loadHats() {
        hatGUI.loadHats();
    }

    public void loadRegions() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.regionConfigFile);
        List maps = config.getMapList(displayName);
        List<Map> regions = (List<Map>) maps;

        if (regions != null && regions.size() >= 2) {
            HashMap<String, String> spawn = (HashMap<String, String>) regions.get(0);
            HashMap<String, String> tower = (HashMap<String, String>) regions.get(1);
            this.spawnArea = new SpawnArea(manager, container.get(BukkitAdapter.adapt(plugin.getServer().getWorld(spawn.get("world")))).getRegion(spawn.get("name")));
            this.towerArea = new TowerArea(manager, container.get(BukkitAdapter.adapt(plugin.getServer().getWorld(tower.get("world")))).getRegion(tower.get("name")), displayName);
        }
    }

    public void loadPortal() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        this.frameLocation = new Location(Bukkit.getWorld(config.getString(displayName+".world")), config.getInt(displayName+".x"), config.getInt(displayName+".y"), config.getInt(displayName+".z"));
        Block block = this.frameLocation.getBlock();
        block.setType(Material.END_PORTAL_FRAME);
        EndPortalFrame blockData = (EndPortalFrame) block.getBlockData();
        blockData.setFacing(BlockFace.valueOf((config.getString(displayName+".facing")).toUpperCase()));
        blockData.setEye(config.getBoolean(displayName+".completed"));
        block.setBlockData(blockData);
        Bukkit.getLogger().info("Loaded portal frame for " + displayName + " at location " + this.frameLocation.getX() +" "+ this.frameLocation.getY() +" "+ this.frameLocation.getZ());
    }

    public String getColor() {
        return color;
    }

    public TextColor getTextColor() {
        return TextColor.fromHexString(color);
    }

    public String getDye() {
        return dye;
    }

    public void destroyTeam() {
        team.unregister();
    }

    public void setArea(ProtectedRegion teamArea) {
        this.teamArea = teamArea;
    }

    public void addPlayer(OfflinePlayer player, Boolean addToConfig) {
        try {
            team.addPlayer(player);
            if (!displayName.equals("God")) {
                if (spawnArea != null) {
                    spawnArea.addPlayer(player);
                }
                if (towerArea != null) {
                    towerArea.addPlayer(player);
                }
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
//            return;
        }
        if (addToConfig) {
            List configTeams = plugin.getConfig().getMapList("Teams");
            for (Object o : configTeams) {
                HashMap configTeam = (HashMap) o;
                if (configTeam.get("name") == team.displayName().toString()) {
                    ArrayList<String> players = (ArrayList<String>) configTeam.get("players");
                    if (!players.contains(player.getUniqueId().toString())) {

                        players.add(player.getUniqueId().toString());
                        plugin.getConfig().set("Teams", configTeams);

                    }
                }
            }
            plugin.saveConfig();
        }
    }
    public void addPlayer(OfflinePlayer player) {
        addPlayer(player, true);
    }

    public Team getTeam() {
        return team;
    }

    public Location getSpawnpoint() {
        return spawnArea.getSpawnpoint();
    }

    public Location getFrameLocation() {
        return frameLocation;
    }

    public boolean hasEye() {
        return ((EndPortalFrame) frameLocation.getBlock().getBlockData()).hasEye();
    }

    public void placeEye() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(true);
        config.set(displayName+".completed", true);
        try {
            config.save(TowerChallenge.endPortalConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setBlockData(frameData);

        int remainingEyes = 16-manager.getCompletedPortalFrames();

        final Component mainTitle = getDisplayName().color(getTextColor());
//        final Component subtitle = Component.text("There are ", NamedTextColor.DARK_GRAY)
//                .append(Component.text(16-manager.getCompletedPortalFrames(), NamedTextColor.DARK_RED))
//                .append(Component.text(" remaining.", NamedTextColor.DARK_GRAY));
        final Component subtitle = Component.text("has contributed to the End Portal!").color(NamedTextColor.WHITE);

        final Component chatMessage = getDisplayName().color(getTextColor())
                .append(Component.text(" has contributed to the End Portal! ").color(NamedTextColor.WHITE))
                .append(Component.text(remainingEyes+" remain... /th").color(NamedTextColor.DARK_RED));

        // Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
        final Title title = Title.title(mainTitle, subtitle);

        // Send the title to your audience
        Bukkit.getServer().showTitle(title);
        Bukkit.getServer().sendMessage(chatMessage);

        if (remainingEyes <= 0) {
            manager.getEndPortal().openPortal();
        }

    }

    public void resetFrame() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(false);
        config.set(displayName+".completed", false);
        try {
            config.save(TowerChallenge.endPortalConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setBlockData(frameData);
        Bukkit.getLogger().info("Reset frame for "+displayName);
    }

    public void removePlayer(OfflinePlayer player) {
        team.removePlayer(player);
    }

    public Boolean hasPlayer(OfflinePlayer player) {

        return team.hasPlayer(player);

    }

    public Set<String> getEntries() {
        return team.getEntries();
    }

    public Component getDisplayName() {
        return team.displayName();
    }

    public void openHatGUI(Player player) {
        hatGUI.openInventory(player);
    }
    public HatGUI getHatGUI() {
        return hatGUI;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

}
