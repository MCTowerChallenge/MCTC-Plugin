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

public class Boney extends QuestCharacter {

    public static final String NAME = "Boney";
    public static final Color NAME_COLOR = new Color(0x7694a5);
    public static final Color TEXT_COLOR = new Color(0x879aa5);
    public static final String TRAIT_NAME = "boney";

    private final Random random = new SecureRandom();

    public Boney(Plugin plugin) {
        super(plugin, EntityType.WITHER_SKELETON, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] boneyLines = {
                new Dialogue(plugin, formatMessage("How do you think that sounded, Percy?"), 3.0d)
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                boneyLines[random.nextInt(boneyLines.length)].play(team, () -> {
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
        return BoneyTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class BoneyTrait extends Trait {
        public BoneyTrait() {
            super(TRAIT_NAME);
        }
    }

}
