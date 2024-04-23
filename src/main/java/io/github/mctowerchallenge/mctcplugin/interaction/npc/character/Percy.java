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

public class Percy extends QuestCharacter {

    public static final String NAME = "Percy";
    public static final Color NAME_COLOR = new Color(0xc63d20);
    public static final Color TEXT_COLOR = new Color(0xe0573a);
    public static final String TRAIT_NAME = "percy";

    private final Random random = new SecureRandom();

    public Percy(Plugin plugin) {
        super(plugin, EntityType.PIGLIN_BRUTE, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] percyLines = {
                new Dialogue(plugin, formatMessage("I think that's great!"), 2.0d)
                        .append(new Dialogue(plugin, formatMessage("You've really improved a lot since June."), 3.5d))
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                percyLines[random.nextInt(percyLines.length)].play(team, () -> {
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
        return PercyTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PercyTrait extends Trait {

        public PercyTrait() {
            super(TRAIT_NAME);
        }
    }

}
