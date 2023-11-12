package io.github.mctowerchallenge.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the Steve Skellington character with specific interactions, traits, and dialogue.
 */
public class SteveSkellington extends QuestCharacter {

    // Character attributes
    public static final String NAME = "steve skellington";
    public static final Color NAME_COLOR = new Color(0x399c91);
    public static final Color TEXT_COLOR = new Color(0x55b4aa);
    public static final String REGION = "steve";
    public static final String TRAIT_NAME = "steveskellington";

    /**
     * Creates a new SteveSkellington instance.
     *
     * @param plugin The plugin instance.
     */
    public SteveSkellington(Plugin plugin) {
        super(plugin, EntityType.SKELETON, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return SteveTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class SteveTrait extends Trait {

        public SteveTrait() {
            super(TRAIT_NAME);
        }

    }

}
