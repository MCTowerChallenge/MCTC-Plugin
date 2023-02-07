package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.*;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.quests.TextFormatter;
import io.github.mystievous.towerchallenge.utility.DefaultFontInfo;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeveloperGui extends PresetGui {

    public DeveloperGui(TowerChallenge plugin, TeamManager teamManager) {
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
        TeamGui addPlayerGui = new TeamGui(
                Component.text("Team to add Player to:"),
                team -> new ArrayList<>(),
                teamManager.getAllTeams(),
                (player, participantTeam) -> new PlayerGui(Component.text("Pick player to add:"),
                        offlinePlayer -> TextUtil.formatTexts(Component.empty()), Arrays.stream(Bukkit.getOfflinePlayers()).toList(),
                        (playerClicking, playerSelected) -> {
                            if (teamManager.setPlayerTeam(player, participantTeam)) {
                                assert playerSelected.getName() != null;
                                playerClicking.sendMessage(Component.text(playerSelected.getName()).append(Component.text(" set to team ")).append(participantTeam.getDisplayName()));
                                Bukkit.getScheduler().runTaskAsynchronously(plugin, teamManager::loadPlayers);
                            } else {
                                playerClicking.sendMessage(CommandUtils.errorMessage(Component.text("Could not set ")
                                        .append(Component.text(player.getName())).append(Component.text(" to team "))
                                        .append(participantTeam.getDisplayName())));
                            }
                            this.openInventory(playerClicking);
                        },
                        new ButtonElement(ButtonElement.exitItem(), this::openInventory)).openInventory(player),
                new ButtonElement(ButtonElement.exitItem(), this::openInventory));
        ButtonElement addPlayerElement = new ButtonElement(addPlayerItem, addPlayerGui::openInventory);

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
        Element openPortalElement = new ButtonElement(openPortalItem,
                player -> new ConfirmationGUI(Component.text("Confirm opening portal?"),
                        player1 -> {
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
                        },
                        this::openInventory)
                        .openInventory(player));

        ItemStack hatItem = formatItem("Hat Gui", Material.DIAMOND_HELMET, 0);
        Element hatElement = new ButtonElement(hatItem, player -> {
            try {
                ListGui hatGui = new ListGui(Component.text("Select a Hat:"), teamManager.getDatabase().getPlayerHats(player.getUniqueId()), new ButtonElement(ButtonElement.exitItem(), this::openInventory));
                hatGui.openInventory(player);
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(CommandUtils.errorMessage("Error getting hats."));
            }
        });

        try {
            ListGui modelGui = new ListGui(Component.text("Model Groups:"), teamManager.getDatabase().getModelGroups(), Element.empty());
            ItemStack modelItem = formatItem("Models", Material.RED_MUSHROOM, 1);
            Element modelElement = new ButtonElement(modelItem, modelGui::openInventory);
            placeElement(9, 1, modelElement);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Models have failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Model has invalid material: " + e.getMessage());
        }

        String title = "Test Quest";

        String line1 = "This is a line";
        String line2 = "This is";
        String line3 = "another line.";

        TextComponent.Builder text = Component.text();

        try {
            text.append(Component.text(title));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(title)));
            text.append(Component.text(TextFormatter.toLine(0, line1)));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(line1)));
            text.append(Component.text(TextFormatter.toLine(1, line2)));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(line2)));
            text.append(Component.text(TextFormatter.toLine(2, line3)));

            // -15 -175
            PresetGui testQuest = new PresetGui(text.build(), -15, '\uE003', -175, 6);
            Element questOpen = new ButtonElement(NBTUtils.setUniqueID(teamManager.getDatabase().getModel(10, false).getItem(), null), testQuest::openInventory);
            placeElement(4, 1, questOpen);
        } catch (SQLException ignored) {}

        placeElement(1, 1, testElement);
        placeElement(2, 1, addPlayerElement);



        placeElement(6, 1, openPortalElement);
        placeElement(7, 1, closePortalElement);
        placeElement(8, 1, hatElement);

//        placeElement(9, 1, new ButtonElement(ButtonElement.exitItem(), player -> {
//            challengeManager.getGodManager().getGodGui().openInventory(player);
//        }));

    }
}
