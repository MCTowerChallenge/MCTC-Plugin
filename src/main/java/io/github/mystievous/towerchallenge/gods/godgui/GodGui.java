package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiHeldItem;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.*;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.gods.GodManager;
import io.github.mystievous.towerchallenge.gui.Icons;
import io.github.mystievous.towerchallenge.gui.page.TeamGui;
import io.github.mystievous.towerchallenge.magic.MagicItems;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.teams.ParticipantTeam;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.teleports.TeleportHistoryManager;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
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
    public GodGui(TowerChallenge plugin, @NotNull GodManager godManager, QuestManager questManager, TeleportHistoryManager teleportHistoryManager, TeamManager teamManager, MagicItems magicItems) {
        super(plugin, COMPONENT_NAME, ROWS);
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
        BiConsumer<Player, TowerTeam> startingItemsBiconsumer = (player, team) -> (new StartingItemsGui(plugin, (ParticipantTeam) team)).openInventory(player);
        TeamGui startingItemsTeamGui = new TeamGui(plugin, startingItemsTitle,
                new ArrayList<>(),
                teamManager.getParticipantTeams().stream().map(participantTeam -> (TowerTeam) participantTeam).toList(),
                startingItemsBiconsumer,
                new ButtonElement(Icons.backItem(), player1 -> godManager.getGodGui().openInventory(player1)));
        ItemStack startingItemsItem = new ItemStack(Material.PAPER);
        ItemMeta startingItemsMeta = startingItemsItem.getItemMeta();
        startingItemsMeta.displayName(TextUtil.noItalic("Starter Items"));
        startingItemsMeta.setCustomModelData(6);
        startingItemsItem.setItemMeta(startingItemsMeta);
        ButtonElement startingItemsElement = new ButtonElement(startingItemsItem, startingItemsTeamGui::openInventory);


        Gui magicGui = magicItems.getGui(null);
        ItemStack magicItem = new ItemStack(Material.PAPER);
        ItemMeta magicMeta = magicItem.getItemMeta();
        magicMeta.displayName(TextUtil.noItalic("Magic Items"));
        magicMeta.setCustomModelData(13);
        magicItem.setItemMeta(magicMeta);
        ButtonElement magicElement = new ButtonElement(magicItem, magicGui::openInventory);


        ButtonElement anvil = new ButtonElement(new ItemStack(Material.ANVIL), player -> player.openAnvil(null, true));

        ButtonElement enderChest = new ButtonElement(new ItemStack(Material.ENDER_CHEST), player -> player.openInventory(player.getEnderChest()));

        ButtonElement crafting = new ButtonElement(new ItemStack(Material.CRAFTING_TABLE), player -> player.openWorkbench(null, true));

        ItemStack hatItem = GuiUtil.formatItem("Hat Gui", Material.PAPER, 11);
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

        ItemStack devItem = new ItemStack(Material.PAPER);
        ItemMeta devMeta = devItem.getItemMeta();
        devMeta.displayName(TextUtil.noItalic("Developer Menu"));
        devMeta.setCustomModelData(4);
        devItem.setItemMeta(devMeta);
        DeveloperGui devGui = new DeveloperGui(plugin, teamManager);
        Element devElement = new ButtonElement(devItem, devGui::openInventory);

        /*
            Bottom to top,
            Negative to positive Z
            portal block boundaries
         */
        Vector[][] portalBlocks = new Vector[][]{
                {new Vector(-690, 67, -2414), new Vector(-690, 69, -2413)}
        };

        ItemStack netherItem = GuiUtil.formatItem("Nether Portal", Material.PAPER, 9);
        ChoiceGUI netherGui = new ChoiceGUI(plugin, Component.text("Nether Portal"),
                player -> new ConfirmationGUI(plugin, Component.text("Confirm opening portal?"),
                        player1 -> {
                            for (Vector[] layer : portalBlocks) {
                                for (int x = layer[0].getBlockX(); x <= layer[1].getBlockX(); x++) {
                                    for (int y = layer[0].getBlockY(); y <= layer[1].getBlockY(); y++) {
                                        for (int z = layer[0].getBlockZ(); z <= layer[1].getBlockZ(); z++) {
                                            Location location = new Location(Worlds.Apr2023(), x, y, z);
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
                                    Location location = new Location(Worlds.Apr2023(), x, y, z);
                                    Block block = location.getBlock();
                                    block.setType(Material.AIR);
                                }
                            }
                        }
                    }
                }
        );
        ButtonElement netherPortal = new ButtonElement(netherItem, netherGui::openInventory);

        ItemStack endItem = GuiUtil.formatItem("Reset End Portal", Material.PAPER, 10);
        ConfirmationGUI endGui = new ConfirmationGUI(plugin, Component.text("Confirm RESETTING end portal?"), player -> teamManager.resetEndPortal(), this::openInventory);
        ButtonElement endPortal = new ButtonElement(endItem, endGui::openInventory);

//        ItemStack ferrisItem = GuiUtil.formatItem("Reload Ferris Wheel", Material.PAPER, 14);
//        ConfirmationGUI ferrisGui = new ConfirmationGUI(plugin, Component.text("Will kick all players off of ferris wheel."), player -> ferrisWheel.reload(), this::openInventory);
//        ButtonElement ferrisElement = new ButtonElement(ferrisItem, ferrisGui::openInventory);

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

//        ButtonElement evilTower = new ButtonElement(new ItemStack(Material.PAPER) {{
//            ItemMeta meta = getItemMeta();
//            meta.setCustomModelData(15);
//            meta.displayName(TextUtil.noItalic("Evil Tower Manager"));
//            setItemMeta(meta);
//        }}, player -> evilTowerManager.getGui(player).openInventory(player));

        ItemStack questBook = questManager.getQuestBook().getItem();
        ButtonElement questItems = new ButtonElement(questBook, player -> questManager.getQuestItems().getGui(player).openInventory(player));

        ItemStack intermissionItem = GuiUtil.formatItem("WARNING WARNING WARNING", Material.END_CRYSTAL, 0);
        ItemMeta intermissionMeta = intermissionItem.getItemMeta();
        intermissionMeta.lore(TextUtil.formatTexts("Triggers intermission", "", "PLEASE FOR THE LOVE", "OF EVERYTHING, KNOW", "WHAT YOU'RE DOING :panik:"));
        intermissionItem.setItemMeta(intermissionMeta);

        ConfirmationGUI intermissionGui = new ConfirmationGUI(plugin, Component.text("Start intermission?"),
                confirmPlayer -> {
                    questManager.triggerIntermission();
                    confirmPlayer.closeInventory();
                },
                denyPlayer -> {
                    denyPlayer.closeInventory();
                }
        );
        ButtonElement intermission = new ButtonElement(intermissionItem, intermissionGui::openInventory);

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
        placeElement(1, 4, Icons.blankSlot);
        placeElement(1, 5, Icons.blankSlot);
        placeElement(1, 6, Icons.blankSlot);
        placeElement(1, 7, devElement);
        placeElement(1, 8, Icons.blankSlot);
        placeElement(1, 9, new ButtonElement(Icons.exitItem(), HumanEntity::closeInventory));

        for (int i = 1; i <= 9; i++) {
            placeElement(2, i, Icons.blankSlot);
        }

        placeElement(3, 1, Icons.blankSlot);
        placeElement(3, 2, teleportHistoryElement);
        placeElement(3, 3, Icons.blankSlot);
        placeElement(3, 4, netherPortal);
        placeElement(3, 5, Icons.blankSlot);
        placeElement(3, 6, questItems);
        placeElement(3, 7, Icons.blankSlot);
        placeElement(3, 8, hatElement);
        placeElement(3, 9, Icons.blankSlot);

        placeElement(4, 1, Icons.blankSlot);
        placeElement(4, 2, startingItemsElement);
        placeElement(4, 3, Icons.blankSlot);
        placeElement(4, 4, Icons.blankSlot);
        placeElement(4, 5, Icons.blankSlot);
        placeElement(4, 6, Icons.blankSlot);
        placeElement(4, 7, Icons.blankSlot);
        placeElement(4, 8, magicElement);
        placeElement(4, 9, Icons.blankSlot);

        placeElement(5, 1, Icons.blankSlot);
        placeElement(5, 2, addPlayerElement);
        placeElement(5, 3, Icons.blankSlot);
        placeElement(5, 4, endPortal);
        placeElement(5, 5, Icons.blankSlot);
        placeElement(5, 6, intermission);
        placeElement(5, 7, Icons.blankSlot);
        try {
            ListGui modelGui = new ListGui(plugin, Component.text("Model Groups:"), teamManager.getDatabase().getModelGroups(), Element.blank());
            ItemStack modelItem = GuiUtil.formatItem("Models", Material.PAPER, 12);
            Element modelElement = new ButtonElement(modelItem, modelGui::openInventory);
            placeElement(5, 8, modelElement);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Models have failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Model has invalid material: " + e.getMessage());
        }
        placeElement(5, 9, Icons.blankSlot);

        for (int i = 1; i <= 9; i++) {
            placeElement(6, i, Icons.blankSlot);
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
