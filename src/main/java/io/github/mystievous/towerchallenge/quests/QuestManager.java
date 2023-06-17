package io.github.mystievous.towerchallenge.quests;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiHeldItem;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystimer.TimerUnsetException;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.Jun2023QuestInstance;
import io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.Jun2023QuestManager;
import io.github.mystievous.towerchallenge.quests.npcs.Dialogue;
import io.github.mystievous.towerchallenge.quests.npcs.GodMountNPC;
import io.github.mystievous.towerchallenge.quests.npcs.NPC;
import io.github.mystievous.towerchallenge.quests.utils.FullInventory;
import io.github.mystievous.towerchallenge.teams.ParticipantTeam;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.timer.TowerTimer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class QuestManager implements Openable, Listener {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final String GUI_ID = "questgui";

    public static final String NO_QUEST = "no-quest";

    // QUEST TAGS
    public static final String BAND_TROUBLE = "band-trouble";
    public static final String TUNNEL = "tunnel";
    public static final String OTHER_BAND = "other-band";
    public static final String WAIT = "wait";
    public static final String MEETING = "meeting";
    public static final String RIDDLE = "riddle";
    public static final String CAVE = "cave";
    public static final String RETURN_STICKS = "return-sticks";
    public static final String FINISHED_QUESTS = "finished-quests";

    // NPC TAGS
    public static final String STEVE = "steve";
    public static final Color STEVE_COLOR = new Color(0xf9b6f4);
    public static final String PENELOPE = "penelope";
    public static final String BUTT_STALLION = "buttstallion";
    public static final String SPIRIT = "evil-spirit";
    public static final String MYSTI = "mystievous";
    public static final String APPLE = "apple270";
    public static final String POLAR = "polar";
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

    public static final QuestGui NO_QUEST_GUI = new QuestGui(TowerChallenge.getInstance(), "No Quests!", "Enjoy the event!");

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

    private final Set<NPC> npcSet;

    private final Plugin plugin;
    private final TowerTimer timer;
    private final Jun2023QuestManager jun2023QuestManager;
    private final QuestItems questItems;
    private final Map<String, Quest> quests;
    private final GuiHeldItem questBook;
    private final TeamManager teamManager;

    private final Dialogue eventStartDialogue;
    private final Dialogue intermissionDialogue;

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
        npcSet = new HashSet<>();

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

        // Create NPCs

        GodMountNPC penelope = new GodMountNPC(teamManager, "Penelope", PENELOPE, new Color(0x932172), new Color(0xbc519c));
        penelope.addAllowedRegion("penelope-.*");
        npcSet.add(penelope);

        GodMountNPC buttStallion = new GodMountNPC(teamManager, "Butt Stallion", BUTT_STALLION, new Color(0x70b5a6), new Color(0x508277));
        buttStallion.addAllowedRegion("buttstallion-.*");
        npcSet.add(buttStallion);

        GodMountNPC mystiHorse = new GodMountNPC(teamManager, "Mystievous", "mysti-horse");

        NPC steve = new NPC(teamManager, "steve skellington", STEVE, new Color(0x399c91), new Color(0x55b4aa));
        steve.addAllowedRegion("steve-.*");
        npcSet.add(steve);

        NPC spirit = new NPC(teamManager, "Evil Spirit", SPIRIT, new Color(0x610b1f), new Color(0x870f2b));
        spirit.addDisallowedRegion(".*");
        npcSet.add(spirit);

        NPC mysti = new NPC(teamManager, "Mystievous", MYSTI, new Color(0xc73858), new Color(0xd2607a));
        npcSet.add(mysti);

        NPC polar = new NPC(teamManager, "Polar W.", POLAR, new Color(0x70c8d4), new Color(0x96e4ee));
        polar.addDisallowedRegion(".*");
        npcSet.add(polar);

        NPC endi = new NPC(teamManager, "Endi", ENDI, new Color(0xc426d9), new Color(0xde44f2));
        endi.addDisallowedRegion(".*");
        npcSet.add(endi);

        NPC erie = new NPC(teamManager, "Erie", ERIE, new Color(0x89b847), new Color(0xa5d166));
        erie.addDisallowedRegion(".*");
        npcSet.add(erie);

        NPC henry = new NPC(teamManager, "Henry", HENRY, new Color(0xa9ab6a), new Color(0xc4c58e));
        henry.addDisallowedRegion(".*");
        npcSet.add(henry);

        NPC percy = new NPC(teamManager, "Percy", PERCY, new Color(0xc63d20), new Color(0xe0573a));
        percy.addDisallowedRegion(".*");
        npcSet.add(percy);

        NPC boney = new NPC(teamManager, "Boney", BONEY, new Color(0x7694a5), new Color(0x879aa5));
        boney.addDisallowedRegion(".*");
        npcSet.add(boney);

        NPC moollicient = new NPC(teamManager, "Moollicient", MOOLLICIENT, new Color(0xffd632), new Color(0xe6c545));
        moollicient.addDisallowedRegion(".*");
        npcSet.add(moollicient);

        NPC pete = new NPC(teamManager, "Pete", PETE, new Color(0xd92521), new Color(0xbe3430));
        pete.addDisallowedRegion(".*");
        npcSet.add(pete);

        NPC ari = new NPC(teamManager, "Ari", ARI, new Color(0x118c9b), new Color(0x02a2b5));
        ari.addDisallowedRegion(".*");
        npcSet.add(ari);

        NPC soup = new NPC(teamManager, "Soup", SOUP, new Color(0xbc775d), new Color(0xd67856));
        soup.addDisallowedRegion(".*");
        npcSet.add(soup);

        NPC alice = new NPC(teamManager, "Alice", ALICE, new Color(0x0d5e84), new Color(0x006f9e));
        alice.addDisallowedRegion(".*");
        npcSet.add(alice);

        NPC dave = new NPC(teamManager, "Dave", DAVE, new Color(0x663b5a), new Color(0xca60ad));
        dave.addDisallowedRegion(".*");
        npcSet.add(dave);

        // Quest Interaction NPCs

        Dialogue doorNoOpen = new Dialogue(playerThoughts("The door won't open..."), 0.0);
        Consumer<PlayerInteractAtEntityEvent> doorNoOpenEvent = event -> {
            Player player = event.getPlayer();
            doorNoOpen.play(player);
        };

        // Configure Quests

        eventStartDialogue = new Dialogue(TextUtil.formatText("* Announcement Sound"), 3.0d)
                .setSoundKey(TowerChallenge.key("bell"));
        eventStartDialogue.append(new Dialogue(steve.formatMessage("Hello everyone."),
                3.0d
        ).setSoundKey(TowerChallenge.key("steve.hello_everyone")));
        eventStartDialogue.append(new Dialogue(
                steve.formatMessage("It is with a grave face that I must tell you that, due to lost and damaged equipment, the band The Withering Groove Machine will be unable to perform until further notice."),
                16.0d
        ).setSoundKey(TowerChallenge.key("steve.grave_face")));
        eventStartDialogue.append(new Dialogue(
                steve.formatMessage("Please enjoy the rest of the festivities, as we await further news on the matter."),
                7.5d
        ).setSoundKey(TowerChallenge.key("steve.further_news")));
        eventStartDialogue.append(new Dialogue(
                playerThoughts("Huh, sounds like something’s up with the band."),
                4.0d
        ));
        eventStartDialogue.append(new Dialogue(
                playerThoughts("Maybe I should head to the main stage by the beacons to see if they need help?"),
                4.0d
        ));

        /*

            POLAR W.

         */

        Quest bandTrouble = new Quest(plugin, teamManager, BAND_TROUBLE, "Band Trouble");
        bandTrouble.setDescription("Check out the main stage by the beacons to see what's up with the band.");

        Dialogue polarInvestigate = new Dialogue(
                polar.formatMessage("I can't believe this!"),
                2.75d);
        polarInvestigate.append(new Dialogue(
                polar.formatMessage("We spent so long getting ready for this performance and it had to just get ruined like this."),
                5.5d
        ));
        polarInvestigate.append(new Dialogue(
                polar.formatMessage("We have to clean up, fix the light that almost annihilated poor Endi and the wire for Boney’s amp, and now also apparently find Percy’s drumsticks too!"),
                11.0d
        ));
        polarInvestigate.append(new Dialogue(
                polar.formatMessage(Component.text("What're we gonna do.. what're we gonna do...").decoration(TextDecoration.ITALIC, true)),
                4.0d));
        polar.addQuestHandler(NO_QUEST, event -> {
        });
        polar.setDefaultHandler(event -> {
            TowerTeam team = teamManager.getPlayerTeam(event.getPlayer());
            if (team != null && !team.isInDialogue()) {
                team.setInDialogue(true);
                polarInvestigate.play(team, () -> team.setInDialogue(false));
            }
        });

        /*

            ENDI

         */

        String glass = "glass";
        String endiTalk = "endi-talk";

        Dialogue endiInvestigate = new Dialogue(endi.formatMessage("Oh, hi there..."), 2.5d);
        endiInvestigate.append(new Dialogue(endi.formatMessage("I'm alright now, thank you."), 3.5d));
        endiInvestigate.append(new Dialogue(endi.formatMessage("The light didn't land too close, it was just scary.."), 4.5d));
        endiInvestigate.append(new Dialogue(endi.formatMessage("I'm worried about Percy though, he seems really upset about his drumsticks."), 5.0d));
        endiInvestigate.append(new Dialogue(endi.formatMessage("I'd go check on him but steve should be back any second with a broom and new glass to fix this up... hopefully"), 7.0d));
        endiInvestigate.append(new Dialogue(endi.formatMessage("Would you mind asking how he's doing for me please?"), 3.5d));
        endi.addQuestHandler(NO_QUEST, event -> {
        });
        endi.setDefaultHandler(event -> {
            TowerTeam team = teamManager.getPlayerTeam(event.getPlayer());
            if (team != null && team.getObjective(BAND_TROUBLE, glass) == 0 && !team.isInDialogue()) {
                team.setInDialogue(true);
                team.addObjectiveScore(BAND_TROUBLE, endiTalk, 1);
                endiInvestigate.play(team, () -> team.setInDialogue(false));
            }
        });

        /*

            STEVE

         */

        String steveTalk = "steve-talk";

        ItemStack prideBrush = GuiUtil.formatItem("Brush", Material.BRUSH, 1);
        ItemMeta brushMeta = prideBrush.getItemMeta();
        brushMeta.setPlaceableKeys(new HashSet<>() {{
            add(NamespacedKey.minecraft("suspicious_sand"));
            add(NamespacedKey.minecraft("suspicious_gravel"));
            add(NamespacedKey.minecraft("sandstone"));
        }});
        brushMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        brushMeta.setUnbreakable(true);
        prideBrush.setItemMeta(brushMeta);

        Dialogue steveInvestigate = new Dialogue(steve.formatMessage("Hello my friends! And a very happy pride month to you all!"), 7.0d)
                .setSoundKey(TowerChallenge.key("steve.pride"));
        steveInvestigate.append(new Dialogue(steve.formatMessage("Ah I see Endi mentioned I was down here?"), 5.5d)
                .setSoundKey(TowerChallenge.key("steve.endi")));
        steveInvestigate.append(new Dialogue(steve.formatMessage("Well I was unable to get glass, but I did find this broom to sweep up the mess from the light!"), 6.5d)
                .setSoundKey(TowerChallenge.key("steve.no_glass")));
        steveInvestigate.append(new Dialogue(steve.formatMessage(Component.text("I may have gotten distracted by these snacks though..")), 6.5d)
                .setSoundKey(TowerChallenge.key("steve.snacks")));
        steveInvestigate.append(new Dialogue(steve.formatMessage("I don't think I'll have time to go make my own fresh or go find a seller, with everything else going on."), 7.5d)
                .setSoundKey(TowerChallenge.key("steve.fresh")));
        steveInvestigate.append(new Dialogue(steve.formatMessage("Hmmm... Would you happen to be busy?"), 5.0d)
                .setSoundKey(TowerChallenge.key("steve.busy")));
        steveInvestigate.append(new Dialogue(steve.formatMessage("If you get the chance I'd really appreciate the help in getting more glass!"), 6.0d)
                .setSoundKey(TowerChallenge.key("steve.help")));
        steveInvestigate.append(new Dialogue(steve.formatMessage("I should probably head back soon... "), 4.5d)
                .setSoundKey(TowerChallenge.key("steve.get_back_soon")));
        steveInvestigate.append(new Dialogue(steve.formatMessage("Endi tends to be quite the worry nether wart."), 5.5d)
                .setSoundKey(TowerChallenge.key("steve.endi_worry")));
        steveInvestigate.append(new Dialogue(steve.formatMessage(Component.text("Maybe one last Pitt Cola first, it's hot out there..").decoration(TextDecoration.ITALIC, true)), 8.0d)
                .setSoundKey(TowerChallenge.key("steve.one_last")));

        Dialogue steveFound = new Dialogue(steve.formatMessage("You're back already!"), 2.0d)
                .setSoundKey(TowerChallenge.key("steve.back_already"));
        steveFound.append(new Dialogue(steve.formatMessage("How long have I been here eating...?"), 3.5d)
                .setSoundKey(TowerChallenge.key("steve.how_long")));
        steveFound.append(new Dialogue(steve.formatMessage("Uhh never mind that!"), 2.25d)
                .setSoundKey(TowerChallenge.key("steve.nevermind")));
        steveFound.append(new Dialogue(steve.formatMessage("Thanks for getting the glass together for us."), 4.5d)
                .setSoundKey(TowerChallenge.key("steve.thanks")));
        steveFound.append(new Dialogue(steve.formatMessage("Hmm I don't actually seem to have much worthwhile on me..."), 6.0d)
                .setSoundKey(TowerChallenge.key("steve.worthwhile")));
        steveFound.append(new Dialogue(steve.formatMessage("How about this!"), 3.5d)
                .setSoundKey(TowerChallenge.key("steve.how_about_this")));

        Dialogue steveNotFound = new Dialogue(steve.formatMessage("Remember, I need plain pure glass!"), 5.0d)
                .setSoundKey(TowerChallenge.key("steve.pure_glass"));

        steve.addQuestHandler(NO_QUEST, event -> {
        });
        steve.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (team.getObjective(BAND_TROUBLE, steveTalk) == 0) {
                    if (!team.isInDialogue()) {
                        team.setInDialogue(true);
                        steveInvestigate.play(team, () -> {
                            team.addObjectiveScore(BAND_TROUBLE, steveTalk, 1);
                            team.setInDialogue(false);
                        });
                    }
                } else {
                    if (team.getObjective(BAND_TROUBLE, glass) == 0) {
                        ItemStack itemStack = player.getEquipment().getItem(event.getHand());
                        if (itemStack.getType().equals(Material.GLASS)) {
                            if (!team.isInDialogue()) {
                                team.setInDialogue(true);
                                itemStack.subtract(1);
                                steveFound.play(team, () -> {
                                    team.addObjectiveScore(BAND_TROUBLE, glass, 1);
                                    team.sendMessage(getRewards(prideBrush));
                                    FullInventory.givePlayerItems(player, prideBrush);
                                    team.setInDialogue(false);
                                });
                            }
                        } else {
                            if (!team.isInDialogue()) {
                                team.setInDialogue(true);
                                steveNotFound.play(team, () -> {
                                    team.setInDialogue(false);
                                });
                            }
                        }
                    } else {
                        // Team clicks after turning in item.
                    }
                }
            }
        });

        /*

            BROKEN LIGHT

         */

        lightInteracted = new HashSet<>();

        String brokenLightTag = "broken-light";

        NPC brokenLight = new NPC(teamManager, "Broken Light", brokenLightTag);

        Dialogue lightInteract = new Dialogue(playerThoughts("This light has a trail of broken glass leading from it... I should follow and see where it goes."), 6.0d);
        brokenLight.addQuestHandler(BAND_TROUBLE, event -> {
            TowerTeam team = teamManager.getPlayerTeam(event.getPlayer());
            if (team != null && !team.isInDialogue()) {
                team.setInDialogue(true);
                lightInteract.play(team, () -> {
                    lightInteracted.add(team.getTextName());
                    team.setInDialogue(false);
                });
            }
        });

        /*

            PERCY

         */

        String percyTalk = "percy-talk";
        String drumsticks = "drumsticks";

        ItemStack drumstickItem = GuiUtil.formatItem("Drumsticks", Material.STICK, 5);
        NBTUtils.setBool(plugin, drumsticks, drumstickItem);
        NBTUtils.noStack(plugin, drumstickItem);
        NBTUtils.setNoUse(drumstickItem);
        TextUtil.appendQuestItemLore(drumstickItem);
        QuestItems.putItem(drumsticks, drumstickItem);

        String drumstickIndividual = "drumstick-individual";
        ItemStack drumstickIndividualItem = GuiUtil.formatItem("Drumstick", Material.STICK, 4);
        QuestItems.putItem(drumstickIndividual, drumstickIndividualItem);

        Dialogue percyInvestigate = new Dialogue(percy.formatMessage("Man, this is such a drag."), 2.5d);
        percyInvestigate.append(new Dialogue(percy.formatMessage("Can you believe someone stole my lucky drumsticks?"), 4.0d));
        percyInvestigate.append(new Dialogue(percy.formatMessage("Those were a gift from my pops before I left home."), 3.0d));
        percyInvestigate.append(new Dialogue(
                percy.formatMessage(Component.text("They're the only ones I brought, and I highly doubt Alice would let me use ")
                        .append(Component.text("anything").decoration(TextDecoration.ITALIC, true))
                        .append(Component.text(" of hers since I took her spot in this band."))),
                7.5d
        ));
        percyInvestigate.append(new Dialogue(percy.formatMessage("Mind keeping an eye out for 'em? They're made of warped and crimson wood so should be easy to spot. 'ppreciate ya!"), 7.0d));

        Dialogue percyNotFound = new Dialogue(percy.formatMessage("Haven't spotted the drumsticks yet?"), 2.0d);
        percyNotFound.append(new Dialogue(percy.formatMessage(
                Component.text("Yeah me neither, where the ")
                        .append(Component.text("nether").decoration(TextDecoration.ITALIC, true))
                        .append(Component.text(" are they?"))),
                4.5d
        ));
        percyNotFound.append(new Dialogue(percy.formatMessage("Don't forget, they're made of crimson and warped woods!"), 4.0d));

        Dialogue percyFound = new Dialogue(percy.formatMessage("Woah! Thank you so much for finding these for me!"), 3.0d);
        percyFound.append(new Dialogue(percy.formatMessage("I had no clue how I was going to continue playing music without them."), 4.0d));
        percyFound.append(new Dialogue(percy.formatMessage("Hey, if you don't mind me asking... Where did you find these that they're all covered in sand?"), 7.0d));
        percyFound.append(new Dialogue(percy.formatMessage("Ahhhh, so Alice must have taken and hid them cause she was jealous..."), 5.5d));
        percyFound.append(new Dialogue(percy.formatMessage("Well, there's no use in being angry or getting revenge."), 4.0d));
        percyFound.append(new Dialogue(percy.formatMessage("My pops always told me, \"If you take an eye for an eye, you'll still be half blind.\""), 5.0d));
        percyFound.append(new Dialogue(percy.formatMessage("Please, take this for all your help!"), 3.5d));

        percy.addQuestHandler(NO_QUEST, event -> {
        });
        percy.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (team.getObjective(BAND_TROUBLE, percyTalk) == 0) {
                    if (!team.isInDialogue()) {
                        team.setInDialogue(true);
                        percyInvestigate.play(team, () -> {
                            team.addObjectiveScore(BAND_TROUBLE, percyTalk, 1);
                            team.setInDialogue(false);
                        });
                    }
                } else {
                    if (team.getObjective(BAND_TROUBLE, drumsticks) == 0) {
                        ItemStack item = player.getEquipment().getItem(event.getHand());
                        if (NBTUtils.boolState(plugin, drumsticks, item)) {
                            if (!team.isInDialogue()) {
                                item.subtract(1);
                                team.setInDialogue(true);
                                percyFound.play(team, () -> {
                                    team.addObjectiveScore(BAND_TROUBLE, drumsticks, 1);
                                    team.setInDialogue(false);
                                    ItemStack bundle = QuestUtil.randomBlockBundle();
                                    FullInventory.givePlayerItems(player, bundle);
                                    team.sendMessage(getRewards(bundle));
                                    setTeamQuest(team, FINISHED_QUESTS);
                                });
                            }
                        } else {
                            if (!team.isInDialogue()) {
                                team.setInDialogue(true);
                                percyNotFound.play(team, () -> {
                                    team.setInDialogue(false);
                                });
                            }
                        }
                    } else {
                        // Team clicks after turning in drumsticks.
                        // Thank team again for finding them?
                    }
                }
            }
        });

        /*

            BONEY

         */

        String boneyTalk = "boney-talk";
        String copper = "copper";

        ItemStack knife = GuiUtil.formatItem("Knife", Material.IRON_SWORD, 0);
        ItemMeta knifeMeta = knife.getItemMeta();
        knifeMeta.lore(new ArrayList<>() {{
            add(TextUtil.formatText("It's engraved with \"AW\".").decoration(TextDecoration.ITALIC, true));
            add(Component.empty());
        }});
        TextUtil.appendQuestItemLore(knifeMeta);
        knife.setItemMeta(knifeMeta);

        QuestItems.putItem("knife", knife);

        ItemStack hastePotion = GuiUtil.formatItem(TextUtil.noItalic("Potion of Haste"), Material.POTION, 0);
        PotionMeta hasteMeta = (PotionMeta) hastePotion.getItemMeta();
        hasteMeta.setColor(new Color(0xE3AC17).toBukkitColor());
        hasteMeta.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 30*20, 1, true, true), true);
        hastePotion.setItemMeta(hasteMeta);

        QuestItems.putItem("haste-potion", hastePotion);

        Dialogue boneyInvestigate = new Dialogue(boney.formatMessage("What the nether, man."), 2.5d);
        boneyInvestigate.append(new Dialogue(boney.formatMessage("This is so obviously sabotage, messed up for real."), 4.5d));
        boneyInvestigate.append(new Dialogue(boney.formatMessage("Worst part is, I'm fresh out of copper since the last time someone cut my instrument cable!"), 5.0d));
        boneyInvestigate.append(new Dialogue(boney.formatMessage(Component.text("That was a joke, this hasn't happened before...").decoration(TextDecoration.ITALIC, true)), 4.5d));
        boneyInvestigate.append(new Dialogue(boney.formatMessage("Anyways, any chance you have 10 copper ingots on you?"), 5.25d));
        boneyInvestigate.append(new Dialogue(boney.formatMessage("Well if you happen upon some spare, y'know I'll be here."), 5.5d));

        Dialogue boneyNotFound = new Dialogue(boney.formatMessage("Scrounged up the 10 copper?"), 2.5d);
        boneyNotFound.append(new Dialogue(boney.formatMessage("Well get out scrounging then!"), 3.0d));

        Dialogue boneyFound = new Dialogue(boney.formatMessage("Oh sweet you found copper, thanks guys!"), 4.0d);
        boneyFound.append(new Dialogue(boney.formatMessage("Here, since I heard you're doing that big tower challenge today too I figured this potion might help you."), 7.0d));
        boneyFound.append(new Dialogue(boney.formatMessage("Oh, would you mind also throwing this away for me? I found it on the ground over there."), 4.5d));
        Dialogue postBoneyFound = new Dialogue(playerThoughts("Woah free knife, nice!"), 3.0d);
        postBoneyFound.append(new Dialogue(playerThoughts("Looks like there's a faded engraving on it..."), 4.0d));
        postBoneyFound.append(new Dialogue(playerThoughts("MV maybe? or AW? It's hard to tell with how dirty and old it is..."), 6.0d));

        boney.addQuestHandler(NO_QUEST, event -> {
        });
        boney.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (team.getObjective(BAND_TROUBLE, boneyTalk) == 0) {
                    if (!team.isInDialogue()) {
                        team.setInDialogue(true);
                        team.addObjectiveScore(BAND_TROUBLE, boneyTalk, 1);
                        boneyInvestigate.play(team, () -> {
                            team.setInDialogue(false);
                        });
                    }
                } else {
                    if (team.getObjective(BAND_TROUBLE, copper) == 0) {
                        ItemStack itemStack = player.getEquipment().getItem(event.getHand());
                        if (itemStack.getType().equals(Material.COPPER_INGOT) && itemStack.getAmount() >= 10) {
                            if (!team.isInDialogue()) {
                                team.setInDialogue(true);
                                team.addObjectiveScore(BAND_TROUBLE, copper, 1);
                                itemStack.subtract(10);
                                boneyFound.play(team, () -> {
                                    FullInventory.givePlayerItems(player, knife, hastePotion);
                                    postBoneyFound.play(team, () -> {
                                        team.setInDialogue(false);
                                        team.sendMessage(getRewards(knife, hastePotion));
                                    });
                                });
                            }
                        } else {
                            if (!team.isInDialogue()) {
                                team.setInDialogue(true);
                                boneyNotFound.play(team, () -> {
                                    team.setInDialogue(false);
                                });
                            }
                        }
                    } else {
                        // Team clicks after turning in item.
                    }
                }
            }
        });

        /*

            PAINTING

         */

        paintingTriggered = new HashSet<>();
        paintingDialogue = new Dialogue(playerThoughts("The glass leads up to this painting?"), 3.0d);
        paintingDialogue.append(new Dialogue(playerThoughts("What could that mean?"), 2.0d));

        /*

            TUNNEL

         */

        tunnelTriggered = new HashSet<>();
        tunnelDialogue = new Dialogue(playerThoughts("A hidden tunnel? I wonder what this is for?"), 4.0d);

        quests.put(BAND_TROUBLE, bandTrouble);


        Quest tunnel = new Quest(plugin, teamManager, TUNNEL, "Odd Tunnel");
        tunnel.setDescription("Investigate the strange tunnel behind the painting.");

        /*

            TUNNEL EXIT

         */

        tunnelExitTriggered = new HashSet<>();
        tunnelExitDialogue = new Dialogue(playerThoughts("This tunnel connects the two bands' stages, I should check the other group out"), 5.0d);

        quests.put(TUNNEL, tunnel);


        Quest otherBand = new Quest(plugin, teamManager, OTHER_BAND, "Rival Band");
        otherBand.setDescription("The rival band has a tunnel leading to the main stage... You should check them out.");

        /*

            PETE

         */

        Dialogue peteStart = new Dialogue(pete.formatMessage("Oh hey, what're you doing down here?"), 3.0d);

        Dialogue peteTunnel = new Dialogue(pete.formatMessage("Oh woah hello there, way to make an entrance!"), 4.0d);
        peteTunnel.append(new Dialogue(pete.formatMessage("Didn't even know there was a tunnel behind that."), 2.5d));

        pete.addQuestHandler(NO_QUEST, playerInteractAtEntityEvent -> {
        });
        pete.addQuestHandler(BAND_TROUBLE, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (!team.isInDialogue()) {
                    team.setInDialogue(true);
                    peteStart.play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
            }
        });
        pete.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (!team.isInDialogue()) {
                    team.setInDialogue(true);
                    peteTunnel.play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
            }
        });

        /*

            MOOLLICIENT

         */

        Dialogue moolicientBlank = new Dialogue(moollicient.formatMessage("..."), 1.0d);
        Dialogue moolicientSideEye = new Dialogue(moollicient.actionMessage("gives you side eye"), 2.5d);
        moolicientSideEye.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "vine"));

        moollicient.addQuestHandler(NO_QUEST, event -> {
        });
        moollicient.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (!team.isInDialogue()) {
                    team.setInDialogue(true);
                    if (RANDOM.nextInt(10) == 0) {
                        moolicientSideEye.play(team, () -> {
                            team.setInDialogue(false);
                        });
                    } else {
                        moolicientBlank.play(team, () -> {
                            team.setInDialogue(false);
                        });
                    }
                }
            }
        });

        /*

            ALICE

         */

        Dialogue aliceSearch = new Dialogue(alice.formatMessage("Ughh where is it??"), 2.0d);
        aliceSearch.append(new Dialogue(alice.formatMessage("Where the nether is it???"), 2.0d));
        aliceSearch.append(new Dialogue(alice.formatMessage("Where did I leave that old thing?"), 3.0d));
        aliceSearch.append(new Dialogue(alice.formatMessage("Hey, do you mind?!"), 2.0d));

        alice.addQuestHandler(NO_QUEST, event -> {
        });
        alice.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (!team.isInDialogue()) {
                    team.setInDialogue(true);
                    aliceSearch.play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
            }
        });

        /*

            ARI

         */

        Dialogue[] ariLines = {
                new Dialogue(ari.formatMessage("Hi there! Hi there! Hi there!"), 1.5d),
                new Dialogue(ari.formatMessage("Wha happa? Wha happa?"), 1.5d),
                new Dialogue(ari.formatMessage("Salty treat? Salty treat?"), 1.5d),
                new Dialogue(ari.formatMessage("Yippee!"), 1.5d)
        };

        ari.addQuestHandler(NO_QUEST, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (!team.isInDialogue()) {
                    team.setInDialogue(true);
                    ariLines[RANDOM.nextInt(ariLines.length)].play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
            }
        });

        /*

            OTHER BAND MISC

         */

        stageLeaveDialogue = new Dialogue(playerThoughts("Hmmm... I couldn't find anything there."), 2.5d);
        stageLeaveDialogue.append(new Dialogue(playerThoughts("Maybe I should wait and see if anything else comes up."), 3.0d));

        stageEntered = new HashSet<>();
        stageExited = new HashSet<>();

        quests.put(OTHER_BAND, otherBand);


        Quest wait = new Quest(plugin, teamManager, WAIT, "Meow.");
        wait.setDescription("(Wait for something to happen.)");

        intermissionDialogue = new Dialogue(playerThoughts("You feel something strange in your pocket..."), 3.0d);
        intermissionDialogue.append(new Dialogue(playerThoughts("As you pull it out, you notice it's a small piece of paper with a note on it."), 5.0d));
        intermissionDialogue.append(new Dialogue(playerThoughts("It says \"Meet me at taco stand have info about band feud and drumsticks\""), 6.5d));
        intermissionDialogue.append(new Dialogue(playerThoughts("Hmmm, maybe we should go check it out..."), 3.5d));

        quests.put(WAIT, wait);


        Quest meeting = new Quest(plugin, teamManager, MEETING, "Meeting");
        meeting.setDescription("Head to the taco stand for the mysterious meeting.");

        /*

            DAVE

         */

        String noteBase64 = "H4sIAAAAAAAA/02PT0vDQBDFJ5aUGPHqechZinjsVUEQvIgIQkGmybRZk+yW3RdjEM9+Mz+Xm3ioh2H+vcdvJida0NmtQJ7VB+Ms0fl3RiemoovOWC297LAevAHUvm6da3JaQPanlB5kryEjooR+PgvoB4p18eR4Z2zFqJXd9k1L8Oh6DqrN5cZu7Etsuj7gqNq2YhtF4KFWr39qA5a4VsFqMt25WE4e77rZ00lZx+N4MKjnQZAWI8NHx4y5nxDXVxygh8CuB5v/gPmCgIkxRbAqzar4ymgpPWrnKX8YA4y+uz5+mMKgVVo+mqqKOaH0xvUWCf0CbmzokT0BAAA=";
        ItemStack noteItem = ItemStack.deserializeBytes(Base64.getDecoder().decode(noteBase64));
        BookMeta noteMeta = (BookMeta) noteItem.getItemMeta();
        noteMeta.setAuthor("Alice Wayward");
        noteItem.setItemMeta(noteMeta);

        Dialogue daveMeet = new Dialogue(dave.formatMessage("Thanks for meeting up with me, sorry for being all cryptic but I had to make sure no one sus would catch on."), 6.5d);
        daveMeet.append(new Dialogue(dave.formatMessage("I heard you were asking about what's happening over at the main stage, and I may have some info."), 5.0d));
        daveMeet.append(new Dialogue(dave.formatMessage("Earlier, I stumbled into Alice and Ari whispering about some hidden cave."), 4.0d));
        daveMeet.append(new Dialogue(dave.formatMessage("They kept giggling about it like they did something."), 3.5d));
        daveMeet.append(new Dialogue(dave.formatMessage("I found this lying around backstage and think it might have something to do with it."), 4.5d));

        dave.addQuestHandler(MEETING, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                if (!team.isInDialogue()) {
                    team.setInDialogue(true);
                    daveMeet.play(team, () -> {
                        team.setInDialogue(false);
                        FullInventory.givePlayerItems(player, noteItem);
                        team.sendMessage(getRewards(noteItem));
                        setTeamQuest(team, RIDDLE);
                    });
                }
            }
        });

        quests.put(MEETING, meeting);


        Quest riddle = new Quest(plugin, teamManager, RIDDLE, "Riddle");
        riddle.setDescription("Figure out the riddle that Dave found.");

        NPC caveEnterDoor = new NPC(teamManager, "Cave Door", Jun2023QuestInstance.ENTER_DOOR);
        caveEnterDoor.addQuestHandler(NO_QUEST, doorNoOpenEvent);
        caveEnterDoor.addQuestHandler(BAND_TROUBLE, doorNoOpenEvent);
        caveEnterDoor.addQuestHandler(TUNNEL, doorNoOpenEvent);
        caveEnterDoor.addQuestHandler(OTHER_BAND, doorNoOpenEvent);
        caveEnterDoor.addQuestHandler(WAIT, doorNoOpenEvent);
        caveEnterDoor.addQuestHandler(MEETING, doorNoOpenEvent);
        caveEnterDoor.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                Jun2023QuestInstance instance = jun2023QuestManager.getQuestInstance(team);
                if (instance != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (getTeamQuest(team).equals(RIDDLE)) {
                            setTeamQuest(team, CAVE);
                        }
                        instance.enterTeleport(player);
                    });
                }
            }
        });

        NPC caveExitDoor = new NPC(teamManager, "Cave Door", Jun2023QuestInstance.EXIT_DOOR);
        caveExitDoor.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTask(plugin, () -> {
                jun2023QuestManager.exitTeleport(player);
            });
        });

        quests.put(RIDDLE, riddle);


        Quest cave = new Quest(plugin, teamManager, CAVE, "Hidden Cave");
        cave.setDescription("Investigate the mysterious cave underneath the sand.");

        /*

            DRUMSTICKS

         */

        NPC drumstickFrame = new NPC(teamManager, "Drumsticks", drumsticks);
        drumstickFrame.setDefaultHandler(event -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Player player = event.getPlayer();
                TowerTeam team = teamManager.getPlayerTeam(player);
                if (team != null && team.getCurrentQuestTag().equals(CAVE)) {
                    Entity entity = event.getRightClicked();
                    if (entity instanceof ItemFrame itemFrame) {
                        itemFrame.setItem(new ItemStack(Material.AIR));
                    }
                    FullInventory.givePlayerItems(player, drumstickItem);
                    team.sendMessage(getRewards(drumstickItem));
                    setTeamQuest(team, RETURN_STICKS);
                }
            });
        });

        quests.put(CAVE, cave);


        Quest returnSticks = new Quest(plugin, teamManager, RETURN_STICKS, "Return Sticks");
        returnSticks.setDescription("Return Percy's drumsticks to him at the main stage!");

        quests.put(RETURN_STICKS, returnSticks);

        Quest finishedQuests = new Quest(plugin, teamManager, FINISHED_QUESTS, "Quest Done!");
        finishedQuests.setDescription("Enjoy the rest of the event!");

        quests.put(FINISHED_QUESTS, finishedQuests);

        // Questbook Commands

        QuestCommands commands = new QuestCommands(this);
        plugin.getCommand("questbook").setExecutor(commands);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static final String PAINTING_REGION = "painting";
    public static final String TUNNEL_REGION = "tunnel";
    public static final String TUNNEL_EXIT_REGION = "tunnel-exit";
    public static final String RIVAL_STAGE_REGION = "rival-stage";

    private final Set<String> lightInteracted;

    private final Dialogue paintingDialogue;
    private final Dialogue tunnelDialogue;
    private final Dialogue tunnelExitDialogue;
    private final Dialogue stageLeaveDialogue;

    private final Set<String> paintingTriggered;
    private final Set<String> tunnelTriggered;
    private final Set<String> tunnelExitTriggered;
    private final Set<String> stageEntered;
    private final Set<String> stageExited;

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (team != null) {
            if (!team.getCurrentQuestTag().equals(NO_QUEST)) {
                String teamName = team.getTextName();
                RegionManager regionManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.Jun2023()));
                if (regionManager != null) {
                    ProtectedRegion painting = regionManager.getRegion(PAINTING_REGION);
                    if (painting != null) {
                        if (painting.contains(BukkitAdapter.adapt(event.getTo()).toVector().toBlockPoint())) {
                            if (team.getCurrentQuestTag().equals(BAND_TROUBLE) && lightInteracted.contains(teamName) && !tunnelTriggered.contains(teamName) && !paintingTriggered.contains(teamName)) {
                                if (!team.isInDialogue()) {
                                    paintingTriggered.add(teamName);
                                    team.setInDialogue(true);
                                    paintingDialogue.play(team, () -> {
                                        team.setInDialogue(false);
                                    });
                                }
                            } else if (team.getCurrentQuestTag().equals(TUNNEL) && tunnelTriggered.contains(teamName) && !tunnelExitTriggered.contains(teamName)) {
                                if (!team.isInDialogue()) {
                                    tunnelExitTriggered.add(teamName);
                                    team.setInDialogue(true);
                                    tunnelExitDialogue.play(team, () -> {
                                        setTeamQuest(team, OTHER_BAND);
                                        team.setInDialogue(false);
                                    });
                                }
                            }
                        }
                    }

                    ProtectedRegion tunnelExit = regionManager.getRegion(TUNNEL_EXIT_REGION);
                    if (tunnelExit != null) {
                        if (tunnelExit.contains(BukkitAdapter.adapt(event.getTo()).toVector().toBlockPoint())) {
                            if (team.getCurrentQuestTag().equals(TUNNEL) && tunnelTriggered.contains(teamName) && !tunnelExitTriggered.contains(teamName)) {
                                if (!team.isInDialogue()) {
                                    tunnelExitTriggered.add(teamName);
                                    team.setInDialogue(true);
                                    tunnelExitDialogue.play(team, () -> {
                                        setTeamQuest(team, OTHER_BAND);
                                        team.setInDialogue(false);
                                    });
                                }
                            }
                        }
                    }

                    ProtectedRegion tunnel = regionManager.getRegion(TUNNEL_REGION);
                    if (tunnel != null) {
                        if (tunnel.contains(BukkitAdapter.adapt(event.getTo()).toVector().toBlockPoint())) {
                            if (team.getCurrentQuestTag().equals(BAND_TROUBLE) && !tunnelTriggered.contains(teamName)) {
                                if (!team.isInDialogue()) {
                                    tunnelTriggered.add(teamName);
                                    team.setInDialogue(true);
                                    tunnelDialogue.play(team, () -> {
                                        setTeamQuest(team, TUNNEL);
                                        team.setInDialogue(false);
                                    });
                                }
                            }
                        }
                    }

                    if (team.getCurrentQuestTag().equals(OTHER_BAND)) {
                        ProtectedRegion rivalStage = regionManager.getRegion(RIVAL_STAGE_REGION);
                        if (rivalStage != null) {
                            if (rivalStage.contains(BukkitAdapter.adapt(event.getTo()).toVector().toBlockPoint())) {
                                // player is walking in rival stage
                                stageEntered.add(teamName);
                            } else {
                                if (stageEntered.contains(teamName) && !stageExited.contains(teamName)) {
                                    if (!team.isInDialogue()) {
                                        stageExited.add(teamName);
                                        team.setInDialogue(true);
                                        stageLeaveDialogue.play(team, () -> {
                                            team.setInDialogue(false);
                                            setTeamQuest(team, WAIT);
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public void triggerStart() {
        try {
            timer.start(true);
        } catch (TimerUnsetException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            eventStartDialogue.play(Audience.audience(Bukkit.getOnlinePlayers()), () -> {
                List<ParticipantTeam> teams = teamManager.getParticipantTeams();
                for (ParticipantTeam team : teams) {
                    setTeamQuest(team, BAND_TROUBLE);
                }
            });
        }, 100);
    }

    private static final UUID daveUUID = UUID.fromString("209f4e33-fe27-4cd1-945a-a1f3e865a0f1");
    private static final Location foodLocation = new Location(Worlds.Jun2023(), 183.5f, 63.5f, -2174.0f, -90.0f, 12.0f);
    private static final Location stageLocation = new Location(Worlds.Jun2023(), 234.064700d, 56.000000d, -2231.980100d, 320.167236f, -26.501534f);

    public void teleportDaveStage() {
        Entity daveEntity = Bukkit.getEntity(daveUUID);

        if (daveEntity != null) {
            daveEntity.teleport(stageLocation);
        }
    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event) {
        if (event.getWorld().getName().equals(Worlds.Jun2023().getName())) {
            if (stageLocation.getChunk().load() && foodLocation.getChunk().load()) {
                Entity daveEntity = Bukkit.getEntity(daveUUID);

                if (daveEntity != null) {
                    daveEntity.teleport(stageLocation);
                }
            }
        }
    }

    public void teleportDaveTacos() {
        Entity daveEntity = Bukkit.getEntity(daveUUID);

        if (daveEntity != null) {
            daveEntity.teleport(foodLocation);
        }
    }

    public void triggerIntermission() {

        if (stageLocation.getChunk().load() && foodLocation.getChunk().load()) {
            Entity daveEntity = Bukkit.getEntity(daveUUID);

            if (daveEntity != null) {
                daveEntity.teleport(foodLocation);
            }
        }

        try {
            timer.start(true);
        } catch (TimerUnsetException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            intermissionDialogue.play(Audience.audience(Bukkit.getOnlinePlayers()), () -> {
                List<ParticipantTeam> teams = teamManager.getParticipantTeams();
                for (ParticipantTeam team : teams) {
                    setTeamQuest(team, MEETING);
                }
            });
        }, 100);

    }

    public Map<String, Quest> getQuests() {
        return quests;
    }

    public Set<NPC> getNPCs() {
        return npcSet;
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

