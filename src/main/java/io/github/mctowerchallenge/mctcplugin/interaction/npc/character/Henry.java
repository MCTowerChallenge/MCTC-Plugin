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

public class Henry extends QuestCharacter {

    public static final String NAME = "Henry";
    public static final Color NAME_COLOR = new Color(0xa9ab6a);
    public static final Color TEXT_COLOR = new Color(0xc4c58e);
    public static final String TRAIT_NAME = "henry";

    private final Random random = new SecureRandom();

    public Henry(Plugin plugin) {
        super(plugin, EntityType.HUSK, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] henryLines = {
                new Dialogue(plugin, formatMessage("Woah, these buildings are hollow!"), 2.5d)
                        .append(new Dialogue(plugin, formatMessage("You could probably fit a lot in here."), 3.0d))
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                henryLines[random.nextInt(henryLines.length)].play(team, () -> {
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
        return HenryTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class HenryTrait extends Trait {
        public HenryTrait() {
            super(TRAIT_NAME);
        }
    }

}
