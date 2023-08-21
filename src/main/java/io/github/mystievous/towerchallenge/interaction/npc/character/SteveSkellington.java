package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
import io.github.mystievous.towerchallenge.interaction.npc.QuestCharacter;
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
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

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

//        setWorldguardRegion(REGION);

        String steveTalk = "steve-talk";

        ItemStack prideBrush = GuiUtil.formatItem("Brush", Material.BRUSH, 1);
        ItemMeta brushMeta = prideBrush.getItemMeta();
        brushMeta.setPlaceableKeys(new HashSet<>() {{
            add(NamespacedKey.minecraft("suspicious_sand"));
            add(NamespacedKey.minecraft("suspicious_gravel"));
            add(NamespacedKey.minecraft("sandstone"));
        }});
        brushMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        brushMeta.setUnbreakable(true);
        prideBrush.setItemMeta(brushMeta);

        Dialogue steveInvestigate = new Dialogue(plugin, formatMessage("Hello my friends! And a very happy pride month to you all!"), 7.0d, TowerChallenge.key("steve.pride"));
        steveInvestigate.append(formatMessage("Ah I see Endi mentioned I was down here?"), 5.5d, TowerChallenge.key("steve.endi"));
        steveInvestigate.append(formatMessage("Well I was unable to get glass, but I did find this broom to sweep up the mess from the light!"), 6.5d, TowerChallenge.key("steve.no_glass"));
        steveInvestigate.append(formatMessage(Component.text("I may have gotten distracted by these snacks though..")), 6.5d, TowerChallenge.key("steve.snacks"));
        steveInvestigate.append(formatMessage("I don't think I'll have time to go make my own fresh or go find a seller, with everything else going on."), 7.5d, TowerChallenge.key("steve.fresh"));
        steveInvestigate.append(formatMessage("Hmmm... Would you happen to be busy?"), 5.0d, TowerChallenge.key("steve.busy"));
        steveInvestigate.append(formatMessage("If you get the chance I'd really appreciate the help in getting more glass!"), 6.0d, TowerChallenge.key("steve.help"));
        steveInvestigate.append(formatMessage("I should probably head back soon... "), 4.5d, TowerChallenge.key("steve.get_back_soon"));
        steveInvestigate.append(formatMessage("Endi tends to be quite the worry nether wart."), 5.5d, TowerChallenge.key("steve.endi_worry"));
        steveInvestigate.append(formatMessage(Component.text("Maybe one last Pitt Cola first, it's hot out there..").decoration(TextDecoration.ITALIC, true)), 8.0d, TowerChallenge.key("steve.one_last"));

        Dialogue steveFound = new Dialogue(plugin, formatMessage("You're back already!"), 2.0d, TowerChallenge.key("steve.back_already"));
        steveFound.append(formatMessage("How long have I been here eating...?"), 3.5d, TowerChallenge.key("steve.how_long"));
        steveFound.append(formatMessage("Uhh never mind that!"), 2.25d, TowerChallenge.key("steve.nevermind"));
        steveFound.append(formatMessage("Thanks for getting the glass together for us."), 4.5d, TowerChallenge.key("steve.thanks"));
        steveFound.append(formatMessage("Hmm I don't actually seem to have much worthwhile on me..."), 6.0d, TowerChallenge.key("steve.worthwhile"));
        steveFound.append(formatMessage("How about this!"), 3.5d, TowerChallenge.key("steve.how_about_this"));

        Dialogue steveNotFound = new Dialogue(plugin, formatMessage("Remember, I need plain pure glass!"), 5.0d, TowerChallenge.key("steve.pure_glass"));

        String glass = "glass";

        addQuestInteractionHandler(QuestManager.NO_QUEST, (towerTeam, npcRightClickEvent) -> {
        });
        setDefaultInteractionHandler((team, npcRightClickEvent) -> {
            Player player = npcRightClickEvent.getClicker();
            if (team.getObjective(QuestManager.BAND_TROUBLE, steveTalk) == 0) {
                if (team.canStartDialogue()) {
                    team.setInDialogue(true);
                    steveInvestigate.play(team, () -> {
                        team.addObjectiveScore(QuestManager.BAND_TROUBLE, steveTalk, 1);
                        team.setInDialogue(false);
                    });
                }
            } else {
                if (team.getObjective(QuestManager.BAND_TROUBLE, glass) == 0) {
                    ItemStack glassRequirement = new ItemStack(Material.GLASS);
                    Inventory inventory = player.getInventory();
                    if (inventory.contains(glassRequirement)) {
                        if (team.canStartDialogue()) {
                            team.setInDialogue(true);
                            inventory.removeItem(glassRequirement);
                            steveFound.play(team, () -> {
                                team.addObjectiveScore(QuestManager.BAND_TROUBLE, glass, 1);
                                team.sendMessage(QuestManager.getRewards(prideBrush));
                                FullInventory.givePlayerItems(player, prideBrush);
                                team.setInDialogue(false);
                            });
                        }
                    } else {
                        if (team.canStartDialogue()) {
                            team.setInDialogue(true);
                            steveNotFound.play(team, () -> {
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
//        Waypoints waypoints = npc.getOrAddTrait(Waypoints.class);
//        waypoints.setWaypointProvider("wander");
//        WaypointProvider provider = waypoints.getCurrentProvider();
//        if (provider instanceof WanderWaypointProvider wanderWaypointProvider) {
//            wanderWaypointProvider.setWorldGuardRegion(getWorldguardRegion());
//            Bukkit.getServer().sendMessage(Component.text(wanderWaypointProvider.getDelay()));
//        }
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
