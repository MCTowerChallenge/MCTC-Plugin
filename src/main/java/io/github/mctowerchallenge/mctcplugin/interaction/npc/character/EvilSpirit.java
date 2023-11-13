package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class EvilSpirit extends QuestCharacter {

    public static final String TRAIT_NAME = "evilspirit";
    public static final String NAME = "Evil Spirit";
    public static final Color NAME_COLOR = new Color(0x610b1f);
    public static final Color TEXT_COLOR = new Color(0x870f2b);

    public EvilSpirit(Plugin plugin) {
        super(plugin, EntityType.BLAZE, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return EvilSpiritTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class EvilSpiritTrait extends Trait {

        public EvilSpiritTrait() {
            super(TRAIT_NAME);
        }

        @Override
        public void onSpawn() {
            super.onSpawn();
            Entity entity = npc.getEntity();
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1, false, false, false));
            }
        }
    }

}
