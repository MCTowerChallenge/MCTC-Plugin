package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.PresetGui;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A Gui that shows all the starting
 * items from a specified team.
 */
public class StartingItemsGui extends PresetGui {

    /**
     * A Gui that shows all the starting
     * items from the specified team.
     *
     * @param team The team to show the items from.
     */
    public StartingItemsGui(@NotNull ParticipantTeam team) {
        super(Component.text("Starting Items for " + team.getTextName()), 5);
        Map<Integer, ItemStack> startingItems = team.getStartingItems();
        Map<EquipmentSlot, ItemStack> startingEquipment = team.getStartingEquipment();

        for (Map.Entry<Integer, ItemStack> entry : startingItems.entrySet()) {
            int index = entry.getKey();
            ItemStack item = entry.getValue();

            Element element = new ButtonElement(item, player -> player.getInventory().addItem(item));

            if (index < 9) {
                placeElement(index + 1, 4, element);
            } else {
                placeElement(indexToCol(index), indexToRow(index), element);
            }

        }

        for (Map.Entry<EquipmentSlot, ItemStack> entry : startingEquipment.entrySet()) {
            Element element = new ButtonElement(entry.getValue(), player -> player.getInventory().addItem(entry.getValue()));
            switch (entry.getKey()) {
                case HEAD -> placeElement(1, 5, element);
                case CHEST -> placeElement(2, 5, element);
                case LEGS -> placeElement(3, 5, element);
                case FEET -> placeElement(4, 5, element);
                case OFF_HAND -> placeElement(6, 5, element);
            }
        }

    }

}
