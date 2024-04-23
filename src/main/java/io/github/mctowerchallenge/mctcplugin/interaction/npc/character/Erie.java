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

public class Erie extends QuestCharacter {

    public static final String NAME = "Erie";
    public static final Color NAME_COLOR = new Color(0x89b847);
    public static final Color TEXT_COLOR = new Color(0xa5d166);
    public static final String TRAIT_NAME = "erie";

    private final Random random = new SecureRandom();

    public Erie(Plugin plugin) {
        super(plugin, EntityType.WITCH, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] lines = {
                new Dialogue(plugin, formatMessage("I sure love this cool air!"), 3.0d),
                new Dialogue(plugin, formatMessage("It's so great to just walk around."), 3.5d)
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                lines[random.nextInt(lines.length)].play(team, () -> {
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
        return ErieTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class ErieTrait extends Trait {
        public ErieTrait() {
            super(TRAIT_NAME);
        }
    }
}
