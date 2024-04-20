package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import io.github.mystievous.mysticore.Color;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.VillagerProfession;
import net.citizensnpcs.trait.versioned.VillagerTrait;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class GenericBeeConservationalistMan extends QuestCharacter {

    public static final String NAME = "Generic Bee Conservationalist Man";
    public static final Color NAME_COLOR = new Color(0x003FD4);
    public static final Color TEXT_COLOR = new Color(0x0036A4);
    public static final String TRAIT_NAME = "genericbeeconservationalistman";

    public GenericBeeConservationalistMan(Plugin plugin) {
        super(plugin, EntityType.VILLAGER, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        VillagerProfession professionTrait = npc.getOrAddTrait(VillagerProfession.class);
        professionTrait.setProfession(Villager.Profession.FARMER);
        VillagerTrait villagerTrait = npc.getOrAddTrait(VillagerTrait.class);
        villagerTrait.setType(Villager.Type.DESERT);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return GenericBeeConservationalistManTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class GenericBeeConservationalistManTrait extends Trait {
        public GenericBeeConservationalistManTrait() {
            super(TRAIT_NAME);
        }
    }

}
