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

public class Erie extends QuestCharacter {

    public static final String NAME = "Erie";
    public static final Color NAME_COLOR = new Color(0x89b847);
    public static final Color TEXT_COLOR = new Color(0xa5d166);
    public static final String TRAIT_NAME = "erie";

    public Erie(Plugin plugin) {
        super(plugin, EntityType.WITCH, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return ErieTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class ErieTrait extends Trait {
        public ErieTrait() {
            super(TRAIT_NAME);
        }
    }
}
