package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Henry extends QuestCharacter {

    public static final String NAME = "Henry";
    public static final Color NAME_COLOR = new Color(0xa9ab6a);
    public static final Color TEXT_COLOR = new Color(0xc4c58e);
    public static final String TRAIT_NAME = "henry";

    public Henry(Plugin plugin) {
        super(plugin, EntityType.HUSK, NAME, NAME_COLOR, TEXT_COLOR);
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
