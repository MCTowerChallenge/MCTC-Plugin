package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.TeamElement;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TeamGui extends ListGui {

    Collection<ParticipantTeam> teamList;

    public TeamGui(Component name, Function<ParticipantTeam, List<Component>> loreBuilder, Collection<ParticipantTeam> teamList, BiConsumer<Player, ParticipantTeam> biConsumer, ButtonElement lastElement) {
        super(TextUtil.noItalic(name), lastElement);
        this.teamList = teamList;
        for (ParticipantTeam team : teamList) {
            if (team != null) {
                TeamElement element = new TeamElement(team, loreBuilder.apply(team), biConsumer);
                addElement(element);
            }
        }
    }

    public TeamGui(Component name, List<Component> lore, Collection<ParticipantTeam> teamList, BiConsumer<Player, ParticipantTeam> biConsumer, ButtonElement lastElement) {
        this(name, team -> lore, teamList, biConsumer, lastElement);
    }

}