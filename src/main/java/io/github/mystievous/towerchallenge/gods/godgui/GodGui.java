package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gods.GodManager;
import io.github.mystievous.towerchallenge.gods.godgui.regionteleports.WorldsRegionOverview;
import io.github.mystievous.towerchallenge.gui.GuiHeldItem;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.*;
import io.github.mystievous.towerchallenge.magic.MagicItems;
import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.Quest;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.towering.TowerListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GodGui extends PresetGui implements Openable {

    public static final String TEXT_NAME = "God Menu";
    public static final String GUI_ID = "godmenu";
    public static final Component COMPONENT_NAME = Component.text(TEXT_NAME);
    private static final int ROWS = 6;

    private GuiHeldItem guiBook;
    private WorldsRegionOverview worldsRegionOverview;

    public GodGui(ChallengeManager challengeManager, GodManager godManager, TowerListener towerListener) {
        super(COMPONENT_NAME, ROWS);
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(Component.text("God Menu").decoration(TextDecoration.ITALIC, false));
        bookMeta.lore(new ArrayList<>(){{
            add(Component.text("Right click with me in your hand").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR));
            add(Component.text("to open the god menu!").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR));
        }});
        bookMeta.setCustomModelData(1);
        book.setItemMeta(bookMeta);
        guiBook = new GuiHeldItem(GUI_ID, book, this);


        ItemStack teleportHistory = new ItemStack(Material.ENDER_EYE);
        ItemMeta teleportHistoryMeta = teleportHistory.getItemMeta();
        teleportHistoryMeta.displayName(Component.text("Teleport History").decoration(TextDecoration.ITALIC, false));
        teleportHistory.setItemMeta(teleportHistoryMeta);
        ButtonElement teleportHistoryElement = new ButtonElement(teleportHistory, player -> challengeManager.getTeleportHistoryManager().getGui(player).openInventory(player));


        worldsRegionOverview = new WorldsRegionOverview();
        ItemStack regionTeleports = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta regionTeleportsMeta = regionTeleports.getItemMeta();
        regionTeleportsMeta.displayName(Component.text("Region Teleports").decoration(TextDecoration.ITALIC, false));
        regionTeleports.setItemMeta(regionTeleportsMeta);
        ButtonElement regionTeleportsElement = new ButtonElement(regionTeleports, player -> worldsRegionOverview.getGui(player).openInventory(player));


        TeamGui startingItemsTeamGui = new TeamGui(Component.text("Pick team to view items"), new ArrayList<>(), towerListener.getTeams().values().stream().toList(), (player, participantTeam) -> {
            (new StartingItemsGui(participantTeam)).openInventory(player);
        }, new ButtonElement(ButtonElement.backItem(), player1 -> {
            godManager.getGodGui().openInventory(player1);
        }));
        ItemStack startingItemsItem = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta startingItemsMeta = startingItemsItem.getItemMeta();
        startingItemsMeta.displayName(TextUtil.noItalic("Starting Items"));
        startingItemsItem.setItemMeta(startingItemsMeta);
        ButtonElement startingItemsElement = new ButtonElement(startingItemsItem, startingItemsTeamGui::openInventory);


//        RegionListGui regionListGui = new RegionListGui(godManager, towerListener);
//        ItemStack regionSetupItem = new ItemStack(Material.WOODEN_AXE);
//        ItemMeta regionSetupMeta = regionSetupItem.getItemMeta();
//        regionSetupMeta.displayName(Component.text("Region Setup").decoration(TextDecoration.ITALIC, false));
//        regionSetupItem.setItemMeta(regionSetupMeta);
//        ButtonElement regionSetupElement = new ButtonElement(regionSetupItem, player -> regionListGui.getGui(player).openInventory(player));
//        placeElement(1, 6, regionSetupElement);

