package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import io.github.mctowerchallenge.mctcplugin.quest.Quest;
import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mctowerchallenge.mctcplugin.quest.QuestUtil;
import io.github.mctowerchallenge.mctcplugin.quest.util.FullInventory;
import io.github.mystievous.mysticore.Color;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue.playerThoughts;

/**
 * Represents the Generic Maintenance Man character with specific interactions, traits, and dialogue.
 */
public class GenericMaintenanceMan extends QuestCharacter {

    // Character attributes
    public static final String NAME = "Generic Maintenance Man";
    public static final Color NAME_COLOR = new Color(0xd49500);
    public static final Color TEXT_COLOR = new Color(0xa46e00);
    public static final String REGION = "gmm";
    public static final String TRAIT_NAME = "genericmaintenanceman";

    /**
     * Creates a new GenericMaintenanceMan instance.
     *
     * @param plugin The plugin instance.
     */
    public GenericMaintenanceMan(Plugin plugin) {
        super(plugin, EntityType.VILLAGER, NAME, NAME_COLOR, TEXT_COLOR);

//        Dialogue genericStartDialogue = new Dialogue(plugin, formatMessage("Ey! The damn ball broke and it's stuck in the up position!"), 4.889d)
//                .setSoundKey(MCTCPlugin.key("genericmaintenanceman.jan2024.quest1"));
//        genericStartDialogue.append(new Dialogue(plugin, formatMessage("*clicks tongue* Ok. According to this video I looked up, I think two gears and a lever are missing."), 6.162d));
//        genericStartDialogue.append(new Dialogue(plugin, formatMessage("But, I don't know."), 1.734d));
//        genericStartDialogue.append(new Dialogue(plugin, formatMessage("I've tried turning it off and back on and, uhhh, hitting it with my wrench, and..."), 5.028d));
//        genericStartDialogue.append(new Dialogue(plugin, formatMessage("Well, nothing seems to be fuckin working."), 2.094));
//        genericStartDialogue.append(new Dialogue(plugin, formatMessage("So, might as well try this."), 4.5d));
//        genericStartDialogue.append(new Dialogue(plugin, playerThoughts("Hmm, maybe I should try to find those parts around here."), 2.0d));
//
//        Dialogue genericReturnDialogue = new Dialogue(plugin, formatMessage("Oh, wow, you got the parts that were missing."), 2.645d)
//                .setSoundKey(MCTCPlugin.key("genericmaintenanceman.jan2024.quest2"));
//        genericReturnDialogue.append(new Dialogue(plugin, formatMessage("Looks like we got a regular fuckin sherlock holmes around here, ey?"), 3.130d));
//        genericReturnDialogue.append(new Dialogue(plugin, formatMessage("Thanks for finding 'em!"), 3.487d));
//        genericReturnDialogue.append(new Dialogue(plugin, formatMessage("..."), 1.5d));
//        genericReturnDialogue.append(new Dialogue(plugin, formatMessage("What the fuck am I supposed to do with these?"), 2.531d)
//                .setSoundKey(MCTCPlugin.key("genericmaintenanceman.jan2024.quest3")));
//        genericReturnDialogue.append(new Dialogue(plugin, formatMessage("I noticed that there was a path up to the ball, but uh..."), 3.703d));
//        genericReturnDialogue.append(new Dialogue(plugin, formatMessage("Yeah, I'll be real. That, yknow looks like a lot of work to get up there, so uhh, I'm not getting paid for that, so..."), 6.954d));
//        genericReturnDialogue.append(new Dialogue(plugin, formatMessage("No."), 1.88d));
//
//        Dialogue genericFinishDialogue = new Dialogue(plugin, formatMessage("So!"), 0.925)
//                .setSoundKey(MCTCPlugin.key("genericmaintenanceman.jan2024.quest4"));
//        genericFinishDialogue.append(new Dialogue(plugin, formatMessage("Good fuckin job! You went up there, you did my work for me-"), 3.466d));
//        genericFinishDialogue.append(new Dialogue(plugin, formatMessage("That I'm not getting paid for so I mean.. yknow, who's *really* the one getting put to work here?"), 5.813d));
//        genericFinishDialogue.append(new Dialogue(plugin, formatMessage("Anyways, all that's left to do: Turn it on, hope it works!"), 6.875d));
//
//        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, npcRightClickEvent) -> {
//        });
//        addQuestInteractionHandler(QuestTags.GEN_START, (team, npcRightClickEvent) -> {
//            if (team.canStartDialogue()) {
//                team.setInDialogue(true);
//                genericStartDialogue.play(team, () -> {
//                    team.setInDialogue(false);
//                    team.completeQuest(QuestTags.GEN_START);
//                    team.setQuest(QuestTags.FIND_ITEMS);
//                });
//            }
//        });
//        addQuestInteractionHandler(QuestTags.GEN_RETURN, (team, npcRightClickEvent) -> {
//            if (team.canStartDialogue()) {
//                team.setInDialogue(true);
//                genericReturnDialogue.play(team, () -> {
//                    team.setInDialogue(false);
//                    ItemStack reward = QuestUtil.randomDyeBundle();
//                    FullInventory.givePlayerItems(npcRightClickEvent.getClicker(), reward);
//                    team.completeQuest(QuestTags.GEN_RETURN);
//                    npcRightClickEvent.getClicker().sendMessage(QuestManager.getRewards(reward));
//                    team.setQuest(QuestTags.FIX_BALL);
//                });
//            }
//        });
//        addQuestInteractionHandler(QuestTags.GEN_COMPLETE, (team, npcRightClickEvent) -> {
//            if (team.canStartDialogue()) {
//                team.setInDialogue(true);
//                genericFinishDialogue.play(team, () -> {
//                    team.setInDialogue(false);
//                    ItemStack reward = QuestUtil.randomBlockBundle(8);
//                    FullInventory.givePlayerItems(npcRightClickEvent.getClicker(), reward);
//                    npcRightClickEvent.getClicker().sendMessage(QuestManager.getRewards(reward));
//                    team.completeQuest(QuestTags.GEN_COMPLETE);
//                    team.setQuest(QuestTags.NO_QUEST);
//                });
//            }
//        });
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return GenericMaintenanceManTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class GenericMaintenanceManTrait extends Trait {

        public GenericMaintenanceManTrait() {
            super(TRAIT_NAME);
        }

    }

}
