package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gods.GodManager;
import io.github.mystievous.towerchallenge.gods.godgui.regionteleports.WorldsRegionOverview;
import io.github.mystievous.towerchallenge.gui.GuiHeldItem;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.*;
import io.github.mystievous.towerchallenge.magic.MagicItems;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.Quest;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.teleports.TeleportHistoryManager;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import io.github.mystievous.towerchallenge.utility.Palette;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
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
    private final WorldsRegionOverview worldsRegionOverview;

    /**
     * Utility GUI for the Gods to use.
     * <p>
     * This is opened generally through the GuiHeldItem.
     */
    public GodGui(TowerChallenge plugin, @NotNull GodManager godManager, QuestManager questManager, TeleportHistoryManager teleportHistoryManager, TeamManager teamManager) {
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
        guiBook = new GuiHeldItem(GUI_ID, book, this);
        guiBook.setPermission("towerchallenge.godgui");

        ItemStack teleportHistory = new ItemStack(Material.ENDER_EYE);
        ItemMeta teleportHistoryMeta = teleportHistory.getItemMeta();
        teleportHistoryMeta.displayName(Component.text("Teleport History").decoration(TextDecoration.ITALIC, false));
        teleportHistory.setItemMeta(teleportHistoryMeta);
        ButtonElement teleportHistoryElement = new ButtonElement(teleportHistory, player -> teleportHistoryManager.getGui(player).openInventory(player));


        worldsRegionOverview = new WorldsRegionOverview();
        ItemStack regionTeleports = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta regionTeleportsMeta = regionTeleports.getItemMeta();
        regionTeleportsMeta.displayName(Component.text("Region Teleports").decoration(TextDecoration.ITALIC, false));
        regionTeleports.setItemMeta(regionTeleportsMeta);
        ButtonElement regionTeleportsElement = new ButtonElement(regionTeleports, player -> worldsRegionOverview.getGui(player).openInventory(player));

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

        PresetGui magicGui = MagicItems.getGui();
        ItemStack magicItem = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta magicMeta = magicItem.getItemMeta();
        magicMeta.displayName(TextUtil.noItalic("Magic Items"));
        magicItem.setItemMeta(magicMeta);
        ButtonElement magicElement = new ButtonElement(magicItem, magicGui::openInventory);


        ListGui dialogueGui = new ListGui(Component.text("Dialogues"), new ButtonElement(ButtonElement.backItem(), player1 -> {
            godManager.getGodGui().openInventory(player1);
        }));
        for (Dialogue dialogue : questManager.getNpcManager().getDialogues()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(TextUtil.noItalic(dialogue.getFriendlyName()));
            meta.lore(TextUtil.formatTexts("Click me to", "listen!"));
            item.setItemMeta(meta);
            ButtonElement element = new ButtonElement(item, dialogue::play);
            dialogueGui.addElement(element);
        }
        ButtonElement dialogueElement = new ButtonElement(new ItemStack(Material.BOOK) {{
            ItemMeta meta = getItemMeta();
            meta.displayName(TextUtil.noItalic("Dialogues"));
            setItemMeta(meta);
        }}, dialogueGui::openInventory);


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

        TrackedStatsGUI trackedStatsGUI = new TrackedStatsGUI(teamManager, new ButtonElement(ButtonElement.backItem(), player -> {
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

        ItemStack devItem = new ItemStack(Material.PAPER);
        ItemMeta devMeta = devItem.getItemMeta();
        devMeta.displayName(TextUtil.noItalic("Developer Menu"));
        devMeta.setCustomModelData(4);
        devItem.setItemMeta(devMeta);
        DeveloperGui devGui = new DeveloperGui(plugin, teamManager);
        Element devElement = new ButtonElement(devItem, devGui::openInventory);

//        ItemStack warning = new ItemStack(Material.RED_STAINED_GLASS_PANE);
//        ItemMeta warningMeta = warning.getItemMeta();
//        warningMeta.displayName(TextUtil.noItalic("!!WARNING!!"));
//        warning.setItemMeta(warningMeta);
//        Element warningElement = new Element(warning);

        placeElement(2, 2, regionTeleportsElement);
        placeElement(4, 2, magicElement);
        placeElement(6, 2, hatElement);
        placeElement(2, 5, startingItemsElement);
        placeElement(3, 4, dialogueElement);
        placeElement(4, 5, teleportHistoryElement);
        placeElement(5, 4, teamQuestElement);
        placeElement(6, 5, trackedStatsElement);
        placeElement(8, 1, crafting);
        placeElement(9, 1, anvil);
        placeElement(9, 2, enderChest);
        placeElement(8, 5, devElement);

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
