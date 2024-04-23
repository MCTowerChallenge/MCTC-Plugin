package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Alice extends QuestCharacter {

    public static final String NAME = "Alice";
    public static final Color NAME_COLOR = new Color(0x0d5e84);
    public static final Color TEXT_COLOR = new Color(0x006f9e);
    public static final String TRAIT_NAME = "alice";

    public Alice(Plugin plugin) {
        super(plugin, EntityType.ALLAY, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue aliceConversation = new Dialogue(plugin, formatMessage("I know you're excited, but we have to keep this down low!"), 4.0d);
        aliceConversation.append(new Dialogue(plugin, formatMessage("You know they don't want us snooping around down there."), 3.0d));

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                aliceConversation.play(team, () -> {
                    team.setInDialogue(false);
                });
            }
        });
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        return super.setNPCProperties(npc);
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return AliceTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class AliceTrait extends Trait {
        public AliceTrait() {
            super(TRAIT_NAME);
        }
    }

}
