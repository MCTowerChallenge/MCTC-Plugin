package io.github.mctowerchallenge.mctcplugin.quest;

import io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.quests.Jan2024QuestManager;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTagManager;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTaggedEntity;
import io.github.mctowerchallenge.mctcplugin.interaction.Sparkle;
import io.github.mctowerchallenge.mctcplugin.quest.util.FullInventory;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mystigui.GuiUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages quests and interactions related to quests.
 */
public class QuestManager implements Listener {

    public static final String GUI_ID = "questgui";

    // No Quest
    public static final String NO_QUEST = "no-quest";

    private final QuestItems questItems;
    private final Map<String, Quest> quests;
    private final Sparkle ballSparkle;

    /**
     * Constructs a QuestManager instance and initializes quests.
     *
     * @param plugin The TowerChallenge plugin instance.
     */
    public QuestManager(MCTCPlugin plugin) {
        this.questItems = new QuestItems(plugin);
        quests = new HashMap<>();

        // Configure Quests
        Quest noQuest = new Quest(plugin, NO_QUEST, "No Quests!");
        noQuest.setDescription("Enjoy the event!");
        quests.put(NO_QUEST, noQuest);


        Quest notStarted = new Quest(plugin, QuestTags.NOT_STARTED, "Patience");
        notStarted.setDescription("Be patient while the event starts :)");
        quests.put(QuestTags.NOT_STARTED, notStarted);

        Quest steveStart = new Quest(plugin, QuestTags.STEVE_START, "Talk to steve");
        steveStart.setDescription("Talk to steve skellington by the stage.");
        quests.put(QuestTags.STEVE_START, steveStart);

        Quest genStart = new Quest(plugin, QuestTags.GEN_START, "Talk to Generic Maintenance Man");
        genStart.setDescription("Talk to Generic Maintenance Man.");
        quests.put(QuestTags.GEN_START, genStart);

        MultiObjectiveQuest findItems = new MultiObjectiveQuest(plugin, QuestTags.FIND_ITEMS, "Find Parts");
        findItems.setDescription("Find two gears and the special lever to fix the new years ball!");
        Quest gear1 = new Quest(plugin, QuestTags.GEAR_1, "Gear (North)");
        gear1.setDescription("Find the lost gear somewhere on the north side!");
        gear1.setItem(GuiUtil.formatItem("Obsidian", Material.OBSIDIAN, 44));
        Quest gear2 = new Quest(plugin, QuestTags.GEAR_2, "Gear (South)");
        gear2.setDescription("Find the lost gear somewhere on the south side!");
        gear2.setItem(GuiUtil.formatItem("Obsidian", Material.OBSIDIAN, 45));
        Quest lever = new Quest(plugin, QuestTags.LEVER, "Lever");
        lever.setDescription("Find the lost lever somewhere at spawn!");
        lever.setItem(GuiUtil.formatItem("Obsidian", Material.OBSIDIAN, 45));
        findItems.addSubQuest(gear1);
        findItems.addSubQuest(gear2);
        findItems.addSubQuest(lever);
        quests.put(findItems.getTag(), findItems);

        Quest genReturn = new Quest(plugin, QuestTags.GEN_RETURN, "Return to Generic Maintenance Man");
        genReturn.setDescription("Return the parts to Generic Maintenance Man.");
        quests.put(QuestTags.GEN_RETURN, genReturn);

        Quest fixBall = new Quest(plugin, QuestTags.FIX_BALL, "Fix the Ball");
        fixBall.setDescription("Climb to the top of the building and fix the new years ball!");
        quests.put(fixBall.getTag(), fixBall);

        Quest genComplete = new Quest(plugin, QuestTags.GEN_COMPLETE, "Return to Generic Maintenance Man");
        genComplete.setDescription("Return to Generic Maintenance Man with the news!");
        quests.put(genComplete.getTag(), genComplete);

        Quest performance = new Quest(plugin, QuestTags.PERFORMANCE, "Performance!");
        performance.setDescription("Enjoy the show!");
        quests.put(performance.getTag(), performance);


        InteractableTaggedEntity northGearEntity = getInteractableTaggedEntity(plugin, Jan2024QuestManager.northGearTag, QuestTags.GEAR_1);
        InteractableTagManager.registerTag(northGearEntity);

        InteractableTaggedEntity southGearEntity = getInteractableTaggedEntity(plugin, Jan2024QuestManager.southGearTag, QuestTags.GEAR_2);
        InteractableTagManager.registerTag(southGearEntity);

        InteractableTaggedEntity leverEntity = getInteractableTaggedEntity(plugin, Jan2024QuestManager.leverTag, QuestTags.LEVER);
        InteractableTagManager.registerTag(leverEntity);

        ballSparkle = new Sparkle(plugin, new Location(Worlds.Jan2024(), -1401.5, 102.25, -333.5), new Vector(1, 1.5, 1).multiply(.3));
        InteractableTaggedEntity fixEntity = new InteractableTaggedEntity(QuestTags.FIX_BALL);
        fixEntity.addQuestInteractionHandler(QuestTags.FIX_BALL, (team, playerInteractEntityEvent) -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ballSparkle.removeTeam(team);
                    team.completeQuest(QuestTags.FIX_BALL);
                    team.setQuest(QuestTags.GEN_COMPLETE);
                }
            }.runTask(plugin);
        });
        InteractableTagManager.registerTag(fixEntity);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Sparkle getBallSparkle() {
        return ballSparkle;
    }

    @NotNull
    private static InteractableTaggedEntity getInteractableTaggedEntity(Plugin plugin, String tag, String completeQuestTag) {
        InteractableTaggedEntity taggedEntity = new InteractableTaggedEntity(tag);
        taggedEntity.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        taggedEntity.addQuestInteractionHandler(QuestTags.FIND_ITEMS, (team, playerInteractEntityEvent) -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    team.completeQuest(completeQuestTag);

                    if (team.getQuest(QuestTags.FIND_ITEMS) instanceof MultiObjectiveQuest itemQuest) {
                        if (itemQuest.tryComplete()) {
                            team.completeQuest(QuestTags.FIND_ITEMS);
                            team.setQuest(QuestTags.GEN_RETURN);
                        }
                    }

                    List<Entity> entities = Bukkit.selectEntities(Jan2024QuestManager.sender, String.format("@e[tag=%s]", tag));
                    for (Player player : team.getOnlinePlayers()) {
                        for (Entity entity : entities) {
                            player.hideEntity(plugin, entity);
                        }
                    }
                }
            }.runTask(plugin);
        });
        return taggedEntity;
    }

    @EventHandler
    public void teamCompleteQuest(final QuestCompleteEvent event) {
        if (event.getQuest().getTag().equals(QuestTags.GEN_RETURN)) {
            ballSparkle.addTeam(event.getTeam());
        }
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

