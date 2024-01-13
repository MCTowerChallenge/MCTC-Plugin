package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Random;

public class PolarW extends QuestCharacter {

    public static final String NAME = "Polar W.";
    public static final Color NAME_COLOR = new Color(0x70c8d4);
    public static final Color TEXT_COLOR = new Color(0x96e4ee);
    public static final String TRAIT_NAME = "polarw";

    private final Random random = new SecureRandom();

    public PolarW(Plugin plugin) {
        super(plugin, EntityType.POLAR_BEAR, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] polarLines = {
                new Dialogue(plugin, formatMessage("These flowers are so pretty!"), 3.0d),
                new Dialogue(plugin, formatMessage("Isn't this beautiful?"), 2.5d),
                new Dialogue(plugin, formatMessage("It's amazing how healthy these are."), 3.0d),
                new Dialogue(plugin, formatMessage("*Achoo*"), 1.0d)
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                polarLines[random.nextInt(polarLines.length)].play(team, () -> {
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
        return PolarWTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PolarWTrait extends Trait {
        public PolarWTrait() {
            super(TRAIT_NAME);
        }
    }

}
