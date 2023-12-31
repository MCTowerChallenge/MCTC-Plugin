package io.github.mctowerchallenge.mctcplugin.god.godgui;

import io.github.mctowerchallenge.mctcplugin.GameFlowManager;
import io.github.mctowerchallenge.mctcplugin.eventspecific.oct2023.quest.Oct2023QuestManager;
import io.github.mctowerchallenge.mctcplugin.gui.Icons;
import io.github.mctowerchallenge.mctcplugin.gui.page.TeamGui;
import io.github.mctowerchallenge.mctcplugin.magic.MagicItems;
import io.github.mctowerchallenge.mctcplugin.quest.Quest;
import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTableOfContents;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiHeldItem;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.*;
import io.github.mctowerchallenge.mctcplugin.decoration.customblock.CustomBlockManager;
import io.github.mctowerchallenge.mctcplugin.god.GodManager;
import io.github.mctowerchallenge.mctcplugin.portal.PortalControllers;
import io.github.mctowerchallenge.mctcplugin.teleport.TeleportHistoryManager;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * Utility GUI for the Gods to use.
 * This GUI is opened generally through the GuiHeldItem.
 */
public class GodGui extends PresetGui implements Openable {

    public static final String TEXT_NAME = "God Menu";
    public static final String GUI_ID = "godmenu";
    public static final Component COMPONENT_NAME = Component.text(TEXT_NAME);
    private static final int ROWS = 6;

    private final GuiHeldItem guiBook;

