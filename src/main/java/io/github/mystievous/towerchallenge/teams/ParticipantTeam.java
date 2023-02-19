package io.github.mystievous.towerchallenge.teams;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quests.Quest;
import io.github.mystievous.towerchallenge.quests.QuestChangeEvent;
import io.github.mystievous.towerchallenge.teams.regions.SpawnRegion;
import io.github.mystievous.towerchallenge.teams.regions.TowerRegion;
import io.github.mystievous.towerchallenge.utility.Color;
import io.github.mystievous.towerchallenge.utility.Palette;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;

public class ParticipantTeam extends TowerTeam {

    public static World getSpawnWorld() {
        return Worlds.Feb2023();
    }

    public static World getTowerWorld() {
        return Worlds.Feb2023_tower();
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

        this.spawnRegion = new SpawnRegion(getPlugin(), this);
        this.towerRegion = new TowerRegion(getPlugin(), this, getTextName());

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
        spawnRegion.unregisterEvents();
        towerRegion.unregisterEvents();
    }
}
