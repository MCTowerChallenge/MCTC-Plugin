package io.github.mystievous.towerchallenge.god;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.quest.Quest;
import io.github.mystievous.towerchallenge.quest.QuestChangeEvent;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A special team representing the Gods/Admins of the event.
 */
public class GodTeam extends TowerTeam {

    /**
     * Constructs a new GodTeam instance.
     *
     * @param plugin      The TowerChallenge plugin instance.
     * @param teamManager The TeamManager instance.
     * @param databaseId  The unique ID for the team in the database.
     * @param name        The name of the God team.
     * @param color       The color of the team.
     * @param dye         The dye color name associated with the team.
     */
    public GodTeam(TowerChallenge plugin, TeamManager teamManager, int databaseId, String name, Color color, String dye) {
        super(plugin, teamManager, databaseId, name, color, dye);
    }

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
     * Notifies the God team when a quest is completed/changed for any team.
     *
     * @param event The quest change event.
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

}
