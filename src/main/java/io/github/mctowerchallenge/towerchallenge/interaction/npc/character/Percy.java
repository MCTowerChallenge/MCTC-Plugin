package io.github.mctowerchallenge.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Percy extends QuestCharacter {

    public static final String NAME = "Percy";
    public static final Color NAME_COLOR = new Color(0xc63d20);
    public static final Color TEXT_COLOR = new Color(0xe0573a);
    public static final String TRAIT_NAME = "percy";

    public Percy(Plugin plugin) {
        super(plugin, EntityType.PIGLIN_BRUTE, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return PercyTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PercyTrait extends Trait {

        public PercyTrait() {
            super(TRAIT_NAME);
        }
    }

}
