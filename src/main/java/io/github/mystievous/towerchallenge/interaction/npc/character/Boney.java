package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
import io.github.mystievous.towerchallenge.interaction.npc.QuestCharacter;
import io.github.mystievous.towerchallenge.quest.QuestItems;
import io.github.mystievous.towerchallenge.quest.QuestManager;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Boney extends QuestCharacter {

    public static final String NAME = "Boney";
    public static final Color NAME_COLOR = new Color(0x7694a5);
    public static final Color TEXT_COLOR = new Color(0x879aa5);
    public static final String TRAIT_NAME = "boney";

    public Boney(Plugin plugin) {
        super(plugin, EntityType.WITHER_SKELETON, NAME, NAME_COLOR, TEXT_COLOR);

        String boneyTalk = "boney-talk";
        String copper = "copper";

        ItemStack knife = GuiUtil.formatItem("Knife", Material.IRON_SWORD, 0);
        ItemMeta knifeMeta = knife.getItemMeta();
        knifeMeta.lore(new ArrayList<>() {{
            add(TextUtil.formatText("It's engraved with \"AW\".").decoration(TextDecoration.ITALIC, true));
            add(Component.empty());
        }});
        TextUtil.appendQuestItemLore(knifeMeta);
        knife.setItemMeta(knifeMeta);

        QuestItems.putItem("knife", knife);

        ItemStack hastePotion = GuiUtil.formatItem(TextUtil.noItalic("Potion of Haste"), Material.POTION, 0);
        PotionMeta hasteMeta = (PotionMeta) hastePotion.getItemMeta();
        hasteMeta.setColor(new Color(0xE3AC17).toBukkitColor());
        hasteMeta.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 30 * 20, 1, true, true), true);
        hastePotion.setItemMeta(hasteMeta);

        QuestItems.putItem("haste-potion", hastePotion);

        Dialogue boneyInvestigate = new Dialogue(plugin, formatMessage("What the nether, man."), 2.5d);
        boneyInvestigate.append(new Dialogue(plugin, formatMessage("This is so obviously sabotage, messed up for real."), 4.5d));
        boneyInvestigate.append(new Dialogue(plugin, formatMessage("Worst part is, I'm fresh out of copper since the last time someone cut my instrument cable!"), 5.0d));
        boneyInvestigate.append(new Dialogue(plugin, formatMessage(Component.text("That was a joke, this hasn't happened before...").decoration(TextDecoration.ITALIC, true)), 4.5d));
        boneyInvestigate.append(new Dialogue(plugin, formatMessage("Anyways, any chance you have 10 copper ingots on you?"), 5.25d));
        boneyInvestigate.append(new Dialogue(plugin, formatMessage("Well if you happen upon some spare, y'know I'll be here."), 5.5d));

        Dialogue boneyNotFound = new Dialogue(plugin, formatMessage("Scrounged up the 10 copper?"), 2.5d);
        boneyNotFound.append(new Dialogue(plugin, formatMessage("Well get out scrounging then!"), 3.0d));

        Dialogue boneyFound = new Dialogue(plugin, formatMessage("Oh sweet you found copper, thanks guys!"), 4.0d);
        boneyFound.append(new Dialogue(plugin, formatMessage("Here, since I heard you're doing that big tower challenge today too I figured this potion might help you."), 7.0d));
        boneyFound.append(new Dialogue(plugin, formatMessage("Oh, would you mind also throwing this away for me? I found it on the ground over there."), 4.5d));
        Dialogue postBoneyFound = new Dialogue(plugin, Dialogue.playerThoughts("Woah free knife, nice!"), 3.0d);
        postBoneyFound.append(new Dialogue(plugin, Dialogue.playerThoughts("Looks like there's a faded engraving on it..."), 4.0d));
        postBoneyFound.append(new Dialogue(plugin, Dialogue.playerThoughts("MV maybe? or AW? It's hard to tell with how dirty and old it is..."), 6.0d));

        addQuestInteractionHandler(QuestManager.NO_QUEST, (team, npcRightClickEvent) -> {
        });

        ItemStack copperIngots = new ItemStack(Material.COPPER_INGOT, 10);

        setDefaultInteractionHandler((team, npcRightClickEvent) -> {
            Player player = npcRightClickEvent.getClicker();
            if (team.getObjective(QuestManager.BAND_TROUBLE, boneyTalk) == 0) {
                if (team.canStartDialogue()) {
                    team.setInDialogue(true);
                    team.addObjectiveScore(QuestManager.BAND_TROUBLE, boneyTalk, 1);
                    boneyInvestigate.play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
            } else {
                if (team.getObjective(QuestManager.BAND_TROUBLE, copper) == 0) {
                    Inventory inventory = player.getInventory();
                    if (inventory.contains(copperIngots)) {
                        if (team.canStartDialogue()) {
                            team.setInDialogue(true);
                            team.addObjectiveScore(QuestManager.BAND_TROUBLE, copper, 1);
                            inventory.remove(copperIngots);
                            boneyFound.play(team, () -> {
                                FullInventory.givePlayerItems(player, knife, hastePotion);
                                postBoneyFound.play(team, () -> {
                                    team.setInDialogue(false);
                                    team.sendMessage(QuestManager.getRewards(knife, hastePotion));
                                });
                            });
                        }
                    } else {
                        if (team.canStartDialogue()) {
                            team.setInDialogue(true);
                            boneyNotFound.play(team, () -> {
                                team.setInDialogue(false);
                            });
                        }
                    }
                }
            }
        });

    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return BoneyTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class BoneyTrait extends Trait {
        public BoneyTrait() {
            super(TRAIT_NAME);
        }
    }

}
