package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.god.GodTeam;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.Controllable;
import net.citizensnpcs.trait.HorseModifiers;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

import java.security.SecureRandom;
import java.util.Random;

public class ButtStallion extends QuestCharacter {

    public static final String NAME = "Butt Stallion";
    public static final Color NAME_COLOR = new Color(0x70b5a6);
    public static final Color TEXT_COLOR = new Color(0x508277);
    public static final String TRAIT_NAME = "buttstallion";

    private final Random random = new SecureRandom();

    public ButtStallion(Plugin plugin) {
        super(plugin, EntityType.HORSE, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue[] buttStallionLines = {
                new Dialogue(plugin, formatMessage("Can't wait for this party tonight!"), 3.0d)
                        .append(new Dialogue(plugin, formatMessage("It's gonna totally rock!"), 3.0d)),
        };

        addQuestInteractionHandler(QuestTags.NOT_STARTED, (team, event) -> {
        });
        addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                buttStallionLines[random.nextInt(buttStallionLines.length)].play(team, () -> {
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
        horseModifiers.setColor(Horse.Color.WHITE);
        horseModifiers.setStyle(Horse.Style.NONE);
        Controllable controllable = npc.getOrAddTrait(Controllable.class);
        controllable.setEnabled(false);
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
