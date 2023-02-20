package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.eventspecific.feb2023.ValentinesUtil;
import io.github.mystievous.towerchallenge.quests.legacy.BlockVoucher;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.GuiHeldItem;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.Openable;
import io.github.mystievous.towerchallenge.quests.entities.GodMountNPC;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.Color;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class QuestManager implements Openable {

    public static final SecureRandom RANDOM = new SecureRandom();

    public static final String GUI_ID = "questgui";

    public static final String NO_QUEST = "no-quest";

    public static final String BUTTSTALLION_START = "buttstallion-start";
    public static final String INVESTIGATE_TOWER = "investigate-tower";
    public static final String PICK_TOWER_ROOM = "pick-tower-room";
    public static final String SHOOTING_GALLERY = "shooting-gallery";
    public static final String LIBRARY_MAZE = "library-maze";
    public static final String OCEAN_SEARCH = "ocean-search";
    public static final String TALK_TO_STEVE = "talk-to-steve";
    public static final String BUTTSTALLION_RETURN = "buttstallion-return";

    public static final String STEVE = "steve";
    public static final String PENELOPE = "penelope";
    public static final String BUTT_STALLION = "buttstallion";
    public static final String SPIRIT = "evil-spirit";

//    public static final PresetGui NO_QUEST_GUI = new PresetGui(Component.text("No quests!"), 3) {{
//        Element element = new Element(new ItemStack(Material.PAPER) {{
//            ItemMeta meta = getItemMeta();
//            meta.displayName(Component.text("You have no available quests!"));
//            setItemMeta(meta);
//        }});
//
//        placeElement(2, 5, element);
//
//    }};

    public static final QuestGui NO_QUEST_GUI = new QuestGui("No more quests!", "Enjoy the rest of the event!");

    public void setTeamQuest(TowerTeam team, String questId) {
        QuestChangeEvent event = new QuestChangeEvent(team, getQuest(team, questId), getQuest(team, getTeamQuest(team)));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        try {
            teamManager.getDatabase().setTeamQuest(team, questId);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error setting database: " + e.getMessage());
        }
    }

    public void setTeamQuest(Player player, String questId) {
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (team != null) {
            setTeamQuest(team, questId);
        } else {
            Bukkit.getLogger().info("What? How? Quest Manager SetTeamQuest without a team?");
        }
    }

    public String getTeamQuest(TowerTeam team) {
        String quest = team.getCurrentQuestId();
        if (quest != null) {
            return quest;
        }
        return NO_QUEST;
    }

    private final Map<String, Quest> quests;
    private final GuiHeldItem questBook;
    private final TeamManager teamManager;

    private final NPC spirit;

    public QuestManager(TowerChallenge plugin, TeamManager teamManager) {
        this.teamManager = teamManager;
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

        // Declare Quests

        Quest buttStallionStart;
        Quest investigateTower;
        Quest pickTowerRoom;
        Quest shootingGallery;
        Quest libraryMaze;
        Quest oceanSearch;
        Quest talkToSteve;
        Quest buttStallionReturn;

        // Create NPCs

        GodMountNPC penelope = new GodMountNPC(teamManager, "Penelope", PENELOPE, new Color(0x932172), new Color(0xbc519c));
        penelope.addAllowedRegion("penelope-.*");

        GodMountNPC buttStallion = new GodMountNPC(teamManager, "Butt Stallion", BUTT_STALLION, new Color(0x70b5a6), new Color(0x508277));
        buttStallion.addAllowedRegion("buttstallion-.*");

        NPC steve = new NPC(teamManager, "steve skellington", STEVE, new Color(0x1d4f4a), new Color(0x2b756d));
        steve.addAllowedRegion("steve-.*");

        spirit = new NPC(teamManager, "Evil Spirit", SPIRIT, new Color(0x610b1f), new Color(0x870f2b));
        spirit.addDisallowedRegion(".*");

        // Configure Quests

        Dialogue steveGetUpHere1 = new Dialogue(teamManager, steve.formatMessage("How the #&!% did you get up here?"), 3.0f);
        steveGetUpHere1.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.get_up_here.1"));
        Dialogue steveGetUpHere2 = new Dialogue(teamManager, steve.formatMessage("How in the blazes did you get up here?"), 3.5f);
        steveGetUpHere2.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.get_up_here.2"));
        Dialogue steveGetUpHere3 = new Dialogue(teamManager, steve.formatMessage("How the fuck did you get up here?"), 3.0f);
        steveGetUpHere3.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.get_up_here.3"));

        Dialogue steveGetUpHere4 = steveGetUpHere1.clone();
        Dialogue steveGetUpHere5 = steveGetUpHere2.clone();
        Dialogue steveGetUpHere6 = steveGetUpHere3.clone();

        Dialogue steveNotTimeYet = new Dialogue(teamManager, steve.formatMessage("It's not time yet!"), 2.5f);
        steveNotTimeYet.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.not_time_yet"));
        {
            Dialogue steveCheater = new Dialogue(teamManager, steve.formatMessage("Are you a little cheater?"), 2.5f);
            steveCheater.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.little_cheater"));
            steveNotTimeYet.setNext(steveCheater);
            Dialogue steveHowDareYou = new Dialogue(teamManager, steve.formatMessage("How dare you..."), 3.5f);
            steveHowDareYou.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.how_dare_you"));
            steveCheater.setNext(steveHowDareYou);
        }
        steveGetUpHere1.setNext(steveNotTimeYet);
        steveGetUpHere2.setNext(steveNotTimeYet);
        steveGetUpHere3.setNext(steveNotTimeYet);

        Dialogue steveDoorNotOpen = new Dialogue(teamManager, steve.formatMessage("The door isn't open yet,"), 2.75f);
        steveDoorNotOpen.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.door_not_open"));
        {
            Dialogue steveCantGetDown = new Dialogue(teamManager, steve.formatMessage("I can't get down that way."), 2.75f);
            steveCantGetDown.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.cant_get_down"));
            steveDoorNotOpen.setNext(steveCantGetDown);
            Dialogue steveOldBones = new Dialogue(teamManager, steve.formatMessage("My old bones don't hop over a fence like they used to you know!"), 4.5);
            steveOldBones.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.old_bones"));
            steveCantGetDown.setNext(steveOldBones);
        }

        steveGetUpHere4.setNext(steveDoorNotOpen);
        steveGetUpHere5.setNext(steveDoorNotOpen);
        steveGetUpHere6.setNext(steveDoorNotOpen);

        Consumer<PlayerInteractAtEntityEvent> getUpHereRandom = playerInteractAtEntityEvent -> {
            Player player = playerInteractAtEntityEvent.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;
            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                Dialogue dialogue;
                float rand = RANDOM.nextFloat();
                if (rand <= 0.05f) {
                    dialogue = steveGetUpHere3;
                } else if (rand <= 0.1f) {
                    dialogue = steveGetUpHere6;
                } else if (rand <= 0.25f) {
                    dialogue = steveGetUpHere2;
                } else if (rand <= 0.4f) {
                    dialogue = steveGetUpHere5;
                } else if (rand <= 0.7f) {
                    dialogue = steveGetUpHere1;
                } else {
                    dialogue = steveGetUpHere4;
                }
                dialogue.play(team, () -> team.setInDialogue(false));
            }
        };


        buttStallionStart = new Quest(teamManager, BUTTSTALLION_START, "Butt Stallion");
        buttStallionStart.setDescription("Go talk to Butt Stallion in the Stables to the North- East of the spawn islands!");
        quests.put(BUTTSTALLION_START, buttStallionStart);

        Key ambient = Key.key(Key.MINECRAFT_NAMESPACE, "entity.horse.ambient");
        Key angry = Key.key(Key.MINECRAFT_NAMESPACE, "entity.horse.angry");
        Key breathe = Key.key(Key.MINECRAFT_NAMESPACE, "entity.horse.breathe");

        Dialogue bsGiveQuest = new Dialogue(teamManager, buttStallion.formatMessage("Hey, you!"), 3.5f);
        bsGiveQuest.setSound(Sound.sound(angry, Sound.Source.RECORD, 1f, 1.25f));
        bsGiveQuest.setFriendlyName("Butt Stallion Give Quest");
        {
            Dialogue situation = new Dialogue(teamManager, buttStallion.formatMessage("Listen, I've got a li'l… situation here."), 6.5f);
            bsGiveQuest.setNext(situation);

            Dialogue beautiful = new Dialogue(teamManager, buttStallion.formatMessage("See, I've got this beautiful mare, Penelope. You might've met before."), 7.5);
            beautiful.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1.5f));
            situation.setNext(beautiful);

            Dialogue softens = new Dialogue(teamManager, buttStallion.formatMessage("She softens my little horse heart more than anything else in this world."), 5.5f);
            beautiful.setNext(softens);

            Dialogue question = new Dialogue(teamManager, buttStallion.formatMessage("An' I think today, I finally got the nerve to pop the question."), 7.5f);
            question.setSound(Sound.sound(breathe, Sound.Source.RECORD, 1f, 1f));
            softens.setNext(question);

            Dialogue problem = new Dialogue(teamManager, buttStallion.formatMessage("Only problem is, I left my special diamond horseshoe in the care of our friend..."), 6.0f);
            question.setNext(problem);
            Dialogue bones = new Dialogue(teamManager, buttStallion.formatMessage("the one with the bones, y'know? Sven or something?"), 6.0f);
            problem.setNext(bones);

            Dialogue fool = new Dialogue(teamManager, buttStallion.formatMessage(Component.text("But that old fool hasn't shown his face in ").append(Component.text("months.").decoration(TextDecoration.ITALIC, true))), 5.0f);
            fool.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1.25f));
            bones.setNext(fool);

            Dialogue thoughtless = new Dialogue(teamManager, buttStallion.formatMessage("And y'know, it's pretty thoughtless of him."), 3.0f);
            fool.setNext(thoughtless);
            Dialogue worried = new Dialogue(teamManager, buttStallion.formatMessage("Today was supposed to be my day with Penn, and now he’s making me go and be all worried 'bout him."), 6.0f);
            worried.setSound(Sound.sound(angry, Sound.Source.RECORD, 1f, 1.4f));
            thoughtless.setNext(worried);

            Dialogue find = new Dialogue(teamManager, buttStallion.formatMessage("I know you've got that little shindig thing going on, but if you can find that horseshoe for me I might jus' have a good day yet."), 8.5f);
            find.setSound(Sound.sound(breathe, Sound.Source.RECORD, 1f, 1f));
            worried.setNext(find);

            Dialogue tower = new Dialogue(teamManager, buttStallion.formatMessage("Penelope seemed a li'l suspicious of that tower on the hill above the fairgrounds, so you can start by goin' to check that out."), 8.0f);
            find.setNext(tower);
        }
        buttStallion.addQuestHandler(BUTTSTALLION_START, playerInteractAtEntityEvent -> {
            Player player = playerInteractAtEntityEvent.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                bsGiveQuest.play(team, () -> {
                    team.setInDialogue(false);
                    setTeamQuest(team, INVESTIGATE_TOWER);
                });
            }
        });
        steve.addQuestHandler(BUTTSTALLION_START, getUpHereRandom);


        investigateTower = new Quest(teamManager, INVESTIGATE_TOWER, "Ominous Tower");
        investigateTower.setDescription("Investigate the Tower on the hill above the Love Fair!");
        quests.put(INVESTIGATE_TOWER, investigateTower);

        Dialogue bsInvestigateTower = new Dialogue(teamManager, buttStallion.formatMessage("Hey, don't forget to check out that tower. I need my horseshoe by the end of the day!"), 0);
        bsInvestigateTower.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1f));
        buttStallion.addQuestHandler(INVESTIGATE_TOWER, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                bsInvestigateTower.play(team, () -> team.setInDialogue(false));
            }
        });
        steve.addQuestHandler(INVESTIGATE_TOWER, getUpHereRandom);

        pickTowerRoom = new Quest(teamManager, PICK_TOWER_ROOM, "Tower Rooms");
        pickTowerRoom.setDescription("Pick your next room to complete in the Tower above the Love Fair.");
        quests.put(PICK_TOWER_ROOM, pickTowerRoom);

        Dialogue bsPickRoom = new Dialogue(teamManager, buttStallion.formatMessage("Challenges? Well I can't help much but you definitely got this."), 0);
        bsPickRoom.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1f));
        buttStallion.addQuestHandler(PICK_TOWER_ROOM, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                bsPickRoom.play(team, () -> team.setInDialogue(false));
            }
        });
        steve.addQuestHandler(PICK_TOWER_ROOM, getUpHereRandom);


        shootingGallery = new Quest(teamManager, SHOOTING_GALLERY, "The Gallery");
        shootingGallery.setDescription("Complete the shooting gallery in the tower above the Love Fair.");
        quests.put(SHOOTING_GALLERY, shootingGallery);

        Dialogue bsShootingGallery = new Dialogue(teamManager, buttStallion.formatMessage("A shooting gallery? Well my days of shootin' are long past me but that don’t sound too bad."), 0);
        bsShootingGallery.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1f));
        buttStallion.addQuestHandler(SHOOTING_GALLERY, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                bsShootingGallery.play(team, () -> team.setInDialogue(false));
            }
        });
        steve.addQuestHandler(SHOOTING_GALLERY, getUpHereRandom);

        libraryMaze = new Quest(teamManager, LIBRARY_MAZE, "The Maze");
        libraryMaze.setDescription("Make your way through the maze in the Tower above the Love Fair.");
        quests.put(LIBRARY_MAZE, libraryMaze);

        Dialogue bsMaze = new Dialogue(teamManager, buttStallion.formatMessage("A maze?! Oh no no no that's not my cuppa tea there. You can handle that one on your own."), 0);
        bsMaze.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1f));
        buttStallion.addQuestHandler(LIBRARY_MAZE, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                bsMaze.play(team, () -> team.setInDialogue(false));
            }
        });
        steve.addQuestHandler(LIBRARY_MAZE, getUpHereRandom);

        oceanSearch = new Quest(teamManager, OCEAN_SEARCH, "The Sea");
        oceanSearch.setDescription("Find 9 key fragments in the Mermaid's Grove, in the tower above the Love Fair.");
        quests.put(OCEAN_SEARCH, oceanSearch);

        Dialogue bsOcean = new Dialogue(teamManager, buttStallion.formatMessage("Underwater? Well I'm not such a great swimmer myself but I'm sure you can handle yourself well."), 0);
        bsOcean.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1f));
        buttStallion.addQuestHandler(OCEAN_SEARCH, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                bsOcean.play(team, () -> team.setInDialogue(false));
            }
        });
        steve.addQuestHandler(OCEAN_SEARCH, getUpHereRandom);

        talkToSteve = new Quest(teamManager, TALK_TO_STEVE, "steve");
        talkToSteve.setDescription("Talk to steve skellington at the top of the tower above the Love Fair.");
        quests.put(TALK_TO_STEVE, talkToSteve);

        Dialogue bsTalkSteve = new Dialogue(teamManager, buttStallion.formatMessage("Who's Steve?"), 2.5f);
        bsMaze.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1f));
        {
            Dialogue bsBones = new Dialogue(teamManager, buttStallion.formatMessage("Oh, Sven! I'm glad ya found that old bag of bones."), 6.5f);
            bsTalkSteve.setNext(bsBones);

            Dialogue bsHorseshoe = new Dialogue(teamManager, buttStallion.formatMessage("Did he have my diamond horseshoe?"), 4.0f);
            bsBones.setNext(bsHorseshoe);

            Dialogue bsWhaddya = new Dialogue(teamManager, buttStallion.formatMessage("Whaddya mean you haven’t talked to him yet?! Well get back up there!"), 4.0f);
            bsWhaddya.setSound(Sound.sound(angry, Sound.Source.RECORD, 1f, 1.4f));
            bsHorseshoe.setNext(bsWhaddya);
        }
        buttStallion.addQuestHandler(TALK_TO_STEVE, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                bsTalkSteve.play(team, () -> team.setInDialogue(false));
            }
        });

        Dialogue steveTalkSteve = new Dialogue(teamManager, steve.formatMessage("Thank you, friends, for saving me."), 2.5f);
        steveTalkSteve.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.saving_me"));
        {
            Dialogue stevePickle = new Dialogue(teamManager, steve.formatMessage("That was quite a sea pickle I was in."), 3.5f);
            stevePickle.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.sea_pickle"));
            steveTalkSteve.setNext(stevePickle);

            Dialogue steveDoor = new Dialogue(teamManager, steve.formatMessage("I've been trying to open that door for months!"), 6.0f);
            steveDoor.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.open_door_for_months"));
            stevePickle.setNext(steveDoor);

            Dialogue steveForgor = new Dialogue(teamManager, steve.formatMessage("Ah! I almost forgot..."), 2.5f);
            steveForgor.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.almost_forgor"));
            steveDoor.setNext(steveForgor);

            Dialogue steveHorseshoe = new Dialogue(teamManager, steve.formatMessage("Here is the horseshoe for Butt Stallion."), 4.0f);
            steveHorseshoe.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.horseshoe"));
            steveForgor.setNext(steveHorseshoe);

            Dialogue steveShiny = new Dialogue(teamManager, steve.formatMessage("You can assure them it is still in the shiny state I got it in."), 5.5f);
            steveShiny.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.shiny_state"));
            steveHorseshoe.setNext(steveShiny);

            Dialogue steveBoney = new Dialogue(teamManager, steve.formatMessage("I would have never let anything happen to it in my boney fingers."), 6.0f);
            steveBoney.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.boney_fingers"));
            steveShiny.setNext(steveBoney);
        }
        steve.addQuestHandler(TALK_TO_STEVE, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                steveTalkSteve.play(team, () -> {
                    team.setInDialogue(false);
                    FullInventory.givePlayerItems(player, ValentinesUtil.diamondHorseshoe);
                    setTeamQuest(team, BUTTSTALLION_RETURN);
                });
            }
        });


        buttStallionReturn = new Quest(teamManager, BUTTSTALLION_RETURN, "Butt Stallion");
        buttStallionReturn.setDescription("Return to Butt Stallion with the Diamond Horseshoe from steve!");
        quests.put(BUTTSTALLION_RETURN, buttStallionReturn);

        Dialogue bsButtStallion = new Dialogue(teamManager, buttStallion.formatMessage("Oh you're finally back!"), 3.5f);
        bsButtStallion.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1.25f));
        {
            Dialogue bsHorseshoe = new Dialogue(teamManager, buttStallion.formatMessage("Thank you so much for findin' the diamond horseshoe for me!"), 5.0f);
            bsButtStallion.setNext(bsHorseshoe);

            Dialogue bsSven = new Dialogue(teamManager, buttStallion.formatMessage("Oh and for saving Sve- I mean steve, too."), 5.0f);
            bsSven.setSound(Sound.sound(breathe, Sound.Source.RECORD, 1f, 1.4f));
            bsHorseshoe.setNext(bsSven);

            Dialogue bsReward = new Dialogue(teamManager, buttStallion.formatMessage("Here take this for savin' my big day, wish me luck later!"), 5.0f);
            bsReward.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f, 1.4f));
            bsSven.setNext(bsReward);
        }
        buttStallion.addQuestHandler(BUTTSTALLION_RETURN, event -> {
            Player player = event.getPlayer();
            EquipmentSlot hand = event.getHand();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;
            ItemStack item = player.getInventory().getItem(hand);

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                if (NBTUtils.boolState(ValentinesUtil.HORSESHOE_TAG, item)) {
                    player.getInventory().setItem(hand, item.subtract(1));
                    bsButtStallion.play(team, () -> {
                        team.setInDialogue(false);
                        setTeamQuest(team, NO_QUEST);
                        FullInventory.givePlayerItems(player, ValentinesUtil.randomBlockBundle(), BlockVoucher.getVouchers(3));
                    });
                } else {
                    bsTalkSteve.play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
            }
        });

        Dialogue steveButtStallion = new Dialogue(teamManager, steve.formatMessage("You need to get that horseshoe back to Butt Stallion!"), 4.0f);
        steveButtStallion.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.back_to_buttstallion"));
        {
            Dialogue steveWorry = new Dialogue(teamManager, steve.formatMessage("Don't worry about me friends, I will get down on my own now that the door is open."), 7.0f);
            steveWorry.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.on_my_own"));
            steveButtStallion.setNext(steveWorry);
        }
        steve.addQuestHandler(BUTTSTALLION_RETURN, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                steveButtStallion.play(team, () -> team.setInDialogue(false));
            }
        });

        QuestCommands commands = new QuestCommands(this, teamManager);
        plugin.getCommand("questbook").setExecutor(commands);
    }

    public NPC getSpirit() {
        return spirit;
    }

    public GuiHeldItem getQuestBook() {
        return questBook;
    }

    public @Nullable Quest getQuest(TowerTeam team, String tag) {
        if (!team.hasQuests()) {
            team.setQuests(quests);
        }
        return team.getQuest(tag);
    }

    @Override
    public Gui getGui(Player player) {
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (team != null) {
            String currentQuestId = team.getCurrentQuestId();
            Quest currentQuest = getQuest(team, currentQuestId);
            if (currentQuest != null) {
                return currentQuest.getGui(player);
            }
        }
        return NO_QUEST_GUI;
    }
}
