package io.github.mystievous.towerchallenge.gui.element;

import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Element representing a ParticipantTeam
 */
public class TeamElement extends Element implements Clickable {

    private final ParticipantTeam team;
    private final BiConsumer<Player, ParticipantTeam> consumer;

    /**
     * Element representing a ParticipantTeam
     */
    public TeamElement(ParticipantTeam team, List<Component> lore, BiConsumer<Player, ParticipantTeam> biConsumer) {
        super(team.getItem());
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(team.getTextName()));
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