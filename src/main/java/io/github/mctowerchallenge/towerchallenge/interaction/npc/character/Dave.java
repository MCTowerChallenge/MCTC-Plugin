package io.github.mctowerchallenge.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Dave extends QuestCharacter {

    public static final String NAME = "Dave";
    public static final Color NAME_COLOR = new Color(0x663b5a);
    public static final Color TEXT_COLOR = new Color(0xca60ad);
    public static final String TRAIT_NAME = "dave";

    public Dave(Plugin plugin) {
        super(plugin, EntityType.STRIDER, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return DaveTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class DaveTrait extends Trait {
        public DaveTrait() {
            super(TRAIT_NAME);
        }
    }

}
