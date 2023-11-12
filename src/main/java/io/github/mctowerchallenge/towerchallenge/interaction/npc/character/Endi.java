package io.github.mctowerchallenge.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
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
