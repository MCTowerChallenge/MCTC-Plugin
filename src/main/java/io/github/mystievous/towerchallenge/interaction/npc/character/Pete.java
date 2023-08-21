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

public class Pete extends QuestCharacter {

    public static final String NAME = "Pete";
    public static final Color NAME_COLOR = new Color(0xd92521);
    public static final Color TEXT_COLOR = new Color(0xbe3430);
    public static final String TRAIT_NAME = "pete";

    public Pete(Plugin plugin) {
        super(plugin, EntityType.CAVE_SPIDER, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue peteStart = new Dialogue(plugin, formatMessage("Oh hey, what're you doing down here?"), 3.0d);

        Dialogue peteTunnel = new Dialogue(plugin, formatMessage("Oh woah hello there, way to make an entrance!"), 4.0d);
        peteTunnel.append(new Dialogue(plugin, formatMessage("Didn't even know there was a tunnel behind that."), 2.5d));

        addQuestInteractionHandler(QuestManager.NO_QUEST, (team, npcRightClickEvent) -> {
        });
        addQuestInteractionHandler(QuestManager.BAND_TROUBLE, (team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                peteStart.play(team, () -> {
                    team.setInDialogue(false);
                });
            }
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team != null) {
                if (team.canStartDialogue()) {
                    team.setInDialogue(true);
                    peteTunnel.play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
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
        return PeteTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PeteTrait extends Trait {
        public PeteTrait() {
            super(TRAIT_NAME);
        }
    }

}
