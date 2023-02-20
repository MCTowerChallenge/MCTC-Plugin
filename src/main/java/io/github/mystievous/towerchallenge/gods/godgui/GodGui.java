package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.FerrisWheel;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.EvilTowerManager;
import io.github.mystievous.towerchallenge.gods.GodManager;
import io.github.mystievous.towerchallenge.gods.godgui.regionteleports.WorldsRegionOverview;
import io.github.mystievous.towerchallenge.gui.GuiHeldItem;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.*;
import io.github.mystievous.towerchallenge.magic.MagicItems;
import io.github.mystievous.towerchallenge.timer.Timer;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.Quest;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.teleports.TeleportHistoryManager;
import io.github.mystievous.towerchallenge.teams.ParticipantTeam;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.Palette;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * Utility GUI for the Gods to use.
 * <p>
 * This is opened generally through the GuiHeldItem.
 */
public class GodGui extends PresetGui implements Openable {

    public static final String TEXT_NAME = "God Menu";
    public static final String GUI_ID = "godmenu";
    public static final Component COMPONENT_NAME = Component.text(TEXT_NAME);
    private static final int ROWS = 6;

    private final GuiHeldItem guiBook;

    /**
     * Utility GUI for the Gods to use.
     * <p>
     * This is opened generally through the GuiHeldItem.
     */
    public GodGui(TowerChallenge plugin, Timer timer, @NotNull GodManager godManager, FerrisWheel ferrisWheel, QuestManager questManager, TeleportHistoryManager teleportHistoryManager, TeamManager teamManager, EvilTowerManager evilTowerManager) {
        super(COMPONENT_NAME, ROWS);
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(Component.text("God Menu").decoration(TextDecoration.ITALIC, false));
        bookMeta.lore(new ArrayList<>() {{
            add(Component.text("Right click with me in your hand").decoration(TextDecoration.ITALIC, false).color(Palette.PRIMARY.toTextColor()));
            add(Component.text("to open the god menu!").decoration(TextDecoration.ITALIC, false).color(Palette.PRIMARY.toTextColor()));
        }});
        bookMeta.setCustomModelData(1);
        book.setItemMeta(bookMeta);
        guiBook = new GuiHeldItem(plugin, GUI_ID, book, this);
        guiBook.setPermission("towerchallenge.godgui");


        ItemStack teleportHistory = new ItemStack(Material.ENDER_EYE);
        ItemMeta teleportHistoryMeta = teleportHistory.getItemMeta();
        teleportHistoryMeta.displayName(Component.text("Teleport History").decoration(TextDecoration.ITALIC, false));
        teleportHistory.setItemMeta(teleportHistoryMeta);
        ButtonElement teleportHistoryElement = new ButtonElement(teleportHistory, player -> teleportHistoryManager.getGui(player).openInventory(player));


        Component startingItemsTitle = Component.text("Pick team to view items");
        BiConsumer<Player, TowerTeam> startingItemsBiconsumer = (player, team) -> {
            (new StartingItemsGui((ParticipantTeam) team)).openInventory(player);
        };
        TeamGui startingItemsTeamGui = new TeamGui(startingItemsTitle,
                new ArrayList<>(),
                teamManager.getParticipantTeams().stream().map(participantTeam -> (TowerTeam) participantTeam).toList(),
                startingItemsBiconsumer,
                new ButtonElement(ButtonElement.backItem(), player1 -> {
                    godManager.getGodGui().openInventory(player1);
                }));
        ItemStack startingItemsItem = new ItemStack(Material.PAPER);
        ItemMeta startingItemsMeta = startingItemsItem.getItemMeta();
        startingItemsMeta.displayName(TextUtil.noItalic("Starter Items"));
        startingItemsMeta.setCustomModelData(6);
        startingItemsItem.setItemMeta(startingItemsMeta);
        ButtonElement startingItemsElement = new ButtonElement(startingItemsItem, startingItemsTeamGui::openInventory);


        PresetGui magicGui = MagicItems.getGui();
        ItemStack magicItem = new ItemStack(Material.PAPER);
        ItemMeta magicMeta = magicItem.getItemMeta();
        magicMeta.displayName(TextUtil.noItalic("Magic Items"));
        magicMeta.setCustomModelData(13);
        magicItem.setItemMeta(magicMeta);
        ButtonElement magicElement = new ButtonElement(magicItem, magicGui::openInventory);


        TeamGui teamQuestGUI = new TeamGui(Component.text("Pick team to view quest:"), team -> {
            String teamQuest = questManager.getTeamQuest(team);
            Quest quest = questManager.getQuest(team, teamQuest);
            if (quest != null) {
                return TextUtil.formatTexts("Current Quest: ", quest.getId());
            } else {
                return TextUtil.formatTexts("No Current Quest");
            }
        }, teamManager.getParticipantTeams().stream().map(TowerTeam.class::cast).toList(), (player, team) -> {
            String teamQuest = questManager.getTeamQuest(team);
            Quest quest = questManager.getQuest(team, teamQuest);
            if (quest != null) {
                quest.getGui(team).openInventory(player);
            } else {
                QuestManager.NO_QUEST_GUI.openInventory(player);
            }
        }, new ButtonElement(ButtonElement.backItem(), player -> {
            godManager.getGodGui().openInventory(player);
        }));
        ButtonElement teamQuestElement = new ButtonElement(new ItemStack(Material.BOOK) {{
            ItemMeta bookMeta = getItemMeta();
            bookMeta.displayName(Component.text("Quest Menu").decoration(TextDecoration.ITALIC, false));
            bookMeta.setCustomModelData(2);
            setItemMeta(bookMeta);
        }}, teamQuestGUI::openInventory);


        ButtonElement anvil = new ButtonElement(new ItemStack(Material.ANVIL), player -> {
            player.openAnvil(null, true);
        });

        ButtonElement enderChest = new ButtonElement(new ItemStack(Material.ENDER_CHEST), player -> {
            player.openInventory(player.getEnderChest());
        });

        ButtonElement crafting = new ButtonElement(new ItemStack(Material.CRAFTING_TABLE), player -> {
            player.openWorkbench(null, true);
        });

        ItemStack hatItem = formatItem("Hat Gui", Material.PAPER, 11);
        Element hatElement = new ButtonElement(hatItem, player -> {
            try {
                ListGui hatGui = new ListGui(Component.text("Select a Hat:"), teamManager.getDatabase().getPlayerHats(player.getUniqueId()), new ButtonElement(ButtonElement.exitItem(), this::openInventory));
                hatGui.openInventory(player);
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(CommandUtils.errorMessage("Error getting hats."));
            }
        });

        ItemStack devItem = new ItemStack(Material.PAPER);
        ItemMeta devMeta = devItem.getItemMeta();
        devMeta.displayName(TextUtil.noItalic("Developer Menu"));
        devMeta.setCustomModelData(4);
        devItem.setItemMeta(devMeta);
        DeveloperGui devGui = new DeveloperGui(plugin, teamManager, evilTowerManager);
        Element devElement = new ButtonElement(devItem, devGui::openInventory);

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

        ItemStack netherItem = formatItem("Nether Portal", Material.PAPER, 9);
        ChoiceGUI netherGui = new ChoiceGUI(Component.text("Nether Portal"),
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
                        this::openInventory).openInventory(player),
                player -> {
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
                }
        );
        ButtonElement netherPortal = new ButtonElement(netherItem, netherGui::openInventory);

