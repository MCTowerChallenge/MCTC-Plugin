package io.github.mystievous.towerchallenge.gods;

import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import io.github.mystievous.towerchallenge.utility.Color;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Team for the Gods/Admins of the event
 */
public class GodTeam extends TowerTeam {

    public GodTeam(TowerChallenge plugin, TeamManager teamManager, int databaseId, String name, Color color, String dye) {
        super(plugin, teamManager, databaseId, name, color, dye);
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.valueOf(getDye() + "_CONCRETE"));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getDisplayName().decoration(TextDecoration.ITALIC, false));
        itemMeta.setCustomModelData(0);
        item.setItemMeta(itemMeta);
        return item;
    }

}
