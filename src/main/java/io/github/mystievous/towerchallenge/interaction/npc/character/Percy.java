package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
import io.github.mystievous.towerchallenge.interaction.npc.QuestCharacter;
import io.github.mystievous.towerchallenge.quest.QuestItems;
import io.github.mystievous.towerchallenge.quest.QuestManager;
import io.github.mystievous.towerchallenge.quest.QuestUtil;
import io.github.mystievous.towerchallenge.quest.util.FullInventory;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.LookClose;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Percy extends QuestCharacter {

    public static final String NAME = "Percy";
    public static final Color NAME_COLOR = new Color(0xc63d20);
    public static final Color TEXT_COLOR = new Color(0xe0573a);
    public static final String TRAIT_NAME = "percy";

    private final String drumsticks;
    private final ItemStack drumstickItem;

    public Percy(Plugin plugin) {
        super(plugin, EntityType.PIGLIN_BRUTE, NAME, NAME_COLOR, TEXT_COLOR);

        String percyTalk = "percy-talk";
        drumsticks = "drumsticks";

        drumstickItem = GuiUtil.formatItem("Drumsticks", Material.STICK, 5);
        NBTUtils.setBool(plugin, drumsticks, drumstickItem);
        NBTUtils.noStack(plugin, drumstickItem);
        NBTUtils.setNoUse(drumstickItem);
        TextUtil.appendQuestItemLore(drumstickItem);
        QuestItems.putItem(drumsticks, drumstickItem);

        String drumstickIndividual = "drumstick-individual";
        ItemStack drumstickIndividualItem = GuiUtil.formatItem("Drumstick", Material.STICK, 4);
        QuestItems.putItem(drumstickIndividual, drumstickIndividualItem);

        Dialogue percyInvestigate = new Dialogue(plugin, formatMessage("Man, this is such a drag."), 2.5d);
        percyInvestigate.append(formatMessage("Can you believe someone stole my lucky drumsticks?"), 4.0d);
        percyInvestigate.append(formatMessage("Those were a gift from my pops before I left home."), 3.0d);
        percyInvestigate.append(formatMessage(Component.text("They're the only ones I brought, and I highly doubt Alice would let me use ")
                        .append(Component.text("anything").decoration(TextDecoration.ITALIC, true))
                        .append(Component.text(" of hers since I took her spot in this band."))),
                7.5d
        );
        percyInvestigate.append(formatMessage("Mind keeping an eye out for 'em? They're made of warped and crimson wood so should be easy to spot. 'ppreciate ya!"), 7.0d);

        Dialogue percyNotFound = new Dialogue(plugin, formatMessage("Haven't spotted the drumsticks yet?"), 2.0d);
        percyNotFound.append(formatMessage(
                        Component.text("Yeah me neither, where the ")
                                .append(Component.text("nether").decoration(TextDecoration.ITALIC, true))
                                .append(Component.text(" are they?"))),
                4.5d
        );
        percyNotFound.append(formatMessage("Don't forget, they're made of crimson and warped woods!"), 4.0d);

        Dialogue percyFound = new Dialogue(plugin, formatMessage("Woah! Thank you so much for finding these for me!"), 3.0d);
        percyFound.append(formatMessage("I had no clue how I was going to continue playing music without them."), 4.0d);
        percyFound.append(formatMessage("Hey, if you don't mind me asking... Where did you find these that they're all covered in sand?"), 7.0d);
        percyFound.append(formatMessage("Ahhhh, so Alice must have taken and hid them cause she was jealous..."), 5.5d);
        percyFound.append(formatMessage("Well, there's no use in being angry or getting revenge."), 4.0d);
        percyFound.append(formatMessage("My pops always told me, \"If you take an eye for an eye, you'll still be half blind.\""), 5.0d);
        percyFound.append(formatMessage("Please, take this for all your help!"), 3.5d);

        addQuestInteractionHandler(QuestManager.NO_QUEST, (team, npcRightClickEvent) -> {
        });
        setDefaultInteractionHandler((team, npcRightClickEvent) -> {
            Player player = npcRightClickEvent.getClicker();
            if (team.getObjective(QuestManager.BAND_TROUBLE, percyTalk) == 0) {
                if (team.canStartDialogue()) {
                    team.setInDialogue(true);
                    percyInvestigate.play(team, () -> {
                        team.addObjectiveScore(QuestManager.BAND_TROUBLE, percyTalk, 1);
                        team.setInDialogue(false);
                    });
                }
            } else {
                if (team.getObjective(QuestManager.BAND_TROUBLE, drumsticks) == 0) {
                    Inventory inventory = player.getInventory();
                    if (inventory.contains(drumstickItem)) {
                        if (team.canStartDialogue()) {
                            inventory.remove(drumstickItem);
                            team.setInDialogue(true);
                            percyFound.play(team, () -> {
                                team.addObjectiveScore(QuestManager.BAND_TROUBLE, drumsticks, 1);
                                team.setInDialogue(false);
                                ItemStack bundle = QuestUtil.randomBlockBundle();
                                FullInventory.givePlayerItems(player, bundle);
                                team.sendMessage(QuestManager.getRewards(bundle));
                                team.setQuest(QuestManager.FINISHED_QUESTS);
                            });
                        }
                    } else {
                        if (team.canStartDialogue()) {
                            team.setInDialogue(true);
                            percyNotFound.play(team, () -> {
                                team.setInDialogue(false);
                            });
                        }
                    }
                }
            }
        });

    }

    public String getDrumsticks() {
        return drumsticks;
    }

    public ItemStack getDrumstickItem() {
        return drumstickItem;
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return PercyTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PercyTrait extends Trait {

        public PercyTrait() {
            super(TRAIT_NAME);
        }
    }

}
