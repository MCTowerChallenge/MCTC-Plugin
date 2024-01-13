package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Random;

public class Pete extends QuestCharacter {

    public static final String NAME = "Pete";
    public static final Color NAME_COLOR = new Color(0xd92521);
    public static final Color TEXT_COLOR = new Color(0xbe3430);
    public static final String TRAIT_NAME = "pete";

    private final Random random = new SecureRandom();

    public Pete(Plugin plugin) {
        super(plugin, EntityType.CAVE_SPIDER, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] peteLines = {
                new Dialogue(plugin, formatMessage("I love all these buildings!"), 2.0d)
                        .append(new Dialogue(plugin, formatMessage("They make it so easy to swing around."), 3.0d)),
                new Dialogue(plugin, formatMessage("I wonder if there's a feast around here?"), 3.0d)
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                peteLines[random.nextInt(peteLines.length)].play(team, () -> {
                    team.setInDialogue(false);
                });
            }
        });
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return PeteTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PeteTrait extends Trait {
        public PeteTrait() {
            super(TRAIT_NAME);
        }
    }

}
