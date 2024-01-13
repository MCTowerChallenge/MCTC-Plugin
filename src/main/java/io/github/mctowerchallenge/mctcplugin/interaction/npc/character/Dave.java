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

public class Dave extends QuestCharacter {

    public static final String NAME = "Dave";
    public static final Color NAME_COLOR = new Color(0x663b5a);
    public static final Color TEXT_COLOR = new Color(0xca60ad);
    public static final String TRAIT_NAME = "dave";

    private final Random random = new SecureRandom();

    public Dave(Plugin plugin) {
        super(plugin, EntityType.STRIDER, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] daveLines = {
                new Dialogue(plugin, formatMessage("Hey! Happy new year!"), 2.0d),
                new Dialogue(plugin, formatMessage("Hope you're having a blast!"), 2.5d)
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                daveLines[random.nextInt(daveLines.length)].play(team, () -> {
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
        return DaveTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class DaveTrait extends Trait {
        public DaveTrait() {
            super(TRAIT_NAME);
        }
    }

}