    /**
     * Initializes a new instance of the GodGui class.
     *
     * @param plugin                The main TowerChallenge plugin instance.
     * @param godManager            The manager for God-related functionalities.
     * @param gameFlowManager       The manager for controlling the flow of the game.
     * @param questManager          The manager for quests in the game.
     * @param oct2023QuestManager   The manager for October 2023 event-specific quests.
     * @param teleportHistoryManager The manager for teleport history.
     * @param teamManager           The manager for player teams.
     * @param magicItems            The manager for magic items.
     * @param portalControllers     The manager for controlling portals.
     */
    public GodGui(JavaPlugin plugin, @NotNull GodManager godManager, GameFlowManager gameFlowManager, CustomBlockManager customBlockManager, QuestManager questManager, Oct2023QuestManager oct2023QuestManager, TeleportHistoryManager teleportHistoryManager, TeamManager teamManager, MagicItems magicItems, PortalControllers portalControllers) {
        super(plugin, COMPONENT_NAME, ROWS);

        /*
            God Book, the item given to access this gui
         */
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

        /*
            GUI for seeing all players' teleport history,
            so that we can teleport them back to a
            previous location if needed.
         */
        ItemStack teleportHistory = new ItemStack(Material.ENDER_EYE);
        ItemMeta teleportHistoryMeta = teleportHistory.getItemMeta();
        teleportHistoryMeta.displayName(Component.text("Teleport History").decoration(TextDecoration.ITALIC, false));
        teleportHistory.setItemMeta(teleportHistoryMeta);
        ButtonElement teleportHistoryElement = new ButtonElement(teleportHistory, player -> teleportHistoryManager.getGui(player).openInventory(player));

        /*
            Shows the starting items for the selected team,
            used for giving extra shulkers at the end
         */
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

        /*
            GUI with all the magic items,
            equivalent to `/wand`
         */
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

        /*
            Shows GUI with all the hats for
            the player that opens it.
            Equivalent to `/hat`
         */
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

        /*
            Opens the Developer Menu,
            a GUI for placing misc items
            used for testing
         */
        ItemStack devItem = new ItemStack(Material.PAPER);
        ItemMeta devMeta = devItem.getItemMeta();
        devMeta.displayName(TextUtil.noItalic("Developer Menu"));
        devMeta.setCustomModelData(4);
        devItem.setItemMeta(devMeta);
        DeveloperGui devGui = new DeveloperGui(plugin, teamManager);
        Element devElement = new ButtonElement(devItem, devGui::openInventory);

        /*
            Opens/closes the nether portal
            based on the portalBlocks above.
         */
        ItemStack netherItem = GuiUtil.formatItem("Nether Portal", Material.PAPER, 9);
        ChoiceGUI netherGui = new ChoiceGUI(plugin, Component.text("Nether Portal"),
                player -> new ConfirmationGUI(plugin, Component.text("Confirm opening portal?"),
                        player1 -> portalControllers.getNetherPortal().openPortal(),
                        this::openInventory).openInventory(player),
                player -> portalControllers.getNetherPortal().resetPortal()
        );
        ButtonElement netherPortal = new ButtonElement(netherItem, netherGui::openInventory);

        /*
            Controls End Portal
         */
        ItemStack endItem = GuiUtil.formatItem("Reset End Portal", Material.PAPER, 10);
        ConfirmationGUI endGui = new ConfirmationGUI(plugin, Component.text("Confirm RESETTING end portal?"), player -> portalControllers.getEndPortal().resetPortal(), this::openInventory);
        ButtonElement endButton = new ButtonElement(endItem, endGui::openInventory);

        /*
            Allows setting what team a player is assigned to.
         */
        ItemStack addPlayerItem = new ItemStack(Material.PAPER);
        ItemMeta addPlayerMeta = addPlayerItem.getItemMeta();
        addPlayerMeta.displayName(TextUtil.noItalic("Add Player to Team"));
        addPlayerMeta.setCustomModelData(3);
        addPlayerItem.setItemMeta(addPlayerMeta);
        ButtonElement addPlayerElement = new ButtonElement(addPlayerItem, player -> {
            new SetTeamGui(plugin, teamManager, Arrays.stream(Bukkit.getOfflinePlayers()).toList(), this).openInventory(player);
        });

        /*
            GUI with all the special Items for the quests
            so that gods can give them if a team loses them
            or it bugs.
         */
        ItemStack questBook = GuiUtil.formatItem("Quest Manager", Material.BOOK, 2);
        ButtonElement questButton = new ButtonElement(questBook, player -> {
            PresetGui questGui = new PresetGui(plugin, Component.text("Quest Manager"), 3);

            ItemStack questItemsIcon = GuiUtil.formatItem("Quest Items", Material.PAPER, 6);
            ButtonElement questItems = new ButtonElement(questItemsIcon, player1 -> questManager.getQuestItems().getGui(player).openInventory(player));
            questGui.placeElement(2, 2, questItems);

            ItemStack totem = GuiUtil.formatItem("Quest Manager", Material.TOTEM_OF_UNDYING, 0);
            ButtonElement questUtil = new ButtonElement(totem, player1 -> oct2023QuestManager.getGui(player).openInventory(player));
            questGui.placeElement(2, 4, questUtil);

            ItemStack questBook2 = GuiUtil.formatItem("See Team Quest", Material.BOOK, 2);
            ButtonElement getQuest = new ButtonElement(questBook2, player1 -> {
                new TeamGui(plugin, Component.text("Select team to get the quest of:"), team -> {
                    Quest currentQuest = team.getCurrentQuest();
                    if (currentQuest != null) {
                        return TextUtil.formatTexts("Current: " + currentQuest.getFriendlyName());
                    } else {
                        return TextUtil.formatTexts("Current: None");
                    }
                }, teamManager.getAllTeams(), (clickingPlayer, selectedTeam) -> {
                    new QuestTableOfContents(plugin, "Quest Book", "Investigate the rooms in the haunted house.", selectedTeam).openInventory(clickingPlayer);
                }, new ButtonElement(Icons.backItem(), questGui::openInventory)).openInventory(player1);
            });
            questGui.placeElement(3, 6, getQuest);

            ItemStack eventStart = GuiUtil.formatItem("Start Event", Material.REINFORCED_DEEPSLATE, 0);
            ButtonElement eventStartElement = new ButtonElement(eventStart, player1 -> {
                new ConfirmationGUI(plugin, Component.text("!!! Confirm STARTING THE EVENT !!!").color(NamedTextColor.RED), player2 -> {
                    gameFlowManager.startEvent();
                }, questGui::openInventory).openInventory(player1);
            });

            questGui.placeElement(1, 9, eventStartElement);

            ItemStack endIntermission = GuiUtil.formatItem("End Intermission", Material.BEDROCK, 0);
            ButtonElement endIntermissionElement = new ButtonElement(endIntermission, player1 -> {
                new ConfirmationGUI(plugin, Component.text("!!! Confirm ENDING INTERMISSION !!!").color(NamedTextColor.RED), player2 -> {
                    gameFlowManager.endIntermission();
                }, questGui::openInventory).openInventory(player1);
            });

            questGui.placeElement(3, 9,endIntermissionElement);

            questGui.openInventory(player);
        });

        ItemStack customBlockIcon = GuiUtil.formatItem("Custom Blocks", Material.BOOK, 0);
        GuiHeldItem customBlockItem = new GuiHeldItem(plugin, CustomBlockManager.CUSTOM_BLOCK_TAG, customBlockIcon, customBlockManager);
        ButtonElement customBlockElement = new ButtonElement(customBlockIcon, player -> player.getInventory().addItem(customBlockItem.getItem()));

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
        placeElement(3, 6, questButton);
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
//        placeElement(5, 4, endButton);
        placeElement(5, 4, Icons.blankSlot);
        placeElement(5, 5, Icons.blankSlot);
        placeElement(5, 6, customBlockElement);
        placeElement(5, 7, Icons.blankSlot);
        try {
            /*
                GUI with all the custom models in the database,
                grouped by modelgroups
             */
            ListGui modelGui = new ListGui(plugin, Component.text("Model Groups:"), teamManager.getDatabase().getModelGroupGuis(), Element.blank());
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
     * Gets the GuiHeldItem that opens the God GUI.
     *
     * @return The GuiHeldItem instance.
     */
    public GuiHeldItem getGuiHeldItem() {
        return guiBook;
    }

    /**
     * Gets the ItemStack from the GuiHeldItem that opens the God GUI.
     *
     * @return The ItemStack associated with the GuiHeldItem.
     */
    public ItemStack getItem() {
        return guiBook.getItem();
    }

    @Override
    public Gui getGui(Player player) {
        return this;
    }
}
