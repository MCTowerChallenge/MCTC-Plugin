package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.versioned.MushroomCowTrait;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Soup extends QuestCharacter {

    public static final String NAME = "Soup";
    public static final Color NAME_COLOR = new Color(0xbc775d);
    public static final Color TEXT_COLOR = new Color(0xd67856);
    public static final String TRAIT_NAME = "soup";

    public Soup(Plugin plugin) {
        super(plugin, EntityType.MUSHROOM_COW, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        MushroomCowTrait mushroomCowTrait = npc.getOrAddTrait(MushroomCowTrait.class);
        mushroomCowTrait.setVariant(MushroomCow.Variant.BROWN);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return SoupTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class SoupTrait extends Trait {
        public SoupTrait() {
            super(TRAIT_NAME);
        }
    }

}
