package io.github.mystievous.towerchallenge.gods;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.quests.Quest;
import io.github.mystievous.towerchallenge.quests.QuestChangeEvent;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Team for the Gods/Admins of the event
 */
public class GodTeam extends TowerTeam {

    public GodTeam(TowerChallenge plugin, TeamManager teamManager, int databaseId, String name, Color color, String dye) {
        super(plugin, teamManager, databaseId, name, color, dye);
    }

    /**
     * @return The item representation of this team
     */
    @Override
    public ItemStack getRepresentation() {
        ItemStack item = new ItemStack(Material.valueOf(getDye() + "_CONCRETE"));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getDisplayName().decoration(TextDecoration.ITALIC, false));
        itemMeta.setCustomModelData(0);
        item.setItemMeta(itemMeta);
        return item;
    }

    /**
     * Tells the gods when a team
     * completes a quest
     *
     * @param event the event of the quest being changed
     */
    @EventHandler
    public void onQuestChange(final QuestChangeEvent event) {
        if (event.isCancelled())
            return;

        TowerTeam team = event.getTeam();
        Quest quest = event.getQuest();
        Quest prevQuest = event.getPrevQuest();
        if (prevQuest != null) {
            if (quest == null) {
                sendMessage(team.getDisplayName()
                        .append(Component.text(" has no more quests! ").color(NamedTextColor.WHITE)));
            } else {
                sendMessage(team.getDisplayName()
                        .append(Component.text(" has completed a quest! ").color(NamedTextColor.WHITE))
                        .append(TextUtil.formatText(String.format("%s -> %s", prevQuest.getFriendlyName(), quest.getFriendlyName()))));
            }

        }
    }

    @Override
    public void unregisterEvents() {
        QuestChangeEvent.getHandlerList().unregister(this);
    }
}
