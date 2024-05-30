package io.github.mctowerchallenge.mctcplugin.quest;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages quests and interactions related to quests.
 */
public class QuestManager implements Listener {

    public static final String GUI_ID = "questgui";

    // No Quest
    public static final String NO_QUEST = "no-quest";

    private final QuestItems questItems;
    private final Map<String, Quest> quests;

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

        Quest genericBeeConservationistManStart = new Quest(plugin, QuestTags.GENERIC_BEE_CONSERVATIONIST_START, "Talk to Generic Bee Conservationist Man");
        genericBeeConservationistManStart.setDescription("Talk to Generic Bee Conservationist Man in the bee pit.");
        quests.put(QuestTags.GENERIC_BEE_CONSERVATIONIST_START, genericBeeConservationistManStart);

        Quest collectNests = new Quest(plugin, QuestTags.COLLECT_NESTS, "Get Bee Nests");
        collectNests.setDescription("Bring 5 bee nests to Generic Bee Conservationist Man.");
        quests.put(QuestTags.COLLECT_NESTS, collectNests);

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

