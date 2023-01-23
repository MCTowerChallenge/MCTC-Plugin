package io.github.mystievous.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.towering.regions.SpawnRegion;
import io.github.mystievous.towerchallenge.towering.regions.TowerRegion;
import io.github.mystievous.towerchallenge.utility.Color;
import io.github.mystievous.towerchallenge.utility.Palette;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.sql.SQLException;

public class ParticipantTeam extends TowerTeam {

    public static World getSpawnWorld() {
        return Worlds.WORLD();
    }

    public static World getTowerWorld() {
        return Worlds.WORLD();
    }

    private SpawnRegion spawnRegion;
    private TowerRegion towerRegion;
    private Location frameLocation;

    public ParticipantTeam(TowerChallenge plugin, TeamManager teamManager, int databaseId, String displayName, Color color, String dye) {
        super(plugin, teamManager, databaseId, displayName, color, dye);
        this.loadRegions();
        this.loadPortal();
    }

    public void loadRegions() {

        RegionManager worldContainer = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.WORLD()));

        if (worldContainer != null) {
            if (worldContainer.hasRegion(getSpawnName())) {
                this.spawnRegion = new SpawnRegion(getPlugin(), this, worldContainer.getRegion(getSpawnName()));
            } else {
                TowerChallenge.log("No Spawn Region for " + getTextName());
            }
            if (worldContainer.hasRegion(getTowerName())) {
                this.towerRegion = new TowerRegion(getPlugin(), this, worldContainer.getRegion(getTowerName()), getTextName());
            } else {
                TowerChallenge.log("No Tower Region for " + getTextName());
            }
        }
    }

    public String getSpawnName() {
        return String.format("%s_spawn", getDye().toLowerCase());
    }

    public String getTowerName() {
        return String.format("%s_tower", getDye().toLowerCase());
    }

    public void loadPortal() {
        this.frameLocation = teamManager.getPortalFrame(this);
        if (this.frameLocation != null) {
            Bukkit.getLogger().info("Loaded portal frame for " + getTextName() + " at location " + this.frameLocation.getX() + " " + this.frameLocation.getY() + " " + this.frameLocation.getZ());
        }
    }

    public TowerRegion getTowerRegion() {
        return towerRegion;
    }

    public SpawnRegion getSpawnRegion() {
        return spawnRegion;
    }

    public void centerRegions(double y) {
        if (towerRegion != null) {
            towerRegion.setSpawnCenter(y);
            towerRegion.setTeleportCenter(y);
        }
        if (spawnRegion != null) {
            spawnRegion.setSpawnCenter(y);
            spawnRegion.setTeleportCenter(y);
        }
    }

    public void addExtraScore(int score) {
        teamManager.addExtraScore(this, score);
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
        teamManager.setPortalFrame(this, true);

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
        teamManager.setPortalFrame(this, false);
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

}
