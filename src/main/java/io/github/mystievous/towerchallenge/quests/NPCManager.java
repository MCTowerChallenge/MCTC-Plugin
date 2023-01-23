package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.eventspecific.winter.presents.PresentEntityHandler;
import io.github.mystievous.towerchallenge.magic.MagicItems;
import io.github.mystievous.towerchallenge.misc.fasttravel.FastTravelListener;
import io.github.mystievous.towerchallenge.quests.entities.GodMountNPC;
import io.github.mystievous.towerchallenge.quests.entities.ItemEntityHandler;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.quests.legacy.BlockVoucher;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class NPCManager {

    public static Component SteveText(Component text) {
        return Component.text("<steve skellington> ").color(TextColor.color(0xc458ab))
                .append(text.color(TextColor.color(0xffabeb)));
    }

    public static Component SteveText(String text) {
        return SteveText(Component.text(text));
    }

    public static Component PenelopeText(String text) {
        return Component.text("<Penelope> ").color(TextColor.color(0x932172))
                .append(Component.text(text).color(TextColor.color(0xbc519c)));
    }

    public static Component SpiritText(Component text) {
        return Component.text("<Mysterious Entity> ").color(TextColor.color(0x373f26))
                .append(text.color(TextColor.color(0x4d5835)));
    }

    public static Component SpiritText(String text) {
        return SpiritText(Component.text(text));
    }

    public static final String STEVE_TAG = "steve";
    public static final String PENELOPE_TAG = "penelope";
    public static final String BUTT_STALLION = "buttstallion";
    public static final String SPIRIT_TAG = "holiday-spirit";
    public static final String HANK_MARVIN_TAG = "golem";

    private final List<Dialogue> dialogues = new ArrayList<>();
    private final Dialogue meltDialogue;
    private final Dialogue spiritFinaleDialogue;

    public NPCManager(TeamManager teamManager, QuestManager questManager) {

        GodMountNPC penelope = new GodMountNPC(teamManager, PENELOPE_TAG);
        penelope.addAllowedRegion("penelope-.*");

        ItemStack penelopeArmorItem = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
        ItemMeta armorMeta = penelopeArmorItem.getItemMeta();
        armorMeta.displayName(TextUtil.noItalic("Penelope's Armor"));
        penelopeArmorItem.setItemMeta(armorMeta);

        new ItemEntityHandler(teamManager, QuestManager.PENELOPE_ARMOR, QuestManager.PENELOPE_ARMOR, penelopeArmorItem);

        new GodMountNPC(teamManager, BUTT_STALLION);

        NPC steve = new NPC(teamManager, STEVE_TAG);
        steve.addAllowedRegion("steve-.*");

        ItemEntityHandler steveList = new ItemEntityHandler(teamManager, QuestManager.STEVE_LIST, QuestManager.STEVE_LIST, questManager.getSteveListItem());

        NPC spirit = new NPC(teamManager, SPIRIT_TAG);
        spirit.addDisallowedRegion(".*");

        new PresentEntityHandler(teamManager);

        NPC hankMarvin = new NPC(teamManager, HANK_MARVIN_TAG);
        hankMarvin.addAllowedRegion("candy-village-inner");
        hankMarvin.addDisallowedRegion(".*_gingerbread");

        Dialogue penelopeStartDialogue = new Dialogue(teamManager, PenelopeText("Hay, friend! It's great to see you!"), 3)
//                .setSoundKey(PENELOPE_SOUND)

                .setNext(new Dialogue(teamManager, PenelopeText("Are you here for steve's winter beach party too?"), 4)
//                        .setSoundKey(PENELOPE_SOUND)

                        .setNext(new Dialogue(teamManager, PenelopeText("I'm so excited! Although I can't seem to find my favorite outfit anywhere :("), 5.5)
//                                .setSoundKey(PENELOPE_SOUND)

                                .setNext(new Dialogue(teamManager, PenelopeText("Last I remember seeing it I was making snowpals on top of the iceberg."), 4.5)
//                                        .setSoundKey(PENELOPE_SOUND)

                                        .setNext(new Dialogue(teamManager, PenelopeText("Would you be a saltcube and go see if I dropped it up there?"), 3.5)
//                                                .setSoundKey(PENELOPE_SOUND)
                                                .setNext(new Dialogue(teamManager, PenelopeText("I believe there's a way up on the north-west side."), 3.5))))));
//                                                .setSoundKey(PENELOPE_SOUND)
        penelopeStartDialogue.setFriendlyName("Penelope Start");
        dialogues.add(penelopeStartDialogue);

        penelope.addQuestHandler(QuestManager.PENELOPE_START, clickEvent -> {
            TowerTeam team = teamManager.getPlayerTeam(clickEvent.getPlayer());

            if (team != null && !team.isInDialogue()) {
                Player clickPlayer = clickEvent.getPlayer();
                team.setInDialogue(true);
                penelopeStartDialogue.play(clickPlayer, player -> {
                    team.setInDialogue(false);
                    questManager.setTeamQuest(player, QuestManager.PENELOPE_ARMOR);
                });
            }
        });

        Dialogue penelopeArmorIdleDialogue = new Dialogue(teamManager, PenelopeText("Can you go look for my outfit?"), 2.5)
                .setNext(new Dialogue(teamManager, PenelopeText("I think I may have left it on the iceberg, you should be able to climb up the north-west side to check."), 6.5));

        penelopeArmorIdleDialogue.setFriendlyName("Penelope Armor In Progress");
        dialogues.add(penelopeArmorIdleDialogue);

        Dialogue penelopeArmorCompleteDialogue = new Dialogue(teamManager, PenelopeText("Oh, is that my armor? Thank you so much for finding it!"), 5)
                .setNext(new Dialogue(teamManager, PenelopeText("If you have some time, stop by steve's house to the south-west of here!"), 5)
                        .setNext(new Dialogue(teamManager, PenelopeText("He might need some help setting up for the party."), 2.5)));

        penelopeArmorCompleteDialogue.setFriendlyName("Penelope Armor Complete");
        dialogues.add(penelopeArmorCompleteDialogue);

        penelope.addQuestHandler(QuestManager.PENELOPE_ARMOR, clickEvent -> {
            TowerTeam team = teamManager.getPlayerTeam(clickEvent.getPlayer());

            if (team != null && !team.isInDialogue()) {
                Player clickPlayer = clickEvent.getPlayer();
                ItemStack item = clickPlayer.getInventory().getItem(clickEvent.getHand());
                Quest quest = questManager.getQuest(team, QuestManager.PENELOPE_ARMOR);
                if (quest != null) {
                    QuestRequirement requirement = quest.getRequirement(item);
                    if (requirement != null) {
                        int amount = item.getAmount();
                        item.setAmount(amount - requirement.turnIn(team, amount));
                    }
                    if (quest.isFulfilled()) {
                        team.setInDialogue(true);
                        questManager.setTeamQuest(team, quest.getNext().getId());
                        clickPlayer.getInventory().setItem(clickEvent.getHand(), BlockVoucher.getVouchers(1));
                        penelopeArmorCompleteDialogue.play(clickPlayer, player -> {
                            team.setInDialogue(false);
                        });
                    } else {
                        team.setInDialogue(true);
                        penelopeArmorIdleDialogue.play(clickPlayer, player -> {
                            team.setInDialogue(false);
                        });
                    }
                }
            }
        });

        Dialogue penelopeSteveStartDialogue = new Dialogue(teamManager, PenelopeText("If you have some time, stop by steve's house to the south-west of here!"), 5)
                .setNext(new Dialogue(teamManager, PenelopeText("He might need some help setting up for the party."), 2.5));

        penelope.addQuestHandler(QuestManager.STEVE_START, clickEvent -> {
            TowerTeam team = teamManager.getPlayerTeam(clickEvent.getPlayer());

            if (team != null && !team.isInDialogue()) {
                Player clickPlayer = clickEvent.getPlayer();
                team.setInDialogue(true);
                penelopeSteveStartDialogue.play(clickPlayer, player -> {
                    team.setInDialogue(false);
                });
            }
        });

        Dialogue steveStartDialogue = new Dialogue(teamManager, SteveText("Oh! Hello there friend!"), 3)
                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly1"))

                .setNext(new Dialogue(teamManager, SteveText("Penelope told me that you were just helping her get ready for our party."), 4.75)
                        .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly2"))

                        .setNext(new Dialogue(teamManager, SteveText("How very nice of you to do!"), 3.5)
                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly3")))

                        .setNext(new Dialogue(teamManager, SteveText("Now I just hope I have everything ready in time..."), 5)
                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly4"))

                                .setNext(new Dialogue(teamManager, SteveText("Could you do me a big favor?"), 5.5)
                                        .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly5"))

                                        .setNext(new Dialogue(teamManager, SteveText("Awesome, thank you so much!"), 3.5)
                                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly6"))

                                                .setNext(new Dialogue(teamManager, SteveText("There are quite a few things I still need to gather for this party."), 5.5)
                                                        .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly7"))

                                                        .setNext(new Dialogue(teamManager, SteveText("I wrote it all down on a list..."), 7.5)
                                                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly8"))

                                                                .setNext(new Dialogue(teamManager, SteveText("Now where did I put it?"), 4)
                                                                        .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly9"))

                                                                        .setNext(new Dialogue(teamManager, SteveText("I think I left it upstairs. Can you go look for me and if you find it get those things for me?"), 9)
                                                                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly10"))

                                                                                .setNext(new Dialogue(teamManager, SteveText(Component.text("Again, I would ")
                                                                                        .append(Component.text("greatly")
                                                                                                .decoration(TextDecoration.ITALIC, true))
                                                                                        .append(Component.text(" appreciate it!")
                                                                                                .decoration(TextDecoration.ITALIC, false))), 5)
                                                                                        .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly11"))
                                                                                )))))))));

        steveStartDialogue.setFriendlyName("Steve Start");
        dialogues.add(steveStartDialogue);

        steve.addQuestHandler(QuestManager.STEVE_START, clickEvent -> {
            TowerTeam team = teamManager.getPlayerTeam(clickEvent.getPlayer());

            if (team != null && !team.isInDialogue()) {
                Player clickPlayer = clickEvent.getPlayer();
                Quest quest = questManager.getQuest(team, QuestManager.PENELOPE_ARMOR);
                if (quest != null) {
                    team.setInDialogue(true);
                    steveStartDialogue.play(clickPlayer, player -> {
                        team.setInDialogue(false);
                        questManager.setTeamQuest(player, QuestManager.STEVE_LIST);
                    });
                }
            }
        });

        steveList.setEventHandler(player -> {
            questManager.setTeamQuest(player, QuestManager.STEVE_ITEMS);
            player.sendMessage(TextUtil.formatText("You found steve's list!").decoration(TextDecoration.ITALIC, true));
        });

        Dialogue steveItemsIdleDialogue = new Dialogue(teamManager, SteveText("Thank you again for going to find those items for me!"), 0);

        steveItemsIdleDialogue.setFriendlyName("Steve Idle");
        dialogues.add(steveItemsIdleDialogue);

        Dialogue steveItemsCompleteDialogue = new Dialogue(teamManager, SteveText(Component.text("Thank you ")
                .append(Component.text("so").decoration(TextDecoration.ITALIC, true)).append(Component.text(" much for your help!"))), 3)
                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly12"))
                .setNext(
                        new Dialogue(teamManager, SteveText("I hope it wasn't too much of a hassle."), 4)
                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly13"))
                                .setNext(
                                        new Dialogue(teamManager, SteveText("Now why don't you take a break from this silly MCTC and relax in my hot tub or lay on the beach with a coconut drink!"), 10)
                                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly14"))
                                                .setNext(
                                                        new Dialogue(teamManager, SteveText(Component.text("Man, I really should get a gardener to take care of those overgrown shrubs on the side of my house before the party. Those noises I keep hearing behind it is driving me crazy...")), 12.5)
                                                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "steve.skelly15"))
                                                                .setNext(new Dialogue(teamManager, SpiritText("Down the rabbit hole you go, on the west side of the hovel."), 5.5)
                                                                        .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.find_me"))
                                                                        .setNext(new Dialogue(teamManager, SpiritText(Component.text("Come find me. It will be worth your trouble...")), 5.5)
                                                                        )
                                                                )
                                                )
                                )
                );

        steveItemsCompleteDialogue.setFriendlyName("Steve Items Complete");
        dialogues.add(steveItemsCompleteDialogue);

        Map<Material, ItemStack> steveItemRewards = new HashMap<>();
        steveItemRewards.put(Material.MELON_SLICE, new ItemStack(Material.NETHER_WART));
        steveItemRewards.put(Material.STRING, new ItemStack(Material.COAL, 16));
        steveItemRewards.put(Material.REDSTONE_LAMP, new ItemStack(Material.GOLDEN_CARROT, 32));
        steveItemRewards.put(Material.POTION, MagicItems.speedBoots);
        steveItemRewards.put(Material.COOKIE, new ItemStack(Material.GOLDEN_CARROT, 24));
        steveItemRewards.put(Material.CAKE, new ItemStack(Material.IRON_INGOT, 24));
        steveItemRewards.put(Material.SNOWBALL, MagicItems.randomUUID(MagicItems.snowballWand.getItem()));
        steveItemRewards.put(Material.SEA_PICKLE, new ItemStack(Material.BLAZE_ROD));
        steveItemRewards.put(Material.FIREWORK_ROCKET, new ItemStack(Material.IRON_INGOT, 8));

        steve.addQuestHandler(QuestManager.STEVE_ITEMS, clickEvent -> {
            TowerTeam team = teamManager.getPlayerTeam(clickEvent.getPlayer());

            if (team != null && !team.isInDialogue()) {
                Player clickPlayer = clickEvent.getPlayer();
                ItemStack item = clickPlayer.getInventory().getItem(clickEvent.getHand());
                Quest quest = questManager.getQuest(team, QuestManager.STEVE_ITEMS);
                if (quest != null) {
                    QuestRequirement requirement = quest.getRequirement(item);
                    if (requirement != null && !requirement.isFulfilled()) {
                        // The item used is one of the requirements, and it is not yet completed
                        boolean stop = false;
                        if (item.getType().equals(Material.POTION)) {
                            PotionMeta meta = (PotionMeta) item.getItemMeta();
                            if (!meta.getBasePotionData().equals(new PotionData(PotionType.SPEED, false, false))) {
                                stop = true;
                            }
                        }
                        if (item.getType().equals(Material.COOKIE)) {
                            if (NBTUtils.boolState(FastTravelListener.COOKIE_TAG, item)) {
                                stop = true;
                            }
                        }
                        if (!stop) {
                            int amount = item.getAmount();
                            item.setAmount(amount - requirement.turnIn(team, amount));
                            if (quest.isFulfilled()) {
                                // the items being turned in completed the quest
                                team.setInDialogue(true);
                                clickPlayer.getInventory().addItem(BlockVoucher.getVouchers(2));
                                steveItemsCompleteDialogue.play(clickPlayer, player -> {
                                    questManager.setTeamQuest(team, QuestManager.SPIRIT_START);
                                    team.setInDialogue(false);
                                });
                            } else {
                                // the items being turned in did not complete the quest
                                if (!requirement.isFulfilled()) {
                                    // the items being turned in did not complete the requirement
                                    team.getAudience().sendMessage(SteveText(Component.text("Oh, thank you! I need ")
                                            .append(Component.text(requirement.getRemaining(team)))
                                            .append(Component.text(" more of those."))));
                                } else {
                                    // the items being turned in completed the requirement
                                    team.getAudience().sendMessage(SteveText(Component.text("Oh, thank you! I think that's all of those I need. Now just find me the rest of those items!")));
                                    ItemStack reward = steveItemRewards.get(requirement.getType());
                                    if (reward.getItemMeta() instanceof LeatherArmorMeta meta) {
                                        meta.setColor(team.getColor().toBukkitColor());
                                        reward.setItemMeta(meta);
                                    }
                                    clickPlayer.getInventory().addItem(reward);
                                }
                            }
                            return;
                        }
                    }
                    // The item being used is not a requirement, or is already completed
                    team.setInDialogue(true);
                    steveItemsIdleDialogue.play(clickPlayer, player -> {
                        team.setInDialogue(false);
                    });
                }
            }
        });

        Dialogue spiritStartDialogue = new Dialogue(teamManager, SpiritText("24 PRESENTS, spread across the earth..."), 4.5)
                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.presents_start"))
                .setNext(new Dialogue(teamManager, SpiritText("Bring them all to me, and you will find your mirth."), 4.5));

        spiritStartDialogue.setFriendlyName("Spirit Start");
        dialogues.add(spiritStartDialogue);

        spirit.addQuestHandler(QuestManager.SPIRIT_START, clickEvent -> {
            TowerTeam team = teamManager.getPlayerTeam(clickEvent.getPlayer());

            if (team != null && !team.isInDialogue()) {
                Player clickPlayer = clickEvent.getPlayer();
                team.setInDialogue(true);
                spiritStartDialogue.play(clickPlayer, player -> {
                    questManager.setTeamQuest(player, QuestManager.SPIRIT_PRESENTS);
                    team.setInDialogue(false);
                });
            }
        });

        Dialogue spiritPresentsComplete = new Dialogue(teamManager, SpiritText("24 PRESENTS, in my hands..."), 4)
                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.presents_complete"))
                .setNext(new Dialogue(teamManager, SpiritText("Now go out, have fun, and await my plans."), 5));

        spiritPresentsComplete.setFriendlyName("Spirit Complete");
        dialogues.add(spiritPresentsComplete);

        spirit.addQuestHandler(QuestManager.SPIRIT_PRESENTS, clickEvent -> {
            TowerTeam team = teamManager.getPlayerTeam(clickEvent.getPlayer());

            if (team != null && !team.isInDialogue()) {
                Player clickPlayer = clickEvent.getPlayer();
                ItemStack item = clickPlayer.getInventory().getItem(clickEvent.getHand());
                Quest quest = questManager.getQuest(team, QuestManager.SPIRIT_PRESENTS);
                if (quest != null) {
                    QuestRequirement requirement = quest.getRequirement(item);
                    if (requirement != null && !requirement.isFulfilled() && NBTUtils.boolState(PresentEntityHandler.PRESENT_TAG, item)) {
                        // The item used is one of the requirements, and it is not yet completed
                        boolean stop = false;
                        if (!stop) {
                            int amount = item.getAmount();
                            item.setAmount(amount - requirement.turnIn(team, amount));
                            if (quest.isFulfilled()) {
                                // the items being turned in completed the quest
                                team.setInDialogue(true);
                                ItemStack goatHorns = MagicItems.goatHat.getItem();
                                LeatherArmorMeta meta = (LeatherArmorMeta) goatHorns.getItemMeta();
                                meta.setColor(team.getColor().toBukkitColor());
                                clickPlayer.getInventory().addItem();
                                clickPlayer.getInventory().addItem(BlockVoucher.getVouchers(3));
                                spiritPresentsComplete.play(clickPlayer, player -> {
                                    questManager.setTeamQuest(team, QuestManager.NO_QUEST);
                                    team.setInDialogue(false);
                                });
                            } else {
                                // the items being turned in did not complete the quest
                                if (!requirement.isFulfilled()) {
                                    // the items being turned in did not complete the requirement
                                    team.getAudience().sendMessage(SpiritText(Component.text(requirement.getRemaining(team))
                                            .append(Component.text(" left..."))));
                                } else {
                                    // the items being turned in completed the requirement
//                                    team.getAudience().sendMessage(SteveText(Component.text("Oh, thank you! I think that's all of those I need. Now just find me the rest of those items!")));
                                }
                            }
                            return;
                        }
                    }
                    // The item being used is not a requirement, or is already completed
                    team.setInDialogue(true);
                    spiritStartDialogue.play(clickPlayer, player -> {
                        team.setInDialogue(false);
                    });

                }
            }
        });

        meltDialogue = new Dialogue(teamManager, SpiritText("Foolish humans, so quick to sway..."), 3.75)
                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.foolish_humans"))
                .setNext(new Dialogue(teamManager, SpiritText("Took but a sentence to get me my way."), 3.5)
                        .setNext(new Dialogue(teamManager, SpiritText("As my dear sibling has kept me at bay,"), 3.5)
                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.dear_sibling"))
                                .setNext(new Dialogue(teamManager, SpiritText("you will find not a trace if you search for my name."), 3.5)
                                        .setNext(new Dialogue(teamManager, SpiritText("But where is he now? Does he frolic and flout?"), 4)
                                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.frolic"))
                                                .setNext(new Dialogue(teamManager, SpiritText("Well that is something you will later find out."), 4)
                                                        .setNext(new Dialogue(teamManager, SpiritText("For now, my friends, you were a means to an end."), 4.25)
                                                                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.return_the_sea"))
                                                                .setNext(new Dialogue(teamManager, SpiritText("But now that I'm free, I must return the sea."), 5.5))))))));
        meltDialogue.setFriendlyName("Spirit Melt");
        dialogues.add(meltDialogue);

        spiritFinaleDialogue = new Dialogue(teamManager, SpiritText("Until next time..."), 5)
                .setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.until_next_time"));
        spiritFinaleDialogue.setFriendlyName("Spirit Finale");
        dialogues.add(spiritFinaleDialogue);

    }

    public List<Dialogue> getDialogues() {
        return dialogues;
    }


    public void spiritMelt() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Audience audience = Audience.audience(players);
        meltDialogue.play(audience, () -> {
            audience.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.elder_guardian.curse"), Sound.Source.RECORD, 1f, 1f));
            for (Player player : players) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 10, false, false));
            }
        });
    }

    public void spiritFinale() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        spiritFinaleDialogue.play(Audience.audience(players), () -> {
            for (Player player : players) {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.SLOW);
            }
        });
    }

}
