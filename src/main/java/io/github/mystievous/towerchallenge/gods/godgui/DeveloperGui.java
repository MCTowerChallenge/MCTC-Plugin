package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.*;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DeveloperGui extends PresetGui {

    public DeveloperGui(ChallengeManager challengeManager) {
        super(Component.text("Developer Menu"), 6);

        ItemStack listTest = new ItemStack(Material.PAPER);
        ItemMeta listTestMeta = listTest.getItemMeta();
        listTestMeta.displayName(TextUtil.noItalic("Prefilled ListGUI for testing"));
        listTest.setItemMeta(listTestMeta);
        List<Element> testElements = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            Element randomElement = new Element(new ItemStack(Material.PAPER));
            testElements.add(randomElement);
        }
        ListGui testGui = new ListGui(Component.text("Test Gui"), testElements, new ButtonElement(ButtonElement.exitItem(), this::openInventory));
        Element testElement = new ButtonElement(listTest, testGui::openInventory);

        ItemStack addPlayerItem = new ItemStack(Material.PAPER);
        ItemMeta addPlayerMeta = addPlayerItem.getItemMeta();
        addPlayerMeta.displayName(TextUtil.noItalic("Add Player to Team"));
        addPlayerMeta.setCustomModelData(3);
        addPlayerItem.setItemMeta(addPlayerMeta);
        TeamGui addPlayerGui = new TeamGui(Component.text("Team to add Player to:"),
                participantTeam -> TextUtil.formatTexts(participantTeam.getDisplayName()), challengeManager.getTowerListener().getTeams().values(),
                (player, participantTeam) -> new PlayerGui(Component.text("Pick player to add:"),
                        offlinePlayer -> TextUtil.formatTexts(Component.empty()), Arrays.stream(Bukkit.getOfflinePlayers()).toList(),
                        (playerClicking, playerSelected) -> {
                            try {
                                String playerName = playerSelected.getName();
                                UUID playerId = playerSelected.getUniqueId();
                                if (challengeManager.getPlugin().getDatabase().upsertUserTeam(playerId, participantTeam)) {
                                    assert playerSelected.getName() != null;
                                    playerClicking.sendMessage(Component.text(playerSelected.getName()).append(Component.text(" set to team ")).append(participantTeam.getDisplayName()));
                                } else {
                                    playerClicking.sendMessage(String.format("Failed to update team for:\n    user: %s\n    team: %s", playerName, participantTeam));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                playerClicking.sendMessage(CommandUtils.errorMessage("Error updating the database"));
                            }
                            this.openInventory(playerClicking);
                        },
                        new ButtonElement(ButtonElement.exitItem(), this::openInventory)).openInventory(player),
                new ButtonElement(ButtonElement.exitItem(), this::openInventory));
        ButtonElement addPlayerElement = new ButtonElement(addPlayerItem, addPlayerGui::openInventory);

        final String tag = "hideentity";

        var ref = new Object() {
            Entity sheep = null;
        };

        ItemStack spawnItem = formatItem("Spawn Entity", Material.SHEEP_SPAWN_EGG, null);
        Element spawnElement = new ButtonElement(spawnItem, player -> {
            ref.sheep = player.getWorld().spawnEntity(player.getLocation(), EntityType.SHEEP);
            ref.sheep.addScoreboardTag(tag);
        });

        ItemStack hideEntity = formatItem("Hide Entity", Material.DIAMOND_SHOVEL, null);
        Element hideElement = new ButtonElement(hideEntity, player -> {
            Entity entity = ref.sheep;
            if (entity != null) {
                player.hideEntity(challengeManager.getPlugin(), entity);
            }
        });

        ItemStack showEntity = formatItem("Show Entity", Material.WATER_BUCKET, null);
        Element showElement = new ButtonElement(showEntity, player -> {
            Entity entity = ref.sheep;
            if (entity != null) {
                player.showEntity(challengeManager.getPlugin(), entity);
            }
        });

        /*
            Bottom to top,
            Negative to positive Z
            portal block boundaries
         */
        Vector[][] portalBlocks = new Vector[][]{
                {new Vector(97, 66, -2119), new Vector(97, 66, -2111)},
                {new Vector(97, 67, -2118), new Vector(97, 68, -2112)},
                {new Vector(97, 69, -2117), new Vector(97, 70, -2113)},
                {new Vector(97, 71, -2116), new Vector(97, 72, -2114)},
                {new Vector(97, 73, -2115), new Vector(97, 74, -2115)},
        };

        ItemStack closePortalItem = formatItem("Close Nether Portal", Material.CRYING_OBSIDIAN, null);
        Element closePortalElement = new ButtonElement(closePortalItem, player -> {
            for (Vector[] layer : portalBlocks) {
                for (int x = layer[0].getBlockX(); x <= layer[1].getBlockX(); x++) {
                    for (int y = layer[0].getBlockY(); y <= layer[1].getBlockY(); y++) {
                        for (int z = layer[0].getBlockZ(); z <= layer[1].getBlockZ(); z++) {
                            Location location = new Location(Worlds.Feb2023(), x, y, z);
                            Block block = location.getBlock();
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        });

        ItemStack openPortalItem = formatItem("Open Nether Portal", Material.OBSIDIAN, null);
        Element openPortalElement = new ButtonElement(openPortalItem, player -> {
            new ConfirmationGUI(Component.text("Confirm opening portal?"), player1 -> {
                for (Vector[] layer : portalBlocks) {
                    for (int x = layer[0].getBlockX(); x <= layer[1].getBlockX(); x++) {
                        for (int y = layer[0].getBlockY(); y <= layer[1].getBlockY(); y++) {
                            for (int z = layer[0].getBlockZ(); z <= layer[1].getBlockZ(); z++) {
                                Location location = new Location(Worlds.Feb2023(), x, y, z);
                                Block block = location.getBlock();
                                block.setType(Material.NETHER_PORTAL);
                                Orientable blockData = (Orientable) block.getBlockData();
                                blockData.setAxis(Axis.Z);
                                block.setBlockData(blockData);
                            }
                        }
                    }
                }
            }, this::openInventory).openInventory(player);
        });

        ItemStack hatItem = formatItem("Hat Gui", Material.DIAMOND_HELMET, 0);
        Element hatElement = new ButtonElement(hatItem, player -> {
            try {
                ListGui hatGui = new ListGui(Component.text("Select a Hat:"), challengeManager.getPlugin().getDatabase().getPlayerHatElements(player.getUniqueId()), new ButtonElement(ButtonElement.exitItem(), this::openInventory));
                hatGui.openInventory(player);
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(CommandUtils.errorMessage("Error getting hats."));
            }
        });

        placeElement(1, 1, testElement);
        placeElement(2, 1, addPlayerElement);
        placeElement(3, 1, spawnElement);
        placeElement(4, 1, hideElement);
        placeElement(5, 1, showElement);
        placeElement(6, 1, openPortalElement);
        placeElement(7, 1, closePortalElement);
        placeElement(8, 1, hatElement);

        placeElement(9, 1, new ButtonElement(ButtonElement.exitItem(), player -> {
            challengeManager.getGodManager().getGodGui().openInventory(player);
        }));

    }
}
