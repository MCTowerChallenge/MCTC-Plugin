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

public class Endi extends QuestCharacter {

    public static final String NAME = "Endi";
    public static final Color NAME_COLOR = new Color(0xc426d9);
    public static final Color TEXT_COLOR = new Color(0xde44f2);
    public static final String TRAIT_NAME = "endi";

    public Endi(Plugin plugin) {
        super(plugin, EntityType.ENDERMAN, NAME, NAME_COLOR, TEXT_COLOR);

        String glass = "glass";
        String endiTalk = "endi-talk";

        Dialogue endiInvestigate = new Dialogue(plugin, formatMessage("Oh, hi there..."), 2.5d);
        endiInvestigate.append(formatMessage("I'm alright now, thank you."), 3.5d);
        endiInvestigate.append(formatMessage("The light didn't land too close, it was just scary.."), 4.5d);
        endiInvestigate.append(formatMessage("I'm worried about Percy though, he seems really upset about his drumsticks."), 5.0d);
        endiInvestigate.append(formatMessage("I'd go check on him but steve should be back any second with a broom and new glass to fix this up... hopefully"), 7.0d);
        endiInvestigate.append(formatMessage("Would you mind asking how he's doing for me please?"), 3.5d);
        addQuestInteractionHandler(QuestManager.NO_QUEST, (team, npcRightClickEvent) -> {
        });
        setDefaultInteractionHandler((team, npcRightClickEvent) -> {
            if (team.getObjective(QuestManager.BAND_TROUBLE, glass) == 0 && team.canStartDialogue()) {
                team.setInDialogue(true);
                team.addObjectiveScore(QuestManager.BAND_TROUBLE, endiTalk, 1);
                endiInvestigate.play(team, () -> team.setInDialogue(false));
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
        return EndiTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class EndiTrait extends Trait {
        public EndiTrait() {
            super(TRAIT_NAME);
        }
    }

}
