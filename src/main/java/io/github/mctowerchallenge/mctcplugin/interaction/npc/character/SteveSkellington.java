package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import io.github.mystievous.mysticore.TextUtil;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue.playerThoughts;

/**
 * Represents the Steve Skellington character with specific interactions, traits, and dialogue.
 */
public class SteveSkellington extends QuestCharacter {

    // Character attributes
    public static final String NAME = "steve skellington";
    public static final Color NAME_COLOR = new Color(0x399c91);
    public static final Color TEXT_COLOR = new Color(0x55b4aa);
    public static final String REGION = "steve";
    public static final String TRAIT_NAME = "steveskellington";

    private final Dialogue eventStartDialogue;
    private final Dialogue ballDropDialogue;

    /**
     * Creates a new SteveSkellington instance.
     *
     * @param plugin The plugin instance.
     */
    public SteveSkellington(Plugin plugin) {
        super(plugin, EntityType.SKELETON, NAME, NAME_COLOR, TEXT_COLOR);

        eventStartDialogue = new Dialogue(plugin, TextUtil.formatText("* Announcement Sound"), 3.0d)
                .setSoundKey(MCTCPlugin.key("bell"));
        eventStartDialogue.append(new Dialogue(plugin, formatMessage("Hello everyone!"), 1.187d)
                .setSoundKey(MCTCPlugin.key("steve.jan2024.intro1")));
        eventStartDialogue.append(new Dialogue(plugin, formatMessage("I am so sorry to announce that we are having some technical difficulties right now."), 7.411));
        eventStartDialogue.append(new Dialogue(plugin, formatMessage("So please bear with us while we hopefully get these issues resolved soon."), 6.027));

        eventStartDialogue.append(new Dialogue(plugin, formatMessage("Until then, please keep partying and having fun!"), 4.08d)
                .setSoundKey(MCTCPlugin.key("steve.jan2024.intro2")));

        eventStartDialogue.append(new Dialogue(plugin, playerThoughts("I should go talk to steve and see if there's anything I can help out with."), 4.5d));

        Dialogue steveStartDialogue = new Dialogue(plugin, formatMessage(Component.text("Oh hey, we have a ")
                .append(Component.text("big").decorate(TextDecoration.ITALIC))
                .append(Component.text(" issue..."))), 4.128)
                .setSoundKey(MCTCPlugin.key("steve.jan2024.quest1"));
        steveStartDialogue.append(new Dialogue(plugin, formatMessage("The mechanism that moves the ball up and down seems to have, uhhh, broken."), 5.874d));
        steveStartDialogue.append(new Dialogue(plugin, formatMessage("And I'm worried that the person we hired won't get it fixed in time!"), 5.691d));
        steveStartDialogue.append(new Dialogue(plugin, formatMessage("Can you go see Generic Maintenance Man and help him get this fixed in time for the ball drop?"), 7.506)
                .setSoundKey(MCTCPlugin.key("steve.jan2024.quest2")));

        Dialogue steveIdleDialogue = new Dialogue(plugin, formatMessage("Thank you so much for helping to get the ball fixed!"), 5.0d)
                .setSoundKey(MCTCPlugin.key("steve.jan2024.questidle"));

        ballDropDialogue = new Dialogue(plugin, TextUtil.formatText("* Announcement Sound"), 3.0d)
                .setSoundKey(MCTCPlugin.key("bell"));
        ballDropDialogue.append(new Dialogue(plugin, formatMessage("Hello all!"), 1.024d)
                .setSoundKey(MCTCPlugin.key("steve.jan2024.quest3")));
        ballDropDialogue.append(new Dialogue(plugin, formatMessage("I am happy to share that the technical difficulties have been resolved, and just in time for the ball drop!"), 7.116));
        ballDropDialogue.append(new Dialogue(plugin, formatMessage("I hope you all enjoy!"), 3.5d));


        setDefaultInteractionHandler((team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                steveIdleDialogue.play(team, () -> team.setInDialogue(false));
            }
        });

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, npcRightClickEvent) -> {
        });
        addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        addQuestInteractionHandler(QuestTags.STEVE_START, (team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                steveStartDialogue.play(team, () -> {
                    team.setInDialogue(false);
                    team.completeQuest(QuestTags.STEVE_START);
                    team.setQuest(QuestTags.GEN_START);
                });
            }
        });

    }

    public Dialogue getEventStartDialogue() {
        return eventStartDialogue;
    }

    public Dialogue getBallDropDialogue() {
        return ballDropDialogue;
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return SteveTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class SteveTrait extends Trait {

        public SteveTrait() {
            super(TRAIT_NAME);
        }

    }

}
