package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.versioned.MushroomCowTrait;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

public class Moollicient extends QuestCharacter {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final String NAME = "Moollicient";
    public static final Color NAME_COLOR = new Color(0xffd632);
    public static final Color TEXT_COLOR = new Color(0xe6c545);
    public static final String TRAIT_NAME = "moollicient";

    public Moollicient(Plugin plugin) {
        super(plugin, EntityType.MUSHROOM_COW, NAME, NAME_COLOR, TEXT_COLOR);


        Dialogue moollicientBlank = new Dialogue(plugin, formatMessage("..."), 1.0d);
        Dialogue moollicientSideEye = new Dialogue(plugin, actionMessage("gives you side eye"), 2.5d);
        moollicientSideEye.setSoundKey(Key.key(MCTCPlugin.MCTC_NAMESPACE, "vine"));

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, npcRightClickEvent) -> {
        });
        addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        setDefaultInteractionHandler((team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                if (RANDOM.nextInt(10) == 0) {
                    moollicientSideEye.play(team, () -> team.setInDialogue(false));
                } else {
                    moollicientBlank.play(team, () -> team.setInDialogue(false));
                }
            }
        });

    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        MushroomCowTrait mushroomCowTrait = npc.getOrAddTrait(MushroomCowTrait.class);
        mushroomCowTrait.setVariant(MushroomCow.Variant.RED);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return MoollicientTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class MoollicientTrait extends Trait {
        public MoollicientTrait() {
            super(TRAIT_NAME);
        }
    }

}
