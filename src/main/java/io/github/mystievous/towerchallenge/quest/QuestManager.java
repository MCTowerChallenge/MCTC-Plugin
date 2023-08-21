package io.github.mystievous.towerchallenge.quest;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.interaction.InteractableTaggedEntity;
import io.github.mystievous.towerchallenge.interaction.InteractableTagManager;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
import io.github.mystievous.towerchallenge.quest.npc.LegacyNPC;
import io.github.mystievous.towerchallenge.quest.util.FullInventory;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages quests and interactions related to quests.
 */
public class QuestManager implements Listener {

    public static final String GUI_ID = "questgui";


    // QUEST TAGS
    public static final String BAND_TROUBLE = "band-trouble";
    public static final String TUNNEL = "tunnel";
    public static final String OTHER_BAND = "other-band";
    public static final String WAIT = "wait";
    public static final String MEETING = "meeting";
    public static final String RIDDLE = "riddle";
    public static final String CAVE = "cave";
    public static final String RETURN_STICKS = "return-sticks";
    public static final String FINISHED_QUESTS = "finished-quests";

    // No Quest
    public static final String NO_QUEST = "no-quest";

    private final QuestItems questItems;
    private final Map<String, Quest> quests;


    /**
     * Constructs a QuestManager instance and initializes quests.
     *
     * @param plugin The TowerChallenge plugin instance.
     */
    public QuestManager(TowerChallenge plugin) {
        this.questItems = new QuestItems(plugin);
        quests = new HashMap<>();

        // Configure Quests

        Quest noQuest = new Quest(TowerChallenge.getInstance(), NO_QUEST, "No Quests!");
        noQuest.setDescription("Enjoy the event!");
        quests.put(NO_QUEST, noQuest);

        Quest bandTrouble = new Quest(plugin, BAND_TROUBLE, "Band Trouble");
        bandTrouble.setDescription("Check out the main stage by the beacons to see what's up with the band.");

        /*

            BROKEN LIGHT

         */

//        lightInteracted = new HashSet<>();

        String brokenLightTag = "broken-light";

        InteractableTaggedEntity brokenLight = new InteractableTaggedEntity(brokenLightTag);

        Dialogue lightInteract = new Dialogue(plugin, Dialogue.playerThoughts("This light has a trail of broken glass leading from it... I should follow and see where it goes."), 6.0d);
        brokenLight.addQuestInteractionHandler(BAND_TROUBLE, (team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                lightInteract.play(team, () -> {
//                    lightInteracted.add(team.getTextName());
                    team.setInDialogue(false);
                });
            }
        });

        InteractableTagManager.registerTag(brokenLight);

        quests.put(BAND_TROUBLE, bandTrouble);


        Quest tunnel = new Quest(plugin, TUNNEL, "Odd Tunnel");
        tunnel.setDescription("Investigate the strange tunnel behind the painting.");
        quests.put(TUNNEL, tunnel);


        Quest otherBand = new Quest(plugin, OTHER_BAND, "Rival Band");
        otherBand.setDescription("The rival band has a tunnel leading to the main stage... You should check them out.");
        quests.put(OTHER_BAND, otherBand);

        Quest wait = new Quest(plugin, WAIT, "Meow.");
        wait.setDescription("(Wait for something to happen.)");



        quests.put(WAIT, wait);


        Quest meeting = new Quest(plugin, MEETING, "Meeting");
        meeting.setDescription("Head to the taco stand for the mysterious meeting.");

        quests.put(MEETING, meeting);


        Quest riddle = new Quest(plugin, RIDDLE, "Riddle");
        riddle.setDescription("Figure out the riddle that Dave found.");



        quests.put(RIDDLE, riddle);


        Quest cave = new Quest(plugin, CAVE, "Hidden Cave");
        cave.setDescription("Investigate the mysterious cave underneath the sand.");

        /*

            DRUMSTICKS

         */

