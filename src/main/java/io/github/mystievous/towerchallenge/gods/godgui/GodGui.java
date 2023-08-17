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
import io.github.mystievous.towerchallenge.quests.Quest;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.teams.ParticipantTeam;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.teleports.TeleportHistoryManager;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

import javax.swing.*;
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
            Portal layers:
            Negative to Positive for all axes
         */
        Vector[][] portalBlocks = new Vector[][]{
                {new Vector(141, 69, -2222), new Vector(141, 71, -2222)}
        };

        /*
            Opens/closes the nether portal
            based on the portalBlocks above.
         */
        ItemStack netherItem = GuiUtil.formatItem("Nether Portal", Material.PAPER, 9);
        ChoiceGUI netherGui = new ChoiceGUI(plugin, Component.text("Nether Portal"),
                player -> new ConfirmationGUI(plugin, Component.text("Confirm opening portal?"),
                        player1 -> {
                            for (Vector[] layer : portalBlocks) {
                                for (int x = layer[0].getBlockX(); x <= layer[1].getBlockX(); x++) {
                                    for (int y = layer[0].getBlockY(); y <= layer[1].getBlockY(); y++) {
                                        for (int z = layer[0].getBlockZ(); z <= layer[1].getBlockZ(); z++) {
                                            Location location = new Location(Worlds.Jun2023(), x, y, z);
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
                                    Location location = new Location(Worlds.Jun2023(), x, y, z);
                                    Block block = location.getBlock();
                                    block.setType(Material.AIR);
                                }
                            }
                        }
                    }
                }
        );
        ButtonElement netherPortal = new ButtonElement(netherItem, netherGui::openInventory);

        /*
            Controls End Portal
         */
        ItemStack endItem = GuiUtil.formatItem("Reset End Portal", Material.PAPER, 10);
        ConfirmationGUI endGui = new ConfirmationGUI(plugin, Component.text("Confirm RESETTING end portal?"), player -> teamManager.resetEndPortal(), this::openInventory);
        ButtonElement endPortal = new ButtonElement(endItem, endGui::openInventory);

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

//        ButtonElement evilTower = new ButtonElement(new ItemStack(Material.PAPER) {{
//            ItemMeta meta = getItemMeta();
//            meta.setCustomModelData(15);
//            meta.displayName(TextUtil.noItalic("Evil Tower Manager"));
//            setItemMeta(meta);
//        }}, player -> evilTowerManager.getGui(player).openInventory(player));

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
            ButtonElement questUtil = new ButtonElement(totem, player1 -> questManager.getJun2023QuestManager().getGui(player).openInventory(player));
            questGui.placeElement(2, 4, questUtil);

            ItemStack writableBook = GuiUtil.formatItem("Set Team Quest", Material.WRITABLE_BOOK, 0);
            ButtonElement setQuest = new ButtonElement(writableBook, player1 -> {
                new TeamGui(plugin, Component.text("Select team to set the quest of:"), team -> {
                    Quest currentQuest = questManager.getQuest(team, team.getCurrentQuestTag());
                    if (currentQuest != null) {
                        return TextUtil.formatTexts("Current: " + currentQuest.getFriendlyName());
                    } else {
                        return TextUtil.formatTexts("Current: None");
                    }
                }, teamManager.getAllTeams(), (clickingPlayer, selectedTeam) -> {
                    new TargetListGui<>(plugin, Component.text("Select a quest to set for " + selectedTeam.getTextName() + ":"), quest -> {
                        ItemStack item = GuiUtil.formatItem(quest.getFriendlyName(), Material.WRITABLE_BOOK, 0);
                        ItemMeta meta = item.getItemMeta();
                        meta.lore(TextUtil.formatTexts(quest.getId()));
                        item.setItemMeta(meta);
                        return item;
                    }, questManager.getQuests().values().stream().toList(), (questClickPlayer, questClicked) -> {
                        questManager.setTeamQuest(selectedTeam, questClicked.getId());
                    }, new ButtonElement(Icons.backItem())).openInventory(clickingPlayer);
                }, new ButtonElement(Icons.backItem(), questGui::openInventory)).openInventory(player1);
            });
            questGui.placeElement(1, 6, setQuest);

            ItemStack questBook2 = GuiUtil.formatItem("See Team Quest", Material.BOOK, 2);
            ButtonElement getQuest = new ButtonElement(questBook2, player1 -> {
                new TeamGui(plugin, Component.text("Select team to get the quest of:"), team -> {
                    Quest currentQuest = questManager.getQuest(team, team.getCurrentQuestTag());
                    if (currentQuest != null) {
                        return TextUtil.formatTexts("Current: " + currentQuest.getFriendlyName());
                    } else {
                        return TextUtil.formatTexts("Current: None");
                    }
                }, teamManager.getAllTeams(), (clickingPlayer, selectedTeam) -> {
                    Quest quest = questManager.getQuest(selectedTeam, questManager.getTeamQuest(selectedTeam));
                    if (quest != null) {
                        quest.getGui(clickingPlayer).openInventory(clickingPlayer);
                    }
                }, new ButtonElement(Icons.backItem(), questGui::openInventory)).openInventory(player1);
            });
            questGui.placeElement(3, 6, getQuest);

            ItemStack strider = GuiUtil.formatItem("Reset Dave", Material.STRIDER_SPAWN_EGG, 0);
            ButtonElement resetDave = new ButtonElement(strider, player1 -> questManager.teleportDaveStage());
            questGui.placeElement(1, 8, resetDave);

            ItemStack eventStart = GuiUtil.formatItem("Start Event", Material.REINFORCED_DEEPSLATE, 0);
            ButtonElement eventStartElement = new ButtonElement(eventStart, player1 -> {
                new ConfirmationGUI(plugin, Component.text("!!! Confirm STARTING THE EVENT !!!").color(NamedTextColor.RED), player2 -> {
                    questManager.triggerStart();
                }, player2 -> {
                    questGui.openInventory(player2);
                }).openInventory(player1);
            });

            questGui.placeElement(1, 9, eventStartElement);

            ItemStack strider2 = GuiUtil.formatItem("Taco Dave", Material.STRIDER_SPAWN_EGG, 0);
            ButtonElement tacoDave = new ButtonElement(strider2, player1 -> questManager.teleportDaveTacos());
            questGui.placeElement(3, 8, tacoDave);

            ItemStack endIntermission = GuiUtil.formatItem("End Intermission", Material.BEDROCK, 0);
            ButtonElement endIntermissionElement = new ButtonElement(endIntermission, player1 -> {
                new ConfirmationGUI(plugin, Component.text("!!! Confirm ENDING INTERMISSION !!!").color(NamedTextColor.RED), player2 -> {
                    questManager.triggerIntermission();
                }, player2 -> {
                    questGui.openInventory(player2);
                }).openInventory(player1);
            });

            questGui.placeElement(3, 9,endIntermissionElement);

            questGui.openInventory(player);
        });