//        ItemStack bottle = BottleManager.getEmpty();
//        ButtonElement bottleElement = new ButtonElement(bottle, player -> {
//            player.getInventory().addItem(BottleManager.getEmpty());
//        });
//        placeElement(4, 1, bottleElement);

        PresetGui magicGui = MagicItems.getGui();
        ItemStack magicItem = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta magicMeta = magicItem.getItemMeta();
        magicMeta.displayName(TextUtil.noItalic("Magic Items"));
        magicItem.setItemMeta(magicMeta);
        ButtonElement magicElement = new ButtonElement(magicItem, magicGui::openInventory);


        ListGui dialogueGui = new ListGui(Component.text("Dialogues"), new ButtonElement(ButtonElement.backItem(), player1 -> {
            godManager.getGodGui().openInventory(player1);
        }));
        for (Dialogue dialogue : challengeManager.getQuestManager().getNpcManager().getDialogues()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(TextUtil.noItalic(dialogue.getFriendlyName()));
            meta.lore(TextUtil.formatTexts("Click me to", "listen!"));
            item.setItemMeta(meta);
            ButtonElement element = new ButtonElement(item, dialogue::play);
            dialogueGui.addElement(element);
        }
        ButtonElement dialogueElement = new ButtonElement(new ItemStack(Material.BOOK){{
            ItemMeta meta = getItemMeta();
            meta.displayName(TextUtil.noItalic("Dialogues"));
            setItemMeta(meta);
        }}, dialogueGui::openInventory);


        TeamGui teamQuestGUI = new TeamGui(Component.text("Pick team to view quest:"), team -> {
            String teamQuest = QuestManager.getTeamQuest(team);
            Quest quest = challengeManager.getQuestManager().getQuest(team, teamQuest);
            if (quest != null) {
                return TextUtil.formatTexts("Current Quest: ", quest.getId());
            } else {
                return TextUtil.formatTexts("No Current Quest");
            }
        }, challengeManager.getTowerListener().getTeams().values(), (player, team) -> {
            String teamQuest = QuestManager.getTeamQuest(team);
            Quest quest = challengeManager.getQuestManager().getQuest(team, teamQuest);
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

        TrackedStatsGUI trackedStatsGUI = new TrackedStatsGUI(new ButtonElement(ButtonElement.backItem(), player -> {
            godManager.getGodGui().openInventory(player);
        }));
        ItemStack trackedStatsItem = new ItemStack(Material.BEACON);
        ItemMeta trackedStatsMeta = trackedStatsItem.getItemMeta();
        trackedStatsMeta.displayName(TextUtil.noItalic("Stat Tracker"));
        trackedStatsItem.setItemMeta(trackedStatsMeta);
        ButtonElement trackedStatsElement = new ButtonElement(trackedStatsItem, trackedStatsGUI::openInventory);

        ButtonElement anvil = new ButtonElement(new ItemStack(Material.ANVIL), player -> {
            player.openAnvil(null, true);
        });

        ButtonElement enderChest = new ButtonElement(new ItemStack(Material.ENDER_CHEST), player -> {
            player.openInventory(player.getEnderChest());
        });

        ButtonElement crafting = new ButtonElement(new ItemStack(Material.CRAFTING_TABLE), player -> {
            player.openWorkbench(null, true);
        });


        ItemStack spiritMeltItem = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta spiritMeltMeta = spiritMeltItem.getItemMeta();
        spiritMeltMeta.displayName(TextUtil.noItalic("Spirit Melt Dialogue"));
        spiritMeltItem.setItemMeta(spiritMeltMeta);
        ButtonElement meltElement = new ButtonElement(spiritMeltItem, player -> {
            new ConfirmationGUI(Component.text("Spirit Melt Dialogue"), player1 -> {
                challengeManager.getQuestManager().getNpcManager().spiritMelt();
            }, player1 -> {
                godManager.getGodGui().openInventory(player1);
            }).openInventory(player);
        });

        ItemStack spiritFinalItem = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta spiritFinalMeta = spiritFinalItem.getItemMeta();
        spiritFinalMeta.displayName(TextUtil.noItalic("Spirit Final Dialogue"));
        spiritFinalItem.setItemMeta(spiritFinalMeta);
        ButtonElement finalElement = new ButtonElement(spiritFinalItem, player -> {
            new ConfirmationGUI(Component.text("Spirit Final Dialogue"), player1 -> {
                challengeManager.getQuestManager().getNpcManager().spiritFinale();
            }, player1 -> {
                godManager.getGodGui().openInventory(player1);
            }).openInventory(player);
        });

        ItemStack nothing = new ItemStack(Material.GLOW_INK_SAC);
        ItemMeta nothingMeta = nothing.getItemMeta();
        nothingMeta.displayName(TextUtil.noItalic("This doesn't do anything"));
        nothingMeta.lore(TextUtil.formatTexts("I just needed to balance", "out the gui"));
        nothing.setItemMeta(nothingMeta);
        Element nothingElement = new Element(nothing);

        ItemStack warning = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta warningMeta = warning.getItemMeta();
        warningMeta.displayName(TextUtil.noItalic("!!WARNING!!"));
        warning.setItemMeta(warningMeta);
        Element warningElement = new Element(warning);

        placeElement(2, 2, regionTeleportsElement);
        placeElement(4, 2, magicElement);
        placeElement(6, 2, nothingElement);
        placeElement(2, 5, startingItemsElement);
        placeElement(3,4, dialogueElement);
        placeElement(4, 5, teleportHistoryElement);
        placeElement(5, 4, teamQuestElement);
        placeElement(6, 5, trackedStatsElement);
        placeElement(8, 1, crafting);
        placeElement(9, 1, anvil);
        placeElement(9, 2, enderChest);
        placeElement(9, 4, warningElement);
        placeElement(8, 4, warningElement);
        placeElement(8, 5, warningElement);
        placeElement(8, 6, warningElement);
        placeElement(9, 5, meltElement);
        placeElement(9, 6, finalElement);

    }

    public GuiHeldItem getGuiHeldItem() {
        return guiBook;
    }

    public ItemStack getItem() {
        return guiBook.getItem();
    }

    @Override
    public Gui getGui(Player player) {
        return this;
    }
}
