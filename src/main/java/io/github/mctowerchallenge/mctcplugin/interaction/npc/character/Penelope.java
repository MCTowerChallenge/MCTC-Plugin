package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
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

import java.security.SecureRandom;
import java.util.Random;

public class Penelope extends QuestCharacter {

    public static final String NAME = "Penelope";
    public static final Color NAME_COLOR = new Color(0x932172);
    public static final Color TEXT_COLOR = new Color(0xbc519c);
    public static final String TRAIT_NAME = "penelope";

    private final Random random = new SecureRandom();

    public Penelope(Plugin plugin) {
        super(plugin, EntityType.HORSE, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] penelopeLines = {
                new Dialogue(plugin, formatMessage("Hi! It's nice to see you, happy new year!"), 3.5d)
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                penelopeLines[random.nextInt(penelopeLines.length)].play(team, () -> {
                    team.setInDialogue(false);
                });
            }
        });
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        HorseModifiers horseModifiers = npc.getOrAddTrait(HorseModifiers.class);
        horseModifiers.setSaddle(new ItemStack(Material.SADDLE));
        horseModifiers.setColor(Horse.Color.BLACK);
        horseModifiers.setStyle(Horse.Style.WHITE);
        Controllable controllable = npc.getOrAddTrait(Controllable.class);
        controllable.setEnabled(false);
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