//        ItemStack clock = GuiUtil.formatItem("Timer Controls", Material.CLOCK, )

        /*
            Button to trigger the intermission sequence.
         */
//        ItemStack intermissionItem = GuiUtil.formatItem("WARNING WARNING WARNING", Material.END_CRYSTAL, 0);
//        ItemMeta intermissionMeta = intermissionItem.getItemMeta();
//        intermissionMeta.lore(TextUtil.formatTexts("Triggers intermission", "", "PLEASE FOR THE LOVE", "OF EVERYTHING, KNOW", "WHAT YOU'RE DOING :panik:"));
//        intermissionItem.setItemMeta(intermissionMeta);
//
//        ConfirmationGUI intermissionGui = new ConfirmationGUI(plugin, Component.text("Start intermission?"),
//                confirmPlayer -> {
//                    questManager.triggerIntermission();
//                    confirmPlayer.closeInventory();
//                },
//                denyPlayer -> {
//                    denyPlayer.closeInventory();
//                }
//        );
//        ButtonElement intermission = new ButtonElement(intermissionItem, intermissionGui::openInventory);


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
        placeElement(5, 4, endPortal);
        placeElement(5, 5, Icons.blankSlot);
        placeElement(5, 6, Icons.blankSlot);
        placeElement(5, 7, Icons.blankSlot);
        try {
            /*
                GUI with all the custom models in the database,
                grouped by modelgroups
             */
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
