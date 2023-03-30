package io.github.mystievous.towerchallenge.gods.godgui.regionteleports;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.gui.Icons;
import io.github.mystievous.mysticore.Palette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Shows all worlds, allowing to open
 * the WorldIndividualGui for each one
 */
public class WorldsRegionOverview implements Openable {

    public static final String GUI_NAME = "Worlds:";

    private final TowerChallenge plugin;

    public WorldsRegionOverview(TowerChallenge plugin) {
        this.plugin = plugin;
    }

    @Override
    public Gui getGui(Player player) {
        PresetGui gui = new PresetGui(plugin, Component.text(GUI_NAME), 3);

        RegionManager overworldManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.WORLD()));
        RegionManager netherManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.NETHER()));
        RegionManager theEndManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(Worlds.THE_END()));

        ItemStack overworldItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta overworldMeta = overworldItem.getItemMeta();
        overworldMeta.displayName(Component.text("Overworld").decoration(TextDecoration.ITALIC, false));
        overworldMeta.lore(new ArrayList<>() {{
            add(Component.text("Regions: ")
                    .append(Component.text(overworldManager.size()))
                    .color(Palette.PRIMARY.toTextColor())
                    .decoration(TextDecoration.ITALIC, false)
            );
        }});
        overworldItem.setItemMeta(overworldMeta);
        ButtonElement overworldElement = new ButtonElement(overworldItem, player1 -> (new WorldIndividualGui(plugin, Worlds.WORLD(), overworldManager, Component.text("Overworld regions:"), new ButtonElement(Icons.backItem(), player2 -> getGui(player2).openInventory(player2)))).openInventory(player1));
        gui.placeElement(2, 3, overworldElement);

        ItemStack netherItem = new ItemStack(Material.NETHERRACK);
        ItemMeta netherMeta = netherItem.getItemMeta();
        netherMeta.displayName(Component.text("Nether").decoration(TextDecoration.ITALIC, false));
        netherMeta.lore(new ArrayList<>() {{
            add(Component.text("Regions: ")
                    .append(Component.text(netherManager.size()))
                    .color(Palette.PRIMARY.toTextColor())
                    .decoration(TextDecoration.ITALIC, false)
            );
        }});
        netherItem.setItemMeta(netherMeta);
        Element netherElement = new ButtonElement(netherItem, player1 -> (new WorldIndividualGui(plugin, Worlds.NETHER(), netherManager, Component.text("Nether regions:"), new ButtonElement(Icons.backItem(), player2 -> getGui(player2).openInventory(player2)))).openInventory(player1));
        gui.placeElement(2, 5, netherElement);

        ItemStack endItem = new ItemStack(Material.END_STONE);
        ItemMeta endMeta = endItem.getItemMeta();
        endMeta.displayName(Component.text("PLACEHOLDER END"));
        endItem.setItemMeta(endMeta);
        Element endElement = new Element(endItem);
        gui.placeElement(2, 7, endElement);

        return gui;
    }
}
