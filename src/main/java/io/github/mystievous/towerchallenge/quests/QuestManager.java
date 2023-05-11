package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiHeldItem;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystimer.TimerUnsetException;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.*;
import io.github.mystievous.towerchallenge.quests.entities.GodMountNPC;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.quests.legacy.BlockVoucher;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.timer.TowerTimer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class QuestManager implements Openable, Listener {

    public static final String GUI_ID = "questgui";

    public static final String NO_QUEST = "no-quest";

    public static final String PENELOPE_START = "penelope-start";
    public static final String STEVE_HOUSE = "steve-house";
    public static final String PARTY_INVITE = "party-invite";
    public static final String BOTTLE_PUZZLE = "bottle-puzzle";
    public static final String RESTORED_TAVERN = "restored-tavern";
    public static final String ENJOY = "enjoy";
    public static final String HELP_STEVE = "help-steve";
    public static final String MAKE_POTION = "make-potion";

    public static final String ANTIDOTE_TAG = "antidote";

    public static final String STEVE = "steve";
    public static final Color STEVE_COLOR = new Color(0xf9b6f4);
    public static final String PENELOPE = "penelope";
    public static final String BUTT_STALLION = "buttstallion";
    public static final String SPIRIT = "evil-spirit";
    public static final String MYSTI = "mystievous";
    public static final String APPLE = "apple270";

    public static final QuestGui NO_QUEST_GUI = new QuestGui(TowerChallenge.getInstance(), "Quests Done!", "Enjoy the rest of the event!");

    public static final String recipeBook = "H4sIAAAAAAAA/62VzW4TMRDHHUpLGsSVCwdGe+JQVaUNBSLl0A+pXFpVFNEDRcjZnd019drBnm2aVpU4IJ6BN8h75FF4AF6AC+NNP5JU5QCJFHnt8Xz9/F9vQ4g58XBbknyPzitrhHi0WBf3VCIeF8pg7GRKrZ5TRGg+daw9bog5ktmimO/KDH1dCLFQe3IeEZ5S1IpgONjat8pbAwdaFQgbhlRiCY/McPCiPRxst2cxHpkQb/3nj++ccGUdNrU8Q9i3vQQdTBqfwx5SzsuH0tG0bQUOyky6ydU12NHKEzplMthFbc0dEd8gh5y27WjpPWxaIs1NRxfidBzOy3FXeIux6lZoXnNbnfYsxhAt/fX1W3RRe3aTufoNB/E4qKpu9sH2LMZJDhvxl1I5TCB1trhO7INJGWAEYXPzEsY2C8UE8bF1eTLOu1x5yJB88IGe5FMBo2IEaRKeumKpemL30nF4RdBTWoMn2WcPydPqwdkyy21JVRRkSTqErrMxer8MTGrzFqnmmGqODHcY87/53+PfKTVT64j78P9KSo04pcp5YgjYBZtCYXmSWZtA1xL7emZWobqidoM2lgZyq5NqqZCZiqXmbeiyfnBwqFGG4kb+nC12yI7JMjN8OsGQC7v9Fg0Hq0xBtmcx3kGy0wduis/bJCGxZONqlT3cR6wb3ttTlFf1caNoYK/MgsCmcB4iy+jY2J4BPpTAQznIUeoQlaXTRUcKGWXpw0phWVGV9EZQOXFF3GFsT9D1GZD4PSWyEaa1yxtoOHjFfb1sz2K8m03s+vxuaHUWig6H/LkMVLgsJ2O6UeJqVRVsSYO3pbbLgtJ97pz3s0YvVQVkWTLHGMIWQJLPHjrI3w3HzTfEg0T5rpb9uri/JwsU6+dRlTVqfTiPVKgpjlqp1B6XrjCNrsfo4uP1ClOsi3lSfLeKhZG5LhZkSbl1orHLvSk8saUXNTG/xRqgmvgDnBheH+IGAAA=";

    private final Dialogue steveFind;
    private final Dialogue intermissionDialogue;

    public static Component getRewards(ItemStack... items) {
        TextComponent.Builder builder = Component.text();
        builder.append(TextUtil.formatText("Rewards: ")).appendNewline();

        for (ItemStack item : items) {
            builder.append(TextUtil.formatText(String.format("+ %dx [", item.getAmount())).append(TextUtil.getItemName(item).hoverEvent(item.asHoverEvent()).color(NamedTextColor.WHITE)).append(Component.text("]")).appendNewline());
        }

        return builder.build();
    }

    public static Component playerThoughts(Component text) {
        return TextUtil.formatText(text).decoration(TextDecoration.ITALIC, true);
    }

    public static Component playerThoughts(String text) {
        return playerThoughts(Component.text(text));
    }

    public static Component appleChat(Component text) {
        return Component.text("[").append(Component.text("God", new Color(16247171).toTextColor())).append(Component.text("] <apple270> "))
                .append(text);
    }

    public static Component appleChat(String text) {
        return appleChat(Component.text(text));
    }

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

    public String getTeamQuest(TowerTeam team) {
        String quest = team.getCurrentQuestId();
        if (quest != null) {
            return quest;
        }
        return NO_QUEST;
    }

    private final Plugin plugin;
    private final TowerTimer timer;
    private final Apr2023QuestManager apr2023QuestManager;
    private final QuestItems questItems;
    private final Map<String, Quest> quests;
    private final GuiHeldItem questBook;
    private final TeamManager teamManager;

    private final NPC spirit;

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
        apr2023QuestManager = new Apr2023QuestManager(plugin, this, teamManager);

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

        spirit = new NPC(teamManager, "Evil Spirit", SPIRIT, new Color(0x610b1f), new Color(0x870f2b));
        spirit.addDisallowedRegion(".*");

        NPC mysti = new NPC(teamManager, "Mystievous", MYSTI, new Color(0xc73858), new Color(0xd2607a));

        // Quest Interaction NPCs

        Dialogue doorNoOpen = new Dialogue(teamManager, playerThoughts("The door won't open..."), 0.0);
        Consumer<PlayerInteractAtEntityEvent> doorNoOpenEvent = event -> {
            Player player = event.getPlayer();
            doorNoOpen.play(player);
        };

        NPC houseEnter = new NPC(teamManager, "Enter House", House.ENTER_DOOR);
        houseEnter.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> instance.house.teleport(player));
            }
        });

        NPC houseInvite = new NPC(teamManager, "Party Invitation", House.INVITE);

        NPC houseLeave = new NPC(teamManager, "Leave House", House.LEAVE_DOOR);
        houseLeave.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> instance.house.leave(player));
            }
        });

        NPC tavernEnter = new NPC(teamManager, "Enter Tavern", BadTavern.ENTER_DOOR);
        tavernEnter.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> instance.badTavern.teleport(player));
            }
        });

        NPC tavernCrate = new NPC(teamManager, "Crate", BadTavern.CRATE_INTERACT);
        tavernCrate.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    instance.badTavern.moveCrate();
                    player.playSound(instance.offsetLocation(BadTavern.baseCrate), org.bukkit.Sound.BLOCK_CHEST_CLOSE, SoundCategory.RECORDS, 1f, 1f);
                });
            }
        });

        NPC tavernTrapdoor = new NPC(teamManager, "Trapdoor", BadTavern.TRAPDOOR_INTERACT);
        tavernTrapdoor.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> instance.badCellar.teleport(player));
            }
        });

        NPC goodTavernTrapdoor = new NPC(teamManager, "Trapdoor", GoodTavern.GOOD_TRAPDOOR_INTERACT);
        goodTavernTrapdoor.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> instance.goodCellar.trapdoorTeleport(player));
            }
        });

        NPC tavernChair = new NPC(teamManager, "Chair", GoodTavern.CHAIR_INTERACT);
        tavernChair.setDefaultHandler(event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    instance.goodTavern.moveChair();
                    player.playSound(instance.offsetLocation(GoodTavern.baseChair), org.bukkit.Sound.BLOCK_CHEST_CLOSE, SoundCategory.RECORDS, 1f, 1.5f);
                });
            }
        });

        NPC cauldron = new NPC(teamManager, "Cauldron", MAKE_POTION);

        // Configure Quests

        Key ambient = Key.key(Key.MINECRAFT_NAMESPACE, "entity.horse.ambient");
        Key angry = Key.key(Key.MINECRAFT_NAMESPACE, "entity.horse.angry");
        Key breathe = Key.key(Key.MINECRAFT_NAMESPACE, "entity.horse.breathe");
        Key land = Key.key(Key.MINECRAFT_NAMESPACE, "entity.horse.land");
        Key eat = Key.key(Key.MINECRAFT_NAMESPACE, "entity.horse.eat");

        penelopeStart = new Quest(plugin, teamManager, PENELOPE_START, "Penelope");
        penelopeStart.setDescription("Penelope needs some help! Go talk to her by the wedding venue.");
        quests.put(PENELOPE_START, penelopeStart);

        // Oh no no no no, what do I do?
        Dialogue pennOhNo = new Dialogue(teamManager, penelope.formatMessage("Oh no no no no, what do I do?"), 4.5f);
        pennOhNo.setSound(Sound.sound(land, Sound.Source.RECORD, 1f ,1f));
        {
            // The wedding is tonight and he's still missing!
            Dialogue wedding = new Dialogue(teamManager, penelope.formatMessage("The wedding is tonight and he's still missing!"), 4.5f);
            pennOhNo.setNext(wedding);

            //- What if something horrible's happened
            Dialogue horrible = new Dialogue(teamManager, penelope.formatMessage("What if something horrible's happened?"), 3.0f);
            horrible.setSound(Sound.sound(angry, Sound.Source.RECORD, 1f ,1f));
            wedding.setNext(horrible);

            //- What if- Oh!
            Dialogue whatIf = new Dialogue(teamManager, penelope.formatMessage("What if- Oh!"), 2.5f);
            horrible.setNext(whatIf);

            //- I'm sorry, I didn't see you there.
            Dialogue sorry = new Dialogue(teamManager, penelope.formatMessage("I'm sorry, I didn't see you there."), 3.5f);
            whatIf.setNext(sorry);

            //- Butt Stallion and I are supposed to get married tonight, but no one's seen him or steve skellington all day!
            Dialogue married = new Dialogue(teamManager, penelope.formatMessage("Butt Stallion and I are supposed to get married tonight, but no one has seen him or steve skellington all day!"), 8.0f);
            married.setSound(Sound.sound(ambient, Sound.Source.RECORD, 1f ,1.5f));
            sorry.setNext(married);

            //- I'm really getting worried about them.
            Dialogue worried = new Dialogue(teamManager, penelope.formatMessage("I'm really getting worried about them..."), 4.0f);
            married.setNext(worried);

            //- I know that you're all busy with your event today but would you be able to help a mar out?
            Dialogue busy = new Dialogue(teamManager, penelope.formatMessage("I know that you're all busy with your event today, but would you be able to help a mare out?"), 6.0f);
            worried.setNext(busy);

            // I would check at steve's house up in the village, there might be something there.
            Dialogue house = new Dialogue(teamManager, penelope.formatMessage("I would check steve's house up in the village, there might be something there."), 5.5f);
            house.setSound(Sound.sound(breathe, Sound.Source.RECORD, 1f, 1f));
            busy.setNext(house);

            // Just keep following the path, you can't miss it.
            Dialogue path = new Dialogue(teamManager, penelope.formatMessage("Just keep following the path up, you can't miss it."), 0.0);
            house.setNext(path);
        }
        penelope.addQuestHandler(PENELOPE_START, playerInteractAtEntityEvent -> {
            Player player = playerInteractAtEntityEvent.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) return;

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                pennOhNo.play(team, () -> {
                    team.setInDialogue(false);
                    setTeamQuest(team, STEVE_HOUSE);
                });
            }
        });
        houseEnter.addQuestHandler(PENELOPE_START, doorNoOpenEvent);
        tavernEnter.addQuestHandler(PENELOPE_START, doorNoOpenEvent);


        steveHouse = new Quest(plugin, teamManager, STEVE_HOUSE, "steve's house");
        steveHouse.setDescription("Butt Stallion and steve are missing! Head to steve's house to look for clues.");
        quests.put(STEVE_HOUSE, steveHouse);

        ItemStack invitePaper = ItemStack.deserializeBytes(Base64.getDecoder().decode(Apr2023QuestManager.INVITE_BOOK));
        BookMeta inviteMeta = (BookMeta) invitePaper.getItemMeta();
        inviteMeta.displayName(null);
        inviteMeta.title(Component.text("Party Invite"));
        inviteMeta.author(Component.text("steve skellington"));
        inviteMeta.setCustomModelData(2);
        invitePaper.setItemMeta(inviteMeta);

        Dialogue findInvite = new Dialogue(teamManager, playerThoughts("This looks like an invite to a party! Maybe that's where they were...?"), 6.0);
        {
            Dialogue check = new Dialogue(teamManager, playerThoughts("I think that village is just on the east side of this mountain, I should go check it out."), 0.0);
            findInvite.setNext(check);
            Dialogue instruction = new Dialogue(teamManager, playerThoughts(Component.text("[Press ").append(Component.keybind("key.use")).append(Component.text(" to open invite]"))), 1.0f);
            check.setNext(instruction);
        }
        tavernEnter.addQuestHandler(STEVE_HOUSE, doorNoOpenEvent);
        houseInvite.addQuestHandler(STEVE_HOUSE, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                if (!team.isInDialogue()) {
                    team.setInDialogue(true);
                    findInvite.play(team, () -> {
                        instance.house.collectInvite();
                        FullInventory.givePlayerItems(player, invitePaper, BlockVoucher.getVouchers(1));
                        team.sendMessage(getRewards(invitePaper, BlockVoucher.getVouchers(1)));
                        setTeamQuest(team, PARTY_INVITE);
                        team.setInDialogue(false);
                    });
                }
            }
        });


        partyInvite = new Quest(plugin, teamManager, PARTY_INVITE, "Party Invite");
        partyInvite.setDescription("Follow the party invite you found at steve's house.");
        quests.put(PARTY_INVITE, partyInvite);

        Dialogue bottles = new Dialogue(teamManager, playerThoughts("The bottles on the shelf in this cellar seem suspicious."), 6.0f);
        {
            Dialogue maybe = new Dialogue(teamManager, playerThoughts("Maybe it has something to do with that book?"), 1.0f);
            bottles.setNext(maybe);
        }

        tavernTrapdoor.addQuestHandler(PARTY_INVITE, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            if (instance != null) {
                if (!team.isInDialogue()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        setTeamQuest(team, BOTTLE_PUZZLE);
                        instance.badCellar.teleport(player);
                    });
                    team.setInDialogue(true);
                    bottles.play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
            }
        });

        bottlePuzzle = new Quest(plugin, teamManager, BOTTLE_PUZZLE, "Cellar Bottles");
        bottlePuzzle.setDescription("Figure out the bottles in the tavern cellar.");
        quests.put(BOTTLE_PUZZLE, bottlePuzzle);

        mysteriousTavern = new Quest(plugin, teamManager, RESTORED_TAVERN, "Changed");
        mysteriousTavern.setDescription("Explore the newly changed tavern.");
        quests.put(RESTORED_TAVERN, mysteriousTavern);


        enjoy = new Quest(plugin, teamManager, ENJOY, "Rest Up");
        enjoy.setDescription("Take a break from questing and enjoy the event!");
        quests.put(ENJOY, enjoy);

        steveFind = new Dialogue(teamManager, steve.formatMessage("Stay back!"), 2.5f);
        steveFind.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.stay_back"));
        {
            Dialogue oh = new Dialogue(teamManager, steve.formatMessage("Oh, it's you!"), 2.5f);
            oh.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.oh_its_you"));
            steveFind.setNext(oh);

            Dialogue thankHeavens = new Dialogue(teamManager, steve.formatMessage("Thank heavens you found me."), 3.0f);
            thankHeavens.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.thank_heavens"));
            oh.setNext(thankHeavens);

            Dialogue buttStallionMissing = new Dialogue(teamManager, steve.formatMessage("Butt Stallion's missing as well?"), 3.5f);
            buttStallionMissing.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.butt_stallion"));
            thankHeavens.setNext(buttStallionMissing);

            Dialogue notGood = new Dialogue(teamManager, steve.formatMessage("That's not good..."), 3.0f);
            notGood.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.not_good"));
            buttStallionMissing.setNext(notGood);

            Dialogue betweenDimensions = new Dialogue(teamManager, steve.formatMessage("The tavern is split between dimensions?"), 4.0f);
            betweenDimensions.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.between_dimensions"));
            notGood.setNext(betweenDimensions);

            Dialogue noSense = new Dialogue(teamManager, steve.formatMessage("That doesn't make any sense!"), 3.0f);
            noSense.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.no_sense"));
            betweenDimensions.setNext(noSense);

            Dialogue unless = new Dialogue(teamManager, steve.formatMessage("Unless..."), 3.0f);
            unless.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.unless"));
            noSense.setNext(unless);

            Dialogue doesntMatter = new Dialogue(teamManager, steve.formatMessage("It doesn't matter."), 1.5f);
            doesntMatter.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.doesnt_matter"));
            unless.setNext(doesntMatter);

            Dialogue groupUpLater = new Dialogue(teamManager, steve.formatMessage("I'll find what I can and group up with you later."), 4.0f);
            groupUpLater.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.group_up_later"));
            doesntMatter.setNext(groupUpLater);

            Dialogue rest = new Dialogue(teamManager, steve.formatMessage("You should take a bit to rest."), 3.0f);
            rest.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.rest"));
            groupUpLater.setNext(rest);

            Dialogue needIt = new Dialogue(teamManager, steve.formatMessage("You look like you need it."), 2.5f);
            needIt.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.need_it"));
            rest.setNext(needIt);

        }

        helpSteve = new Quest(plugin, teamManager, HELP_STEVE, "Help steve");
        helpSteve.setDescription("Help steve skellington make an antidote for Butt Stallion!");
        quests.put(HELP_STEVE, helpSteve);
        ItemStack recipeItem = ItemStack.deserializeBytes(Base64.getDecoder().decode(recipeBook));
        BookMeta recipeMeta = (BookMeta) recipeItem.getItemMeta();
        recipeMeta.displayName(null);
        recipeMeta.title(Component.text("Antidote Recipe"));
        recipeMeta.author(Component.text("steve skellington"));
        TextUtil.appendQuestItemLore(recipeMeta);
        recipeItem.setItemMeta(recipeMeta);

        Dialogue talkSteveHelp = new Dialogue(teamManager, steve.formatMessage("Antidotes are simple. The first things you learn as an apprentice making potions."), 6.5f);
        talkSteveHelp.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.antidote_simple"));
        {
            Dialogue recipe = new Dialogue(teamManager, steve.formatMessage("For this specific one, I was able to find this recipe."), 5.5f);
            recipe.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.recipe"));
            talkSteveHelp.setNext(recipe);

            Dialogue allNeedDo = new Dialogue(teamManager, steve.formatMessage("All you need to do is place those ingredients in the cauldron, scoop it into a bottle, and then you can hand me the finished potion."), 10);
            allNeedDo.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.all_need_to_do"));
            recipe.setNext(allNeedDo);
        }

        steve.addQuestHandler(HELP_STEVE, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }

            if (!team.isInDialogue()) {
                team.setInDialogue(true);
                talkSteveHelp.play(team, () -> {
                    setTeamQuest(team, MAKE_POTION);
                    FullInventory.givePlayerItems(player, recipeItem);
                    team.sendMessage(getRewards(recipeItem));
                    team.setInDialogue(false);
                });
            }
        });



        makePotion = new RequirementsQuest(plugin, teamManager, MAKE_POTION, "The Antidote");
        makePotion.setDescription("Follow the recipe for the antidote, to save Butt Stallion!");
        makePotion.addRequirement(new MaterialRequirement(Material.BLAZE_POWDER, 6));
        makePotion.addRequirement(new MaterialRequirement(Material.NETHER_WART, 1));
        makePotion.addRequirement(new MaterialRequirement(Material.SUGAR, 10));
        makePotion.addRequirement(new MaterialRequirement(Material.GLISTERING_MELON_SLICE, 3));
        TagRequirement netherHeart = new TagRequirement(plugin, Material.NETHER_STAR, NetherHeart.NETHER_HEART, 1);
        netherHeart.setName(NetherHeart.NAME);
        makePotion.addRequirement(netherHeart);
        quests.put(MAKE_POTION, makePotion);

        ItemStack antidote = GuiUtil.formatItem("Antidote", Material.POTION, 0);
        PotionMeta antidoteMeta = (PotionMeta) antidote.getItemMeta();
        antidoteMeta.setColor(new Color(0xff0d45).toBukkitColor());
        antidoteMeta.lore(TextUtil.formatTexts("Said to cure", "the strongest", "of poisons."));
        antidoteMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES);
        TextUtil.appendQuestItemLore(antidoteMeta);
        antidote.setItemMeta(antidoteMeta);
        NBTUtils.setBool(plugin, ANTIDOTE_TAG, antidote);
        NBTUtils.setNoUse(plugin, antidote);

        QuestItems.putItem(ANTIDOTE_TAG, antidote);


        Dialogue steveGiveAntidote = new Dialogue(teamManager, steve.formatMessage("Wonderful job!"), 2.5f);
        steveGiveAntidote.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.wonderful_job"));
        {
            Dialogue everything = new Dialogue(teamManager, steve.formatMessage("I will take care of everything from here."), 5.0f);
            everything.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.take_care_everything"));
            steveGiveAntidote.setNext(everything);
        }
        cauldron.addQuestHandler(MAKE_POTION, event -> {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItem(event.getHand());
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {
                RequirementsQuest quest = (RequirementsQuest) team.getQuest(MAKE_POTION);
                if (quest != null) {
                    if (item.getType().equals(Material.GLASS_BOTTLE)) {
                        if (quest.isComplete()) {
                            item.subtract(1);
                            FullInventory.givePlayerItems(player, antidote);
                            team.sendMessage(playerThoughts("The potion is finished! I should give this to steve now."));
                        } else {
                            player.sendMessage(playerThoughts("The potion isn't ready yet, I'm still missing some ingredients..."));
                        }
                    } else {
                        quest.turnIn(item);
                    }
                }
            }
        });
        steve.addQuestHandler(MAKE_POTION, event -> {
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }

            ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());

            if (!team.isInDialogue() && NBTUtils.boolState(plugin, ANTIDOTE_TAG, item)) {
                item.subtract(1);
                team.setInDialogue(true);
                steveGiveAntidote.play(team, () -> {
                    teamManager.giveTeamHatgroup(13, team);
                    FullInventory.givePlayerItems(player, BlockVoucher.getVouchers(4));
                    team.sendMessage(getRewards(BlockVoucher.getVouchers(4)));
                    team.sendMessage(Component.text("You've unlocked the ")
                            .append(Component.text("[Potion Goggles]", team.getColor().toTextColor())
                                    .clickEvent(ClickEvent.runCommand("/hat")))
                            .append(Component.text(" hat for this and future events!"))
                    );
                    setTeamQuest(team, NO_QUEST);
                    team.setInDialogue(false);
                });
            }
        });


        intermissionDialogue = new Dialogue(teamManager, spirit.formatMessage("So you found steve skellington, in the tavern down the street."), 6.0f);
        intermissionDialogue.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.found-steve"));
        {
            Dialogue notDifficult = new Dialogue(teamManager, spirit.formatMessage("That was not too difficult. I admit, I was not discrete."), 5.0f);
            notDifficult.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.not-discrete"));
            intermissionDialogue.setNext(notDifficult);

            Dialogue groom = new Dialogue(teamManager, spirit.formatMessage("But as for the groom, their mane so bold,"), 4.5f);
            groom.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.as-for-groom"));
            notDifficult.setNext(groom);

            Dialogue cold = new Dialogue(teamManager, spirit.formatMessage("You must work quickly, before his eyes go cold."), 4.5f);
            cold.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.work-quickly"));
            groom.setNext(cold);

            Dialogue biteApple = new Dialogue(teamManager, spirit.formatMessage("With a bite from this apple, dipped in poison slime..."), 3.75f);
            biteApple.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.bite-apple"));
            cold.setNext(biteApple);

            Dialogue chomp = new Dialogue(teamManager, 1.0f);
            chomp.setSound(Sound.sound(eat, Sound.Source.RECORD, 1f ,1.2f));
            biteApple.setNext(chomp);

            Dialogue limitedTime = new Dialogue(teamManager, spirit.formatMessage("The stallion himself is quite limited on time."), 6.5f);
            limitedTime.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.limited-time"));
            chomp.setNext(limitedTime);

            Dialogue enough = new Dialogue(teamManager, appleChat("That's enough."), 4.0f);
            limitedTime.setNext(enough);

            Dialogue gone = new Dialogue(teamManager, appleChat("This has gone too far."), 2.5f);
            enough.setNext(gone);

            Dialogue laugh = new Dialogue(teamManager, 2.9f);
            laugh.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.too-far"));
            gone.setNext(laugh);

            Dialogue tooFar = new Dialogue(teamManager, spirit.formatMessage("Too far?"), 13.2f);
            laugh.setNext(tooFar);

            Dialogue okay = new Dialogue(teamManager, mysti.formatMessage("Okay, okay."), 2.0f);
            tooFar.setNext(okay);

            Dialogue carried = new Dialogue(teamManager, mysti.formatMessage("Maybe, yeah, maybe I got a little carried away there."), 3.0f);
            carried.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.carried-away"));
            okay.setNext(carried);

            Dialogue goodRun = new Dialogue(teamManager, mysti.formatMessage("We had a good run though, like we were going for what like 6 months?"), 4.5f);
            goodRun.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.good-run"));
            carried.setNext(goodRun);

            Dialogue hints = new Dialogue(teamManager, mysti.formatMessage("We were even leaving hints and stuff all over the place, and y'all just did not have a clue."), 5.0f);
            hints.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.leaving-hints"));
            goodRun.setNext(hints);

            Dialogue steveActing = new Dialogue(teamManager, mysti.formatMessage("Not to mention, steve's acting? Perfection the whole time."), 5.5f);
            steveActing.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.steve-acting"));
            hints.setNext(steveActing);

            Dialogue grabAntidote = new Dialogue(teamManager, mysti.formatMessage("But let me just grab the antidote real qui- Hey apple?"), 6.0f);
            grabAntidote.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.grab-antidote"));
            steveActing.setNext(grabAntidote);

            Dialogue grocery = new Dialogue(teamManager, mysti.formatMessage("Did we put the antidote on the grocery list...?"), 3.5f);
            grocery.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.grocery-list"));
            grabAntidote.setNext(grocery);

            Dialogue person_standing = new Dialogue(teamManager, appleChat(">->o"), 1.5f);
            grocery.setNext(person_standing);

            Dialogue umm = new Dialogue(teamManager, mysti.formatMessage("Uhhhh okay ummm mmmmm mmmm uhhhhh ju- gi- give m- give me just one second..."), 13.0f);
            umm.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.one-second"));
            person_standing.setNext(umm);

            Dialogue door = new Dialogue(teamManager, 0.0f);
            door.setSoundKey(Key.key(Key.MINECRAFT_NAMESPACE, "entity.zombie.attack_wooden_door"));
            umm.setNext(door);

            Dialogue noAntidote = new Dialogue(teamManager, steve.formatMessage("What do you mean you don't have the antidote?!?"), 3.5f);
            noAntidote.setSound(Sound.sound(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.dont_have_antidote"), Sound.Source.RECORD, 0.75f ,1f));
            door.setNext(noAntidote);

            Dialogue mushroomLamp = new Dialogue(teamManager, mysti.formatMessage("Look. Mistakes happen, sometimes a silly little mushroom lamp catches my eye more than a boring bottle of liquid."), 6.5f);
            mushroomLamp.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.mushroom-lamp"));
            noAntidote.setNext(mushroomLamp);

            Dialogue willYouHelp = new Dialogue(teamManager, mysti.formatMessage("Just- Will you help me?"), 2.5f);
            willYouHelp.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.will-you-help"));
            mushroomLamp.setNext(willYouHelp);

            Dialogue ofCourse = new Dialogue(teamManager, steve.formatMessage("Yes of course I'll help, but I can't keep bailing you out of these situations!"), 8.0f);
            ofCourse.setSound(Sound.sound(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.bailing_out"), Sound.Source.RECORD, 0.75f ,1f));
            willYouHelp.setNext(ofCourse);

            Dialogue steveHelp = new Dialogue(teamManager, mysti.formatMessage("Ok. Steve knows how to make the antidote, but he can't gather the materials himself. So he'll need some help."), 6.0f);
            steveHelp.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.gather-materials"));
            ofCourse.setNext(steveHelp);

            Dialogue enchanting = new Dialogue(teamManager, mysti.formatMessage("He'll be standing over at the cauldron by the enchantment area."), 3.0f);
            enchanting.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.enchantment-area"));
            steveHelp.setNext(enchanting);

            Dialogue beforeWorse = new Dialogue(teamManager, mysti.formatMessage(Component.text("So if you have any time at all, ")
                    .append(Component.text("please").decoration(TextDecoration.ITALIC, true))
                    .append(Component.text(" go talk to him and help us before this gets worse."))), 7.0f);
            beforeWorse.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.any-time"));
            enchanting.setNext(beforeWorse);

        }


        // Questbook Commands

        QuestCommands commands = new QuestCommands(this);
        plugin.getCommand("questbook").setExecutor(commands);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (team != null && team.getCurrentQuestId().equals(RESTORED_TAVERN)) {
            Apr2023QuestInstance instance = apr2023QuestManager.getQuestInstance(team);
            Block block = event.getClickedBlock();
            if (instance != null && block != null) {
                if (instance.goodTavern.getInstanceDoorClicks().contains(block.getLocation())) {
                    if (!team.isInDialogue()) {
                        team.setInDialogue(true);
                        steveFind.play(team, () -> {
                            FullInventory.givePlayerItems(player, BlockVoucher.getVouchers(2));
                            team.sendMessage(getRewards(BlockVoucher.getVouchers(2)));
                            setTeamQuest(team, ENJOY);
                            team.setInDialogue(false);
                        });
                    }
                }
            }
        }

    }

    public static final Location steveLocation = new Location(Worlds.Apr2023(), -591, 65, -2446);

    public void triggerIntermission() {
        intermissionDialogue.play(Bukkit.getServer(), () -> {
            try {
                apr2023QuestManager.removeSteve();
                Skeleton steve = (Skeleton) steveLocation.getWorld().spawnEntity(steveLocation, EntityType.SKELETON, false);
                steve.addScoreboardTag(QuestManager.STEVE);
                steve.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
                steve.setPersistent(true);
                steve.setInvulnerable(true);
                steve.customName(Component.text("steve skellington"));
                ItemStack goggles = GuiUtil.formatItem("Potion Goggles", Material.LEATHER_HORSE_ARMOR, 30);
                LeatherArmorMeta meta = (LeatherArmorMeta) goggles.getItemMeta();
                meta.setColor(STEVE_COLOR.toBukkitColor());
                goggles.setItemMeta(meta);
                steve.getEquipment().setHelmet(goggles);
                timer.start(true);
                for (TowerTeam team : teamManager.getParticipantTeams()) {
                    setTeamQuest(team, HELP_STEVE);
                }
            } catch (TimerUnsetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public NPC getSpirit() {
        return spirit;
    }

    public QuestItems getQuestItems() {
        return questItems;
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

