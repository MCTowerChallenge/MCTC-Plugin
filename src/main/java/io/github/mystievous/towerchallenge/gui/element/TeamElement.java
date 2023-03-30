package io.github.mystievous.towerchallenge.gui.element;

import io.github.mystievous.mystigui.element.Clickable;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Element representing a TowerTeam
 */
public class TeamElement extends Element implements Clickable {

    private final TowerTeam team;
    private final BiConsumer<Player, TowerTeam> consumer;

    /**
     * Element representing a TowerTeam
     */
    public TeamElement(TowerTeam team, List<Component> lore, BiConsumer<Player, TowerTeam> biConsumer) {
        super(team.getItem());
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        meta.displayName(TextUtil.noItalic(team.getDisplayName()));
        meta.lore(lore);
        item.setItemMeta(meta);
        this.team = team;
        this.consumer = biConsumer;
    }

    public void use(Player fromPlayer) {
        if (consumer != null) {
            consumer.accept(fromPlayer, team);
        }
    }

}