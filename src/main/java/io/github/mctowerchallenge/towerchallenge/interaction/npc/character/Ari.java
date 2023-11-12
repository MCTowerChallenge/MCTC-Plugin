package io.github.mctowerchallenge.towerchallenge.interaction.npc.character;

import io.github.mctowerchallenge.towerchallenge.quest.QuestManager;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.Dialogue;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.versioned.ParrotTrait;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

public class Ari extends QuestCharacter {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final String NAME = "Ari";
    public static final Color NAME_COLOR = new Color(0x118c9b);
    public static final Color TEXT_COLOR = new Color(0x02a2b5);
    public static final String TRAIT_NAME = "ari";

    public Ari(Plugin plugin) {
        super(plugin, EntityType.PARROT, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] ariLines = {
                new Dialogue(plugin, formatMessage("Hi there! Hi there! Hi there!"), 1.5d),
                new Dialogue(plugin, formatMessage("Wha happa? Wha happa?"), 1.5d),
                new Dialogue(plugin, formatMessage("Salty treat? Salty treat?"), 1.5d),
                new Dialogue(plugin, formatMessage("Yippee!"), 1.5d)
        };

        addQuestInteractionHandler(QuestManager.NO_QUEST, (team, event) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                ariLines[RANDOM.nextInt(ariLines.length)].play(team, () -> {
                    team.setInDialogue(false);
                });
            }
        });
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        ParrotTrait parrotTrait = npc.getOrAddTrait(ParrotTrait.class);
        parrotTrait.setVariant(Parrot.Variant.BLUE);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return AriTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class AriTrait extends Trait {
        public AriTrait() {
            super(TRAIT_NAME);
        }
    }

}
