package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.element.TeamElement;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TeamGui extends ListGui {

    public TeamGui(Component name, Function<TowerTeam, List<Component>> loreBuilder, Collection<TowerTeam> teamList, BiConsumer<Player, TowerTeam> biConsumer, Element lastElement) {
        super(TextUtil.noItalic(name), lastElement);
        for (TowerTeam team : teamList) {
            if (team != null) {
                TeamElement element = new TeamElement(team, loreBuilder.apply(team), biConsumer);
                addElement(element);
            }
        }
    }

    public TeamGui(Component name, List<Component> lore, Collection<TowerTeam> teamList, BiConsumer<Player, TowerTeam> biConsumer, Element lastElement) {
        this(name, team -> lore, teamList, biConsumer, lastElement);
    }

}