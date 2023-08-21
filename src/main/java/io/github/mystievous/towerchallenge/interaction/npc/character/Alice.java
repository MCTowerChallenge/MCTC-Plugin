package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
import io.github.mystievous.towerchallenge.interaction.npc.QuestCharacter;
import io.github.mystievous.towerchallenge.quest.QuestManager;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.LookClose;
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

        Dialogue aliceSearch = new Dialogue(plugin, formatMessage("Ughh where is it??"), 2.0d);
        aliceSearch.append(new Dialogue(plugin, formatMessage("Where the nether is it???"), 2.0d));
        aliceSearch.append(new Dialogue(plugin, formatMessage("Where did I leave that old thing?"), 3.0d));
        aliceSearch.append(new Dialogue(plugin, formatMessage("Hey, do you mind?!"), 2.0d));

        addQuestInteractionHandler(QuestManager.NO_QUEST, (team, event) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                aliceSearch.play(team, () -> {
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
