package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.Controllable;
import net.citizensnpcs.trait.HorseModifiers;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Penelope extends QuestCharacter {

    public static final String NAME = "Penelope";
    public static final Color NAME_COLOR = new Color(0x932172);
    public static final Color TEXT_COLOR = new Color(0xbc519c);
    public static final String TRAIT_NAME = "penelope";

    public Penelope(Plugin plugin) {
        super(plugin, EntityType.HORSE, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        HorseModifiers horseModifiers = npc.getOrAddTrait(HorseModifiers.class);
        horseModifiers.setSaddle(new ItemStack(Material.SADDLE));
        horseModifiers.setColor(Horse.Color.BLACK);
        horseModifiers.setStyle(Horse.Style.WHITE);
        Controllable controllable = npc.getOrAddTrait(Controllable.class);
        controllable.setEnabled(true);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return PenelopeTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PenelopeTrait extends Trait {

        public PenelopeTrait() {
            super(TRAIT_NAME);
        }

    }

}
