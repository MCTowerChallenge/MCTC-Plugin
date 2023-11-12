package io.github.mctowerchallenge.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
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
