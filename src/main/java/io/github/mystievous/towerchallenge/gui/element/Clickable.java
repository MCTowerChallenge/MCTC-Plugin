package io.github.mystievous.towerchallenge.gui.element;

import org.bukkit.entity.Player;

public interface Clickable {

    /**
     * Method that is called when
     * the button is clicked
     *
     * @param player The player that clicked the button
     */
    void use(Player player);

}
