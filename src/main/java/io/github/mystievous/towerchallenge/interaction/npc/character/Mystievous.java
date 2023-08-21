package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.interaction.npc.PlayerQuestCharacter;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Mystievous extends PlayerQuestCharacter {

    public static final String NAME = "Mystievous";
    public static final Color NAME_COLOR = new Color(0xc73858);
    public static final Color TEXT_COLOR = new Color(0xd2607a);
    public static final String TRAIT_NAME = "mystievous";

    public Mystievous(Plugin plugin, TowerTeam team) {
        super(plugin, team, NAME, NAME_COLOR, TEXT_COLOR);
    }

    public Mystievous(Plugin plugin) {
        super(plugin, NAME, NAME_COLOR, TEXT_COLOR);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return MystievousTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class MystievousTrait extends Trait {

        public MystievousTrait() {
            super(TRAIT_NAME);
        }

    }

}
