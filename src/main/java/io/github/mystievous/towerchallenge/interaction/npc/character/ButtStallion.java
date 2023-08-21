package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.Controllable;
import net.citizensnpcs.trait.HorseModifiers;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ButtStallion extends QuestCharacter {

    public static final String NAME = "Butt Stallion";
    public static final Color NAME_COLOR = new Color(0x70b5a6);
    public static final Color TEXT_COLOR = new Color(0x508277);
    public static final String TRAIT_NAME = "buttstallion";

    public ButtStallion(Plugin plugin) {
        super(plugin, EntityType.HORSE, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        HorseModifiers horseModifiers = npc.getOrAddTrait(HorseModifiers.class);
        horseModifiers.setSaddle(new ItemStack(Material.SADDLE));
        horseModifiers.setColor(Horse.Color.WHITE);
        horseModifiers.setStyle(Horse.Style.NONE);
        Controllable controllable = npc.getOrAddTrait(Controllable.class);
        controllable.setEnabled(true);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return ButtStallionTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class ButtStallionTrait extends Trait {

        public ButtStallionTrait() {
            super(TRAIT_NAME);
        }

    }

}
