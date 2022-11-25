package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.halloween.candy.Candy;
import io.github.idkahn.towerchallenge.halloween.candy.CandyUtils;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.spawncompass.SpawnCompass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class ParticipantTeam extends TowerTeam {

    public static final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();


    private int extraScore;
    private SpawnArea spawnArea;
    private TowerArea towerArea;

    private Location frameLocation;

    public ParticipantTeam(EventManager manager, String displayName, String color, String dye) {
        super(manager, displayName, color, dye);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamScoreConfigFile);
        extraScore = config.getInt(displayName);
        this.loadRegions();
        this.loadPortal();
    }

    public void loadRegions() {
        YamlConfiguration config = getConfig();
        List<Map<?, ?>> regions = config.getMapList(getName());

        if (regions.size() >= 2) {
            HashMap<String, String> spawn = (HashMap<String, String>) regions.get(0);
            HashMap<String, String> tower = (HashMap<String, String>) regions.get(1);
            this.spawnArea = new SpawnArea(getManager(), container.get(BukkitAdapter.adapt(TowerChallenge.WORLD)).getRegion(spawn.get("name")));
            this.towerArea = new TowerArea(this, getManager(), container.get(BukkitAdapter.adapt(TowerChallenge.WORLD)).getRegion(tower.get("name")), getName());
        }
    }

    public void loadPortal() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        if (config.isString(getName()+".world")) {
            this.frameLocation = new Location(Bukkit.getWorld(config.getString(getName()+".world")), config.getInt(getName()+".x"), config.getInt(getName()+".y"), config.getInt(getName()+".z"));
            Block block = this.frameLocation.getBlock();
            block.setType(Material.END_PORTAL_FRAME);
            EndPortalFrame blockData = (EndPortalFrame) block.getBlockData();
            if (config.isString(getName()+".facing")) {
                blockData.setFacing(BlockFace.valueOf((config.getString(getName()+".facing")).toUpperCase()));
            }
            if (config.isBoolean(getName()+".completed")) {
                blockData.setEye(config.getBoolean(getName()+".completed"));
                block.setBlockData(blockData);
            }
            Bukkit.getLogger().info("Loaded portal frame for " + getName() + " at location " + this.frameLocation.getX() +" "+ this.frameLocation.getY() +" "+ this.frameLocation.getZ());
        }
    }

    public int getScore() {
        int score = getManager().getObjective().getScore(PlainTextComponentSerializer.plainText().serialize(getDisplayName())).getScore();
        return score+extraScore;
    }

    public int addExtraScore(int score) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamScoreConfigFile);
        extraScore += score;
        config.set(getName(), extraScore);
        try {
            config.save(TowerChallenge.teamScoreConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return extraScore;
    }

    public int removeExtraScore(int score) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamScoreConfigFile);
        extraScore -= score;
        config.set(getName(), extraScore);
        try {
            config.save(TowerChallenge.teamScoreConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return extraScore;
    }

    public int getExtraScore() {
        return extraScore;
    }

    public void setExtraScore(int extraScore) {
        this.extraScore = extraScore;
    }

    @Override
    public void addPlayerConfig(OfflinePlayer player) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamConfigFile);
        List<String> players = config.getStringList("Teams."+getName()+".players");
        players.add(player.getUniqueId().toString());
        try {
            config.save(TowerChallenge.teamConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getSpawnpoint() {
        return spawnArea.getSpawnpoint();
    }

    public Location getFrameLocation() {
        return frameLocation;
    }

    public boolean hasEye() {
        if (frameLocation.getBlock().getBlockData() instanceof EndPortalFrame frame) {
            return frame.hasEye();
        }
        return false;
    }

    public void placeEye() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(true);
        config.set(getName()+".completed", true);
        try {
            config.save(TowerChallenge.endPortalConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setBlockData(frameData);

        int remainingEyes = getManager().getTowerListener().getTeams().size()-getManager().getCompletedPortalFrames();

//        final Component mainTitle = getDisplayName().color(getTextColor());
////        final Component subtitle = Component.text("There are ", NamedTextColor.DARK_GRAY)
////                .append(Component.text(16-manager.getCompletedPortalFrames(), NamedTextColor.DARK_RED))
////                .append(Component.text(" remaining.", NamedTextColor.DARK_GRAY));
//        final Component subtitle = Component.text("has contributed to the End Portal!").color(NamedTextColor.WHITE);

        final Component chatMessage = getDisplayName().color(getTextColor())
                .append(Component.text(" has contributed to the End Portal! ").color(NamedTextColor.WHITE))
                .append(Component.text(remainingEyes+" remain... ").color(TowerChallenge.PRIMARY_COLOR));

        // Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
//        final Title title = Title.title(mainTitle, subtitle);

        // Send the title to your audience
//        Bukkit.getServer().showTitle(title);
        Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.MASTER, 100, 1));
        Bukkit.getServer().sendMessage(chatMessage);

        if (remainingEyes <= 0) {
            getManager().getEndPortal().openPortal();
        }

    }

    public void resetFrame() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(false);
        config.set(getName() + ".completed", false);
        try {
            config.save(TowerChallenge.endPortalConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setBlockData(frameData);
        Bukkit.getLogger().info("Reset frame for " + getName());
    }

    @Override
    public void clear() {
        getTeam().removeEntries(getTeam().getEntries());
        if (towerArea != null) {
            towerArea.clearPlayers();
        }
        if (spawnArea != null) {
            spawnArea.clearPlayers();
        }
    }

}
