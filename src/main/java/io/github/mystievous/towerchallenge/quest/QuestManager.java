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
    public static final String TRIVIA = "trivia";
    public static final String PARKOUR = "parkour";
    public static final String HAUNTED_HOUSE = "haunted-house";

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

        Quest triviaQuest = new Quest(plugin, TRIVIA, "Trivia");
        triviaQuest.setDescription("Complete the trivia on the left side of the haunted house!");
        quests.put(TRIVIA, triviaQuest);

        Quest parkourQuest = new Quest(plugin, PARKOUR, "Parkour");
        parkourQuest.setDescription("Complete the parkour course on the right side of the haunted house!");
        quests.put(PARKOUR, parkourQuest);

        Quest hauntedHouseQuest = new Quest(plugin, HAUNTED_HOUSE, "Haunted House");
        hauntedHouseQuest.setDescription("Complete both rooms of the haunted house!");
        quests.put(HAUNTED_HOUSE, hauntedHouseQuest);

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

    public void initTeamQuests(TowerTeam team, @Nullable Collection<String> completedQuests) {
        team.setQuests(quests, completedQuests);
    }

}

