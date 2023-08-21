package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
import io.github.mystievous.towerchallenge.interaction.npc.QuestCharacter;
import io.github.mystievous.towerchallenge.quest.QuestManager;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.LookClose;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PolarW extends QuestCharacter {

    public static final String NAME = "Polar W.";
    public static final Color NAME_COLOR = new Color(0x70c8d4);
    public static final Color TEXT_COLOR = new Color(0x96e4ee);
    public static final String TRAIT_NAME = "polarw";

    public PolarW(Plugin plugin) {
        super(plugin, EntityType.POLAR_BEAR, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue polarInvestigate = new Dialogue(plugin, formatMessage("I can't believe this!"), 2.75d);
        polarInvestigate.append(formatMessage("We spent so long getting ready for this performance and it had to just get ruined like this."), 5.5d);
        polarInvestigate.append(formatMessage("We have to clean up, fix the light that almost annihilated poor Endi and the wire for Boney’s amp, and now also apparently find Percy’s drumsticks too!"), 11.0d);
        polarInvestigate.append(formatMessage(Component.text("What're we gonna do.. what're we gonna do...").decoration(TextDecoration.ITALIC, true)), 4.0d);
        addQuestInteractionHandler(QuestManager.NO_QUEST, (towerTeam, npcRightClickEvent) -> {
        });
        setDefaultInteractionHandler((team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                polarInvestigate.play(team, () -> team.setInDialogue(false));
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
