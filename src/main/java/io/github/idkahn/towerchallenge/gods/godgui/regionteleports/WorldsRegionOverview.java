package io.github.idkahn.towerchallenge.gods.godgui.regionteleports;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.gui.element.ButtonElement;
import io.github.idkahn.towerchallenge.gui.element.Element;
import io.github.idkahn.towerchallenge.gui.page.Gui;
import io.github.idkahn.towerchallenge.gui.page.Openable;
import io.github.idkahn.towerchallenge.gui.page.PresetGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class WorldsRegionOverview implements Openable {

    public static final String GUI_NAME = "Worlds:";

    public WorldsRegionOverview() {

    }

    @Override
    public Gui getGui() {
        PresetGui gui = new PresetGui(Component.text(GUI_NAME), 3);

        RegionManager overworldManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(TowerChallenge.WORLD()));
        RegionManager netherManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(TowerChallenge.NETHER()));
        RegionManager theEndManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(TowerChallenge.THE_END()));

        ItemStack overworldItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta overworldMeta = overworldItem.getItemMeta();
        overworldMeta.displayName(Component.text("Overworld").decoration(TextDecoration.ITALIC, false));
        overworldMeta.lore(new ArrayList<>(){{
            add(Component.text("Regions: ")
                    .append(Component.text(overworldManager.size()))
                    .color(TowerChallenge.PRIMARY_COLOR)
                    .decoration(TextDecoration.ITALIC, false)
            );
        }});
        overworldItem.setItemMeta(overworldMeta);
        ButtonElement overworldElement = new ButtonElement(overworldItem, player -> (new WorldIndividualGui(TowerChallenge.WORLD(), overworldManager, Component.text("Overworld regions:"), new ButtonElement(ButtonElement.backItem(), player1 -> getGui().openInventory(player1)))).openInventory(player));
        gui.placeElement(3, 2, overworldElement);

        ItemStack netherItem = new ItemStack(Material.NETHERRACK);
        ItemMeta netherMeta = netherItem.getItemMeta();
        netherMeta.displayName(Component.text("PLACEHOLDER NETHER"));
        netherItem.setItemMeta(netherMeta);
        Element netherElement = new Element(netherItem);
        gui.placeElement(5, 2, netherElement);

        ItemStack endItem = new ItemStack(Material.END_STONE);
        ItemMeta endMeta = endItem.getItemMeta();
        endMeta.displayName(Component.text("PLACEHOLDER END"));
        endItem.setItemMeta(endMeta);
        Element endElement = new Element(endItem);
        gui.placeElement(7, 2, endElement);

        return gui;
    }
}
