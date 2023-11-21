package io.github.mctowerchallenge.mctcplugin.god.godgui;

import io.github.mctowerchallenge.mctcplugin.gui.Icons;
import io.github.mctowerchallenge.mctcplugin.gui.page.TeamGui;
import io.github.mctowerchallenge.mctcplugin.quest.QuestbookTextUtil;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mystievous.mysticore.DefaultFontInfo;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mystievous.mystigui.page.PlayerGui;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import io.github.mctowerchallenge.mctcplugin.utility.FontUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeveloperGui extends PresetGui {

    public DeveloperGui(MCTCPlugin plugin, TeamManager teamManager) {
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
            ListGui modelGui = new ListGui(plugin, Component.text("Model Groups:"), teamManager.getDatabase().getModelGroupGuis(), Element.blank());
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

        try {
            text.append(Component.text(title));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(title)));
            text.append(QuestbookTextUtil.toLine(0, line1));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(line1)));
            text.append(QuestbookTextUtil.toLine(1, line2));
            text.append(TextUtil.space(-DefaultFontInfo.getPixelLength(line2)));
            text.append(QuestbookTextUtil.toLine(2, line3));

            // -15 -175
            PresetGui testQuest = new PresetGui(plugin, text.build(), -15, FontUtils.toGuiFont("\uE003"), -175, 6);
            Element questOpen = new ButtonElement(
                    NBTUtils.applyToItemMeta(
                            teamManager.getDatabase().getModel(10, false, false).getItem(),
                            itemMeta -> NBTUtils.setUUID(Element.UUID_KEY(plugin), itemMeta, null)
                    ), testQuest::openInventory);
            placeElement(1, 4, questOpen);
        } catch (SQLException ignored) {
        }

        placeElement(1, 8, hatElement);

        ArmorTrimsGui armorTrimsGui = new ArmorTrimsGui(plugin);
        Element armorTrims = new ButtonElement(GuiUtil.formatItem("Armor Trims", Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, 0), player -> {
            armorTrimsGui.getGui(player).openInventory(player);
        });

        placeElement(6, 9, armorTrims);

    }
}
