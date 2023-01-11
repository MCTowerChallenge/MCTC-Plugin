package io.github.mystievous.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.towering.regions.SpawnRegion;
import io.github.mystievous.towerchallenge.towering.regions.TowerRegion;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.List;

public class ParticipantTeam extends TowerTeam {

    public static World getSpawnWorld() {
        return Worlds.WORLD();
    }
    public static World getTowerWorld() {
        return Worlds.WORLD();
    }

    private int extraScore;
    private SpawnRegion spawnRegion;
    private TowerRegion towerRegion;
    private Location frameLocation;

    public ParticipantTeam(ChallengeManager manager, String displayName, String color, String dye) {
        super(manager, displayName, color, dye);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamScoreConfigFile);
        extraScore = config.getInt(displayName);
        this.loadRegions();
        this.loadPortal();
    }

    public void loadRegions() {

        RegionManager worldContainer = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.WORLD()));

        if (worldContainer != null) {
            if (worldContainer.hasRegion(getSpawnName())) {
                this.spawnRegion = new SpawnRegion(this, getManager(), worldContainer.getRegion(getSpawnName()));
            } else {
                ChallengeManager.log("No Spawn Region for "+getTextName());
            }
            if (worldContainer.hasRegion(getTowerName())) {
                this.towerRegion = new TowerRegion(this, getManager(), worldContainer.getRegion(getTowerName()), getTextName());
            } else {
                ChallengeManager.log("No Tower Region for "+getTextName());
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
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        if (config.isString(getTextName()+".world")) {
            this.frameLocation = new Location(Worlds.WORLD(), config.getInt(getTextName()+".x"), config.getInt(getTextName()+".y"), config.getInt(getTextName()+".z"));
            Block block = this.frameLocation.getBlock();
            block.setType(Material.END_PORTAL_FRAME);
            EndPortalFrame blockData = (EndPortalFrame) block.getBlockData();
            if (config.isString(getTextName()+".facing")) {
                blockData.setFacing(BlockFace.valueOf((config.getString(getTextName()+".facing")).toUpperCase()));
            }
            if (config.isBoolean(getTextName()+".completed")) {
                blockData.setEye(config.getBoolean(getTextName()+".completed"));
                block.setBlockData(blockData);
            }
            Bukkit.getLogger().info("Loaded portal frame for " + getTextName() + " at location " + this.frameLocation.getX() +" "+ this.frameLocation.getY() +" "+ this.frameLocation.getZ());
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

    public int getScore() {
        int score = getManager().getObjective().getScore(PlainTextComponentSerializer.plainText().serialize(getDisplayName())).getScore();
        return score+extraScore;
    }

    public int addExtraScore(int score) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamScoreConfigFile);
        extraScore += score;
        config.set(getTextName(), extraScore);
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
        config.set(getTextName(), extraScore);
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
    public void addPlayer(OfflinePlayer player) {
        try {
            getTeam().addPlayer(player);
            if (spawnRegion != null)
                spawnRegion.addPlayer(player);
        } catch (IllegalArgumentException e) {
            getPlugin().getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
        }
    }

    @Override
    public void addPlayerConfig(OfflinePlayer player) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamConfigFile);
        List<String> players = config.getStringList("Teams."+ getTextName()+".players");
        players.add(player.getUniqueId().toString());
        try {
            config.save(TowerChallenge.teamConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getSpawnpoint() {
        return spawnRegion.getSpawnpoint();
    }

    public Location getFrameLocation() {
        return frameLocation;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.valueOf(getDye()+"_CONCRETE"));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getDisplayName().decoration(TextDecoration.ITALIC, false));
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        return item;
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
        config.set(getTextName()+".completed", true);
        try {
            config.save(TowerChallenge.endPortalConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setBlockData(frameData);

        int remainingEyes = getManager().getTowerListener().getTeams().size()-getManager().getCompletedPortalFrames();

        final Component chatMessage = getDisplayName().color(getTextColor())
                .append(Component.text(" has contributed to the End Portal! ").color(NamedTextColor.WHITE))
                .append(Component.text(remainingEyes+" remain... ").color(TowerChallenge.PRIMARY_COLOR));

        // Send the title to your audience
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
        config.set(getTextName() + ".completed", false);
        try {
            config.save(TowerChallenge.endPortalConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setBlockData(frameData);
        Bukkit.getLogger().info("Reset frame for " + getTextName());
    }

    @Override
    public void clear() {
        getTeam().removeEntries(getTeam().getEntries());
        if (towerRegion != null) {
            towerRegion.clearPlayers();
        }
        if (spawnRegion != null) {
            spawnRegion.clearPlayers();
        }
    }

}
