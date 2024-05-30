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

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class GenericBeeConservationistMan extends QuestCharacter {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {
    });

    public static final String NAME = "Generic Bee Conservationist Man";
    public static final Color NAME_COLOR = new Color(0x0084d4);
    public static final Color TEXT_COLOR = new Color(0x006fa4);
    public static final String TRAIT_NAME = "genericbeeconservationistman";

    public GenericBeeConservationistMan(Plugin plugin) {
        super(plugin, EntityType.VILLAGER, NAME, NAME_COLOR, TEXT_COLOR);

        Dialogue startLine1 = new Dialogue(plugin, formatMessage(Component.text("I just can't bee-lieve that all the wonderful bees vanished!")), 6.0d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.start_quest.line_1.3"));

        Dialogue startLine5 = new Dialogue(plugin, formatMessage(Component.text("I told everyone we didn't appreciate our buzzing friends enough.")), 4.5d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.start_quest.line_5.2"));
        startLine1.setNext(startLine5);

        Dialogue startLine2 = new Dialogue(plugin, formatMessage(Component.text("If only I had more abandoned beehives to research, maybe I could figure out where they went.")), 7.5d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.start_quest.line_2.1"));
        startLine5.setNext(startLine2);

        Dialogue startLine3 = new Dialogue(plugin, formatMessage(Component.text("Oh, hey you!")), 1.75d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.start_quest.line_3.2"));
        startLine2.setNext(startLine3);

        Dialogue startLine4 = new Dialogue(plugin, formatMessage(Component.text("Can you get me some beehives while you're out exploring?")), 4.5d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.start_quest.line_4.1"));
        startLine3.setNext(startLine4);


        Dialogue collectIdleLine1 = new Dialogue(plugin, formatMessage(Component.text("Yeah I know Generic Maintenance Man, he's my cousin.")), 4.5d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.collect_idle.line_1.2"));

        Dialogue collectIdleLine2 = new Dialogue(plugin, formatMessage(Component.text("How dare you say I am just like him, that is a deeply offensive stereotype.")), 7.0d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.collect_idle.line_2.2"));
        collectIdleLine1.setNext(collectIdleLine2);

        Dialogue collectIdleLine3 = new Dialogue(plugin, formatMessage(Component.text("We are absolutely nothing alike! Now go get me those beehives, buzz off.")), 5.5d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.collect_idle.line_3.2"));
        collectIdleLine2.setNext(collectIdleLine3);


        Dialogue giveHivesLine1 = new Dialogue(plugin, formatMessage(Component.text("Finally, that took you long enough!")), 3.5d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_1.1"));

        Dialogue giveHivesLine2 = new Dialogue(plugin, formatMessage(Component.text("Don't you know bees are important here?")), 3.0d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_2.2"));
        giveHivesLine1.setNext(giveHivesLine2);

        Dialogue giveHivesLine3 = new Dialogue(plugin, formatMessage(Component.text("Anyway... While you were off collecting blocks for the \"competition\", I've been doing research.")), 7.0d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_3.1"));
        giveHivesLine2.setNext(giveHivesLine3);

        Dialogue giveHivesLine4 = new Dialogue(plugin, formatMessage(Component.text("Y'know...")), 1d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_4.1"));
        giveHivesLine3.setNext(giveHivesLine4);

        Dialogue giveHivesLine5 = new Dialogue(plugin, formatMessage(Component.text("Helpful shit...")), 1.75d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_5.1"));
        giveHivesLine4.setNext(giveHivesLine5);

        Dialogue giveHivesLine6 = new Dialogue(plugin, formatMessage(Component.text("That people care about...")), 1.90d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_6.1"));
        giveHivesLine5.setNext(giveHivesLine6);

        Dialogue giveHivesLine7 = new Dialogue(plugin, formatMessage(Component.text("That matters... that, I don't know, might save the day someday.")), 6.0d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_7.1"));
        giveHivesLine6.setNext(giveHivesLine7);

        Dialogue giveHivesLine8 = new Dialogue(plugin, formatMessage(Component.text("Anyways...")), 1.1d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_8.1"));
        giveHivesLine7.setNext(giveHivesLine8);

        Dialogue giveHivesLine9 = new Dialogue(plugin, formatMessage(Component.text("I found that the bees left to go live in a world of their own making, where they would not get taken for granted.")), 7.5d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_9.2"));
        giveHivesLine8.setNext(giveHivesLine9);

        Dialogue giveHivesLine10 = new Dialogue(plugin, formatMessage(Component.text("With these last 5 hives, I should be able to open a portal to their world.")), 6.0d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_10.2"));
        giveHivesLine9.setNext(giveHivesLine10);

        Dialogue giveHivesLine11 = new Dialogue(plugin, formatMessage(Component.text("Get the fuck in there, apologise. Get- Y'know let them know they're loved, let them know they're appreciated. Bring 'em home. We miss them.")), 7.5d)
                .setSoundKey(MCTCPlugin.key("genericbeeconservationistman.may2024.give_bee_hives.line_11.2"));
        giveHivesLine10.setNext(giveHivesLine11);

        Dialogue[] finishedIdle = new Dialogue[]{
                new Dialogue(plugin, formatMessage(Component.text("What're you doing sitting talking to me for? Get a move on already!")), 4.5d),
                new Dialogue(plugin, formatMessage(Component.text("The fuck are you still doing here? Buzz off! I'm doing research here!")), 5.5d),
                new Dialogue(plugin, formatMessage(Component.text("Ey! I'm researching here! Get a move on!")), 4.5d),
        };

        addQuestInteractionHandler(QuestTags.GENERIC_BEE_CONSERVATIONIST_START, (team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                startLine1.play(team, () -> {
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
                Player player = npcRightClickEvent.getClicker();
                if (!player.getInventory().contains(Material.BEE_NEST)) {
                    collectIdleLine1.play(team, () -> team.setInDialogue(false));
                    return;
                }
                int nests = getNests(team);
                if (nests < 5) {
                    int needed = 5 - nests;
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
                    Dialogue moreDialogue = new Dialogue(plugin, formatMessage(Component.text("Thanks! I still need " + needed + " more nests.")), 2.0d);
                    moreDialogue.play(team, () -> team.setInDialogue(false));
                } else {
                    giveHivesLine1.play(team, () -> {
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

        addQuestInteractionHandler(QuestTags.NO_QUEST, (team, npcRightClickEvent) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                finishedIdle[RANDOM.nextInt(finishedIdle.length)].play(team, () -> team.setInDialogue(false));
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
