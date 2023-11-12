package io.github.mctowerchallenge.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Boney extends QuestCharacter {

    public static final String NAME = "Boney";
    public static final Color NAME_COLOR = new Color(0x7694a5);
    public static final Color TEXT_COLOR = new Color(0x879aa5);
    public static final String TRAIT_NAME = "boney";

    public Boney(Plugin plugin) {
        super(plugin, EntityType.WITHER_SKELETON, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return BoneyTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class BoneyTrait extends Trait {
        public BoneyTrait() {
            super(TRAIT_NAME);
        }
    }

}