        ItemStack endItem = formatItem("Reset End Portal", Material.PAPER, 10);
        ConfirmationGUI endGui = new ConfirmationGUI(Component.text("Confirm RESETTING end portal?"), player -> teamManager.resetEndPortal(), this::openInventory);
        ButtonElement endPortal = new ButtonElement(endItem, endGui::openInventory);

        ItemStack ferrisItem = formatItem("Reload Ferris Wheel", Material.PAPER, 14);
        ConfirmationGUI ferrisGui = new ConfirmationGUI(Component.text("Will kick all players off of ferris wheel."), player -> ferrisWheel.reload(), this::openInventory);
        ButtonElement ferrisElement = new ButtonElement(ferrisItem, ferrisGui::openInventory);

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
                        new ButtonElement(ButtonElement.exitItem(), this::openInventory)).openInventory(player),
                new ButtonElement(ButtonElement.exitItem(), this::openInventory));
        ButtonElement addPlayerElement = new ButtonElement(addPlayerItem, addPlayerGui::openInventory);

        ButtonElement evilTower = new ButtonElement(new ItemStack(Material.PAPER) {{
            ItemMeta meta = getItemMeta();
            meta.setCustomModelData(15);
            meta.displayName(TextUtil.noItalic("Evil Tower Manager"));
            setItemMeta(meta);
        }}, player -> evilTowerManager.getGui(player).openInventory(player));

        ItemStack eventItem = formatItem("Event Logistics", Material.PAPER, 5);
        PresetGui eventGui = new PresetGui(Component.text("Event Logistics"), 4);



        // Add Player to Team
        // End Portal
        // Ferris Wheel Reload
        // Hat Menu
        // Magic Items
        // Model Browser
        // Nether Portal
        // Starter Items

        // Dev Menu
        // Logistics Menu
        // Tower Menu


        placeElement(1, 1, crafting);
        placeElement(1, 2, anvil);
        placeElement(1, 3, enderChest);
        placeElement(1, 4, Element.blankSlot);
        placeElement(1, 5, Element.blankSlot);
        placeElement(1, 6, Element.blankSlot);
        placeElement(1, 7, devElement);
        placeElement(1, 8, Element.blankSlot);
        placeElement(1, 9, new ButtonElement(ButtonElement.exitItem(), HumanEntity::closeInventory));

        for (int i = 1; i <= 9; i++) {
            placeElement(2, i, Element.blankSlot);
        }

        placeElement(3, 1, Element.blankSlot);
        placeElement(3, 2, teleportHistoryElement);
        placeElement(3, 3, Element.blankSlot);
        placeElement(3, 4, netherPortal);
        placeElement(3, 5, Element.blankSlot);
        placeElement(3, 6, ferrisElement);
        placeElement(3, 7, Element.blankSlot);
        placeElement(3, 8, hatElement);
        placeElement(3, 9, Element.blankSlot);

        placeElement(4, 1, Element.blankSlot);
        placeElement(4, 2, startingItemsElement);
        placeElement(4, 3, Element.blankSlot);
        placeElement(4, 4, Element.blankSlot);
        placeElement(4, 5, Element.blankSlot);
        placeElement(4, 6, evilTower);
        placeElement(4, 7, Element.blankSlot);
        placeElement(4, 8, magicElement);
        placeElement(4, 9, Element.blankSlot);

        placeElement(5, 1, Element.blankSlot);
        placeElement(5, 2, addPlayerElement);
        placeElement(5, 3, Element.blankSlot);
        placeElement(5, 4, endPortal);
        placeElement(5, 5, Element.blankSlot);
        placeElement(5, 6, Element.blankSlot);
        placeElement(5, 7, Element.blankSlot);
        try {
            ListGui modelGui = new ListGui(Component.text("Model Groups:"), teamManager.getDatabase().getModelGroups(), Element.empty());
            ItemStack modelItem = formatItem("Models", Material.PAPER, 12);
            Element modelElement = new ButtonElement(modelItem, modelGui::openInventory);
            placeElement(5, 8, modelElement);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Models have failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Model has invalid material: " + e.getMessage());
        }
        placeElement(5, 9, Element.blankSlot);

        for (int i = 1; i <= 9; i++) {
            placeElement(6, i, Element.blankSlot);
        }

    }

    /**
     * Gets the GuiHeldItem that
     * opens the God GUI
     */
    public GuiHeldItem getGuiHeldItem() {
        return guiBook;
    }

    /**
     * Gets the ItemStack from
     * the GuiHeldItem that opens
     * the God GUI
     */
    public ItemStack getItem() {
        return guiBook.getItem();
    }

    @Override
    public Gui getGui(Player player) {
        return this;
    }
}
