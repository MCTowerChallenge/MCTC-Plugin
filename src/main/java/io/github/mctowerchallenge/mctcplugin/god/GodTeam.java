package io.github.mctowerchallenge.mctcplugin.god;

import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
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
    public GodTeam(MCTCPlugin plugin, TeamManager teamManager, int databaseId, String name, Color color, String dye) {
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
}
