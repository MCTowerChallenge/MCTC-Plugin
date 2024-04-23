package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.hideentity.HiddenEntityManager;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.Color;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.VillagerProfession;
import net.citizensnpcs.trait.versioned.VillagerTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GenericBeeConservationistMan extends QuestCharacter {

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {
    });

    public static final String NAME = "Generic Bee Conservationist Man";
    public static final Color NAME_COLOR = new Color(0x003FD4);
    public static final Color TEXT_COLOR = new Color(0x0036A4);
    public static final String TRAIT_NAME = "genericbeeconservationistman";

    public GenericBeeConservationistMan(Plugin plugin) {
        super(plugin, EntityType.VILLAGER, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue genericBeeConservationistManStartDialogue = new Dialogue(plugin, formatMessage(Component.text("Bring 5 bee nests pls \uD83D\uDC49\uD83D\uDC48")), 2.0d);

        Dialogue genericBeeConservationistManIdleDialogue = new Dialogue(plugin, formatMessage(Component.text("bees")), 2.0d);

        Dialogue collectNestsCompleteDialogue = new Dialogue(plugin, formatMessage(Component.text("Thanks, go in now")), 2.0d);

        setDefaultInteractionHandler((team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                genericBeeConservationistManIdleDialogue.play(team, () -> team.setInDialogue(false));
            }
        });

        addQuestInteractionHandler(QuestTags.GENERIC_BEE_CONSERVATIONIST_START, (team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                genericBeeConservationistManStartDialogue.play(team, () -> {
                    team.setInDialogue(false);
                    boolean completed = team.completeQuest(QuestTags.GENERIC_BEE_CONSERVATIONIST_START);
                    if (completed) {
                        team.setQuest(QuestTags.COLLECT_NESTS);
                    }
                });
            }
        });

        addQuestInteractionHandler(QuestTags.COLLECT_NESTS, (team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                int nests = getNests(team);
                if (nests < 5) {
                    int needed = 5 - nests;
                    Player player = npcRightClickEvent.getClicker();
                    PlayerInventory inventory = player.getInventory();
                    HashMap<Integer, ? extends ItemStack> items = inventory.all(Material.BEE_NEST);
                    for (var entry : items.entrySet()) {
                        ItemStack item = entry.getValue();
                        int amount = item.getAmount();
                        int finalNeeded = needed;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                item.subtract(finalNeeded);
                            }
                        }.runTask(plugin);
                        needed = Math.max(needed - amount, 0);
                        if (needed == 0) break;
                    }
                    int addedNests = 5 - nests - needed;
                    addNests(team, addedNests);
                }
                nests = getNests(team);
                if (nests < 5) {
                    int needed = 5 - nests;
                    Dialogue moreDialogue = new Dialogue(plugin, formatMessage(Component.text("I still need " + needed + " more")), 2.0d);
                    moreDialogue.play(team, () -> team.setInDialogue(false));
                } else {
                    collectNestsCompleteDialogue.play(team, () -> {
                        team.setInDialogue(false);
                        boolean completed = team.completeQuest(QuestTags.COLLECT_NESTS);
                        if (completed) {
                            team.setQuest(QuestTags.NO_QUEST);
                            hideTape(team);
                        }
                    });
                }
            }
        });
    }

    public static final Location tapeLocation = new Location(Worlds.May2024(), 582, 54, 413);

    public static void hideTape(TowerTeam team) {
        tapeLocation.getChunk().load();
        for (Entity entity : Bukkit.selectEntities(sender, "@e[tag=bee-tape]")) {
            team.hideEntity(entity);
        }
    }

    private static final String objectiveTag = "nests";

    public static int getNests(TowerTeam team) {
        return team.getObjective(QuestTags.COLLECT_NESTS, objectiveTag);
    }

    public static void addNests(TowerTeam team, int number) {
        team.addObjectiveScore(QuestTags.COLLECT_NESTS, objectiveTag, number);
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
        return GenericBeeConservationistManTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class GenericBeeConservationistManTrait extends Trait {
        public GenericBeeConservationistManTrait() {
            super(TRAIT_NAME);
        }
    }

}
