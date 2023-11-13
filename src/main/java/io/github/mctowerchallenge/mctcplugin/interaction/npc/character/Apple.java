package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.PlayerQuestCharacter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Apple extends PlayerQuestCharacter {

    public static final String NAME = "apple270";
    public static final Color NAME_COLOR = new Color(0xf98b33);
    public static final Color TEXT_COLOR = new Color(0xffb142);
    public static final String TRAIT_NAME = "apple";

    public Apple(Plugin plugin, TowerTeam team) {
        super(plugin, team, NAME, NAME_COLOR, TEXT_COLOR);
    }

    public Apple(Plugin plugin) {
        super(plugin, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return AppleTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class AppleTrait extends Trait {
        public AppleTrait() {
            super(TRAIT_NAME);
        }
    }

}
