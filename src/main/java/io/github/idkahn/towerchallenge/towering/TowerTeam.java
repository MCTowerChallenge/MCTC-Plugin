package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.idkahn.towerchallenge.Hats.HatGUI;
import io.github.idkahn.towerchallenge.TowerChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TowerTeam {

    public static final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    // Server's scoreboard
    public static Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

    private Team team;
    private ProtectedRegion teamArea;
    private JavaPlugin plugin;
    private String name;
    private String displayName;
    private String color;
    private HatGUI hatGUI;

    private SpawnArea spawnArea;
    private TowerArea towerArea;

    public TowerTeam(JavaPlugin plugin, String displayName, String color) {
        this.plugin = plugin;
        this.displayName = displayName;
        this.name = displayName.replaceAll("\\s", "");
        this.color = color;
        Team team = scoreboard.getTeam(this.name);
        if (team != null) {
            this.team = team;
        } else {
            this.team = scoreboard.registerNewTeam(this.name);
            this.team.displayName(Component.text(displayName));
        }
        this.team.prefix(Component.text("[").append(Component.text(name, TextColor.fromHexString(color))).append(Component.text("] ")));
        this.hatGUI = new HatGUI(plugin, Color.fromRGB(Integer.parseInt(this.color.replaceAll("#", ""), 16)));
        this.loadRegions();
    }

    public TowerTeam(JavaPlugin plugin, String displayName) {
        this(plugin, displayName, "#FFFFFF");
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
            this.spawnArea = new SpawnArea(plugin, container.get(BukkitAdapter.adapt(plugin.getServer().getWorld(spawn.get("world")))).getRegion(spawn.get("name")));
            this.towerArea = new TowerArea(plugin, container.get(BukkitAdapter.adapt(plugin.getServer().getWorld(tower.get("world")))).getRegion(tower.get("name")));
        }
    }

    public String getColor() {
        return color;
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
                spawnArea.addPlayer(player);
                towerArea.addPlayer(player);
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
