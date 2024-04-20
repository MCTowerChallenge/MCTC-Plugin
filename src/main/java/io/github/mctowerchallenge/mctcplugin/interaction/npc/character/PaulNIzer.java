package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import io.github.mystievous.mysticore.Color;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PaulNIzer extends QuestCharacter {

    public static final String NAME = "Paul N Izer";
    public static final Color NAME_COLOR = new Color(0xd17507);
    public static final Color TEXT_COLOR = new Color(0xdb8e10);
    public static final String TRAIT_NAME = "paulnizer";

    public PaulNIzer(Plugin plugin) {
        super(plugin, EntityType.BEE, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return PaulNIzerTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PaulNIzerTrait extends Trait {
        public PaulNIzerTrait() {
            super(TRAIT_NAME);
        }
    }

}
