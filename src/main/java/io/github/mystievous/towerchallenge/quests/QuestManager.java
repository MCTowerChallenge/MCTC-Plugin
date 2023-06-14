package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiHeldItem;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.Jun2023QuestManager;
import io.github.mystievous.towerchallenge.quests.npcs.Dialogue;
import io.github.mystievous.towerchallenge.quests.npcs.GodMountNPC;
import io.github.mystievous.towerchallenge.quests.npcs.NPC;
import io.github.mystievous.towerchallenge.quests.requirements.RequirementsQuest;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.timer.TowerTimer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class QuestManager implements Openable, Listener {

    public static final String GUI_ID = "questgui";

    public static final String NO_QUEST = "no-quest";

    public static final String DUB_START = "dub-start";

    public static final String STEVE = "steve";
    public static final Color STEVE_COLOR = new Color(0xf9b6f4);
    public static final String PENELOPE = "penelope";
    public static final String BUTT_STALLION = "buttstallion";
    public static final String SPIRIT = "evil-spirit";
    public static final String MYSTI = "mystievous";
    public static final String APPLE = "apple270";
    public static final String DUB = "dub";
    public static final String ENDI = "endi";
    public static final String ERIE = "erie";
    public static final String HENRY = "henry";
    public static final String PERCY = "percy";
    public static final String BONEY = "boney";

    public static final String MOOLLICIENT = "moollicient";
    public static final String PETE = "pete";
    public static final String ARI = "ari";
    public static final String SOUP = "soup";
    public static final String ALICE = "alice";
    public static final String DAVE = "dave";

    public static final QuestGui NO_QUEST_GUI = new QuestGui(TowerChallenge.getInstance(), "Quests Done!", "Enjoy the rest of the event!");

    public static final String recipeBook = "H4sIAAAAAAAA/62VzW4TMRDHHUpLGsSVCwdGe+JQVaUNBSLl0A+pXFpVFNEDRcjZnd019drBnm2aVpU4IJ6BN8h75FF4AF6AC+NNP5JU5QCJFHnt8Xz9/F9vQ4g58XBbknyPzitrhHi0WBf3VCIeF8pg7GRKrZ5TRGg+daw9bog5ktmimO/KDH1dCLFQe3IeEZ5S1IpgONjat8pbAwdaFQgbhlRiCY/McPCiPRxst2cxHpkQb/3nj++ccGUdNrU8Q9i3vQQdTBqfwx5SzsuH0tG0bQUOyky6ydU12NHKEzplMthFbc0dEd8gh5y27WjpPWxaIs1NRxfidBzOy3FXeIux6lZoXnNbnfYsxhAt/fX1W3RRe3aTufoNB/E4qKpu9sH2LMZJDhvxl1I5TCB1trhO7INJGWAEYXPzEsY2C8UE8bF1eTLOu1x5yJB88IGe5FMBo2IEaRKeumKpemL30nF4RdBTWoMn2WcPydPqwdkyy21JVRRkSTqErrMxer8MTGrzFqnmmGqODHcY87/53+PfKTVT64j78P9KSo04pcp5YgjYBZtCYXmSWZtA1xL7emZWobqidoM2lgZyq5NqqZCZiqXmbeiyfnBwqFGG4kb+nC12yI7JMjN8OsGQC7v9Fg0Hq0xBtmcx3kGy0wduis/bJCGxZONqlT3cR6wb3ttTlFf1caNoYK/MgsCmcB4iy+jY2J4BPpTAQznIUeoQlaXTRUcKGWXpw0phWVGV9EZQOXFF3GFsT9D1GZD4PSWyEaa1yxtoOHjFfb1sz2K8m03s+vxuaHUWig6H/LkMVLgsJ2O6UeJqVRVsSYO3pbbLgtJ97pz3s0YvVQVkWTLHGMIWQJLPHjrI3w3HzTfEg0T5rpb9uri/JwsU6+dRlTVqfTiPVKgpjlqp1B6XrjCNrsfo4uP1ClOsi3lSfLeKhZG5LhZkSbl1orHLvSk8saUXNTG/xRqgmvgDnBheH+IGAAA=";

    /**
     * Makes a {@link Component} that
     * displays the given items
     * in a "rewards" list format
     *
     * @param items The items to list.
     * @return The formatted {@link Component}
     */
    public static Component getRewards(ItemStack... items) {
        TextComponent.Builder builder = Component.text();
        builder.append(TextUtil.formatText("Rewards: ")).appendNewline();

        for (ItemStack item : items) {
            builder.append(TextUtil.formatText(String.format("+ %dx [", item.getAmount())).append(TextUtil.getItemName(item).hoverEvent(item.asHoverEvent()).color(NamedTextColor.WHITE)).append(Component.text("]")).appendNewline());
        }

        return builder.build();
    }

    /**
     * Formats text to look like a player's thoughts.
     *
     * @param text The message to format.
     * @return The formatted {@link Component}.
     */
    public static Component playerThoughts(Component text) {
        return TextUtil.formatText(text).decoration(TextDecoration.ITALIC, true);
    }

    /**
     * Formats text to look like a player's thoughts.
     *
     * @param text The message to format.
     * @return The formatted {@link Component}.
     */
    public static Component playerThoughts(String text) {
        return playerThoughts(Component.text(text));
    }

    /**
     * Sets a team's quest to
     * the one with the given
     * id.
     *
     * @param team    The team that's changing quests.
     * @param questId The quest to change to.
     */
    public void setTeamQuest(TowerTeam team, String questId) {
        QuestChangeEvent event = new QuestChangeEvent(team, getQuest(team, questId), getQuest(team, getTeamQuest(team)));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                teamManager.getDatabase().setTeamQuest(team, questId);
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Error setting database: " + e.getMessage());
            }
        });
    }

    /**
     * Gets a team's current quest.
     *
     * @param team The team to get.
     * @return The quest, or the
     * default no quest if the team
     * has none.
     */
    public String getTeamQuest(TowerTeam team) {
        String quest = team.getCurrentQuestTag();
        if (quest != null) {
            return quest;
        }
        return NO_QUEST;
    }

    private final Plugin plugin;
    private final TowerTimer timer;
    private final Jun2023QuestManager jun2023QuestManager;
    private final QuestItems questItems;
    private final Map<String, Quest> quests;
    private final GuiHeldItem questBook;
    private final TeamManager teamManager;

    private final Dialogue steveStartDialogue;

    /**
     * Initializes the Quest Manager.
     * <p></p>
     * This is where all quests should be created,
     * and added to {@link #quests} for the plugin
     * to register them.
     *
     * @param plugin      The current plugin instance.
     * @param timer       The event timer.
     * @param teamManager The current team manager instance.
     */
    public QuestManager(TowerChallenge plugin, TowerTimer timer, TeamManager teamManager) {
        this.plugin = plugin;
        this.timer = timer;
        this.teamManager = teamManager;
        this.questItems = new QuestItems(plugin);
        quests = new HashMap<>();

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(Component.text("Quest Book").decoration(TextDecoration.ITALIC, false));
        bookMeta.setCustomModelData(2);
        bookMeta.lore(TextUtil.formatTexts(
                Component.text("Use ").append(Component.keybind("key.use")).append(Component.text(" with me in your hand")),
                Component.text("to open the quest menu!")
        ));
        book.setItemMeta(bookMeta);
        questBook = new GuiHeldItem(plugin, GUI_ID, book, this);

        // Event Specific Quest Stuff
        jun2023QuestManager = new Jun2023QuestManager(plugin, this, teamManager);


        // Declare Quests

        Quest penelopeStart;
        Quest steveHouse;
        Quest partyInvite;
        Quest bottlePuzzle;
        Quest mysteriousTavern;
        Quest enjoy;
        Quest helpSteve;
        RequirementsQuest makePotion;

        // Create NPCs

        GodMountNPC penelope = new GodMountNPC(teamManager, "Penelope", PENELOPE, new Color(0x932172), new Color(0xbc519c));
        penelope.addAllowedRegion("penelope-.*");

        GodMountNPC buttStallion = new GodMountNPC(teamManager, "Butt Stallion", BUTT_STALLION, new Color(0x70b5a6), new Color(0x508277));
        buttStallion.addAllowedRegion("buttstallion-.*");

        NPC steve = new NPC(teamManager, "steve skellington", STEVE, new Color(0x1d4f4a), new Color(0x2b756d));
        steve.addAllowedRegion("steve-.*");

        NPC spirit = new NPC(teamManager, "Evil Spirit", SPIRIT, new Color(0x610b1f), new Color(0x870f2b));
        spirit.addDisallowedRegion(".*");

        NPC mysti = new NPC(teamManager, "Mystievous", MYSTI, new Color(0xc73858), new Color(0xd2607a));

        NPC dub = new NPC(teamManager, "Dub", DUB);
        dub.addDisallowedRegion("*");

        NPC endi = new NPC(teamManager, "Endi", ENDI);
        endi.addDisallowedRegion("*");

        NPC erie = new NPC(teamManager, "Erie", ERIE);
        erie.addDisallowedRegion("*");

        NPC henry = new NPC(teamManager, "Henry", HENRY);
        henry.addDisallowedRegion("*");

        NPC percy = new NPC(teamManager, "Percy", PERCY);
        percy.addDisallowedRegion("*");

        NPC boney = new NPC(teamManager, "Boney", BONEY);
        boney.addDisallowedRegion("*");

        NPC moollicient = new NPC(teamManager, "Moollicient", MOOLLICIENT);
        moollicient.addDisallowedRegion("*");

        NPC pete = new NPC(teamManager, "Pete", PETE);
        pete.addDisallowedRegion("*");

        NPC ari = new NPC(teamManager, "Ari", ARI);
        ari.addDisallowedRegion("*");

        NPC alice = new NPC(teamManager, "Alice", ALICE);
        alice.addDisallowedRegion("*");

        NPC dave = new NPC(teamManager, "Dave", DAVE);
        dave.addDisallowedRegion("*");

        // Quest Interaction NPCs

        Dialogue doorNoOpen = new Dialogue(teamManager, playerThoughts("The door won't open..."), 0.0);
        Consumer<PlayerInteractAtEntityEvent> doorNoOpenEvent = event -> {
            Player player = event.getPlayer();
            doorNoOpen.play(player);
        };

        // Configure Quests

        steveStartDialogue = new Dialogue(teamManager, steve.formatMessage("Hello everyone."), 2.5d);
        steveStartDialogue.append(new Dialogue(teamManager,
                steve.formatMessage("It is with a grave face that I must tell you that, due to lost and damaged equipment, the band The Withering Groove Machine will be unable to perform until further notice."),
                12.0d
        ));
        steveStartDialogue.append(new Dialogue(teamManager,
                steve.formatMessage("Please enjoy the rest of the festivities, as we await further news on the matter."),
                6.0d
        ));

        // Questbook Commands

        QuestCommands commands = new QuestCommands(this);
        plugin.getCommand("questbook").setExecutor(commands);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public QuestItems getQuestItems() {
        return questItems;
    }

    public GuiHeldItem getQuestBook() {
        return questBook;
    }

    /**
     * Gets a team's specific copy of a quest.
     *
     * @param team The team of the copy to grab.
     * @param tag  The id of the quest.
     * @return The quest object.
     */
    public @Nullable Quest getQuest(TowerTeam team, String tag) {
        if (!team.hasQuests()) {
            team.setQuests(quests);
        }
        return team.getQuest(tag);
    }

    public Jun2023QuestManager getJun2023QuestManager() {
        return jun2023QuestManager;
    }

    @Override
    public Gui getGui(Player player) {
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (team != null) {
            String currentQuestId = team.getCurrentQuestTag();
            Quest currentQuest = getQuest(team, currentQuestId);
            if (currentQuest != null) {
                return currentQuest.getGui(player);
            }
        }
        return NO_QUEST_GUI;
    }
}

