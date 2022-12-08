package io.github.idkahn.towerchallenge.gui.page;

import io.github.idkahn.towerchallenge.gui.element.ButtonElement;
import io.github.idkahn.towerchallenge.gui.element.PlayerElement;
import io.github.idkahn.towerchallenge.gui.element.TeamElement;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TeamGui extends ListGui {

    List<ParticipantTeam> teamList;

    public TeamGui(Component name, Function<ParticipantTeam, List<Component>> loreBuilder, List<ParticipantTeam> teamList, BiConsumer<Player, ParticipantTeam> biConsumer, ButtonElement lastElement) {
        super(name, lastElement);
        this.teamList = teamList;
        for (ParticipantTeam team : teamList) {
            if (team != null) {
                TeamElement element = new TeamElement(team, loreBuilder.apply(team), biConsumer);
                addElement(element);
            }
        }
    }

    public TeamGui(Component name, List<Component> lore, List<ParticipantTeam> teamList, BiConsumer<Player, ParticipantTeam> biConsumer, ButtonElement lastElement) {
        this(name, team -> lore, teamList, biConsumer, lastElement);
    }

}