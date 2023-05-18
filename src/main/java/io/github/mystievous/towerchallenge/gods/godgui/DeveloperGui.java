package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.mysticore.DefaultFontInfo;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.ConfirmationGUI;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mystievous.mystigui.page.PlayerGui;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.gui.Icons;
import io.github.mystievous.towerchallenge.gui.page.TeamGui;
import io.github.mystievous.towerchallenge.quests.TextFormatter;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
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
        super(plugin, Component.text("Developer Menu"), 6);

        ItemStack listTest = new ItemStack(Material.PAPER);
        ItemMeta listTestMeta = listTest.getItemMeta();
        listTestMeta.displayName(TextUtil.noItalic("Prefilled ListGUI for testing"));
        listTest.setItemMeta(listTestMeta);
        List<Element> testElements = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            Element randomElement = new Element(new ItemStack(Material.PAPER));
            testElements.add(randomElement);
        }
        ListGui testGui = new ListGui(plugin, Component.text("Test Gui"), testElements, new ButtonElement(Icons.exitItem(), this::openInventory));
        Element testElement = new ButtonElement(listTest, testGui::openInventory);

        ItemStack addPlayerItem = new ItemStack(Material.PAPER);
        ItemMeta addPlayerMeta = addPlayerItem.getItemMeta();
        addPlayerMeta.displayName(TextUtil.noItalic("Add Player to Team"));
        addPlayerMeta.setCustomModelData(3);
        addPlayerItem.setItemMeta(addPlayerMeta);
        TeamGui addPlayerGui = new TeamGui(
                plugin,
                Component.text("Team to add Player to:"),
                team -> new ArrayList<>(),
                teamManager.getAllTeams(),
                (player, participantTeam) -> new PlayerGui(plugin, Component.text("Pick player to add:"),
                        offlinePlayer -> TextUtil.formatTexts(Component.empty()), Arrays.stream(Bukkit.getOfflinePlayers()).toList(),
                        (playerClicking, playerSelected) -> {
                            if (teamManager.setPlayerTeam(playerSelected, participantTeam)) {
                                assert playerSelected.getName() != null;
                                playerClicking.sendMessage(Component.text(playerSelected.getName()).append(Component.text(" set to team ")).append(participantTeam.getDisplayName()));
                                Bukkit.getScheduler().runTaskAsynchronously(plugin, teamManager::loadPlayers);
                            } else {
                                playerClicking.sendMessage(CommandUtils.errorMessage(Component.text("Could not set ")
                                        .append(Component.text(playerSelected.getName())).append(Component.text(" to team "))
                                        .append(participantTeam.getDisplayName())));
                            }
                            this.openInventory(playerClicking);
                        },
                        new ButtonElement(Icons.exitItem(), this::openInventory)).openInventory(player),
                new ButtonElement(Icons.exitItem(), this::openInventory));
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

        ItemStack hatItem = GuiUtil.formatItem("Hat Gui", Material.DIAMOND_HELMET, 0);
        Element hatElement = new ButtonElement(hatItem, player -> {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    ListGui hatGui = new ListGui(plugin, Component.text("Select a Hat:"), teamManager.getDatabase().getPlayerHats(player.getUniqueId()), new ButtonElement(Icons.exitItem(), this::openInventory));
                    Bukkit.getScheduler().runTask(plugin, () -> hatGui.openInventory(player));
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.sendMessage(CommandUtils.errorMessage("Error getting hats."));
                }
            });
        });

        try {
            ListGui modelGui = new ListGui(plugin, Component.text("Model Groups:"), teamManager.getDatabase().getModelGroups(), Element.blank());
            ItemStack modelItem = GuiUtil.formatItem("Models", Material.RED_MUSHROOM, 1);
            Element modelElement = new ButtonElement(modelItem, modelGui::openInventory);
            placeElement(1, 9, modelElement);
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

        placeElement(1, 1, testElement);
        placeElement(1, 2, addPlayerElement);
//        placeElement(1, 3, new ButtonElement(new ItemStack(Material.OBSIDIAN) {{
//            ItemMeta meta = getItemMeta();
//            meta.displayName(TextUtil.noItalic("Evil Tower Manager"));
//            setItemMeta(meta);
//        }}, player -> evilTowerManager.getGui(player).openInventory(player)));

        try {
            text.append(Component.text(title));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(title)));
            text.append(Component.text(TextFormatter.toLine(0, line1)));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(line1)));
            text.append(Component.text(TextFormatter.toLine(1, line2)));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(line2)));
            text.append(Component.text(TextFormatter.toLine(2, line3)));

            // -15 -175
            PresetGui testQuest = new PresetGui(plugin, text.build(), -15, '\uE003', -175, 6);
            Element questOpen = new ButtonElement(NBTUtils.setUniqueID(plugin, teamManager.getDatabase().getModel(10, false, false).getItem(), null), testQuest::openInventory);
            placeElement(1, 4, questOpen);
        } catch (SQLException ignored) {
        }

        placeElement(1, 8, hatElement);

    }
}