        String drumsticks = "drumsticks";

        ItemStack drumstickItem = GuiUtil.formatItem("Drumsticks", Material.STICK, 5);
        NBTUtils.setBool(plugin, drumsticks, drumstickItem);
        NBTUtils.noStack(plugin, drumstickItem);
        NBTUtils.setNoUse(drumstickItem);
        TextUtil.appendQuestItemLore(drumstickItem);
        QuestItems.putItem(drumsticks, drumstickItem);

        String drumstickIndividual = "drumstick-individual";
        ItemStack drumstickIndividualItem = GuiUtil.formatItem("Drumstick", Material.STICK, 4);
        QuestItems.putItem(drumstickIndividual, drumstickIndividualItem);

        InteractableTaggedEntity drumstickFrame = new InteractableTaggedEntity(drumsticks);
        drumstickFrame.addQuestInteractionHandler(CAVE, (team, event) -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Player player = event.getPlayer();
                Entity entity = event.getRightClicked();
                if (entity instanceof ItemFrame itemFrame) {
                    itemFrame.setItem(new ItemStack(Material.AIR));
                }
                FullInventory.givePlayerItems(player, drumstickItem);
                team.sendMessage(getRewards(drumstickItem));
                team.setQuest(RETURN_STICKS);
            });
        });

        quests.put(CAVE, cave);

        Quest returnSticks = new Quest(plugin, RETURN_STICKS, "Return Sticks");
        returnSticks.setDescription("Return Percy's drumsticks to him at the main stage!");

        quests.put(RETURN_STICKS, returnSticks);

        Quest finishedQuests = new Quest(plugin, FINISHED_QUESTS, "Quest Done!");
        finishedQuests.setDescription("Enjoy the rest of the event!");

        quests.put(FINISHED_QUESTS, finishedQuests);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Creates a formatted component for displaying rewards.
     *
     * @param items The items to list as rewards.
     * @return The formatted {@link Component}.
     */
    public static Component getRewards(ItemStack... items) {
        TextComponent.Builder builder = Component.text();
        builder.append(TextUtil.formatText("Rewards: ")).appendNewline();

        for (ItemStack item : items) {
            builder.append(TextUtil.formatText(String.format("+ %dx [", item.getAmount())).append(TextUtil.getItemName(item).hoverEvent(item.asHoverEvent()).color(NamedTextColor.WHITE)).append(Component.text("]")).appendNewline());
        }

        return builder.build();
    }

    public static final UUID daveUUID = UUID.fromString("209f4e33-fe27-4cd1-945a-a1f3e865a0f1");
    public static final Location foodLocation = new Location(Worlds.Jun2023(), 183.5f, 63.5f, -2174.0f, -90.0f, 12.0f);
    public static final Location stageLocation = new Location(Worlds.Jun2023(), 234.064700d, 56.000000d, -2231.980100d, 320.167236f, -26.501534f);

    public void teleportDaveStage() {
        Entity daveEntity = Bukkit.getEntity(daveUUID);

        if (daveEntity != null) {
            daveEntity.teleport(stageLocation);
        }
    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event) {
        if (event.getWorld().getName().equals(Worlds.Jun2023().getName())) {
            if (stageLocation.getChunk().load() && foodLocation.getChunk().load()) {
                Entity daveEntity = Bukkit.getEntity(daveUUID);

                if (daveEntity != null) {
                    daveEntity.teleport(stageLocation);
                }
            }
        }
    }

    public void teleportDaveTacos() {
        Entity daveEntity = Bukkit.getEntity(daveUUID);

        if (daveEntity != null) {
            daveEntity.teleport(foodLocation);
        }
    }



    public Map<String, Quest> getQuests() {
        return quests;
    }

    public QuestItems getQuestItems() {
        return questItems;
    }

    public void initTeamQuests(TowerTeam team) {
        team.setQuests(quests);
    }

}

