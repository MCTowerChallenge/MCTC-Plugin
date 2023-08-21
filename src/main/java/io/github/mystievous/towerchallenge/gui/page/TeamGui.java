package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.TargetListGui;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TeamGui extends TargetListGui<TowerTeam> {

    public TeamGui(Plugin plugin, Component name, Function<TowerTeam, List<Component>> loreBuilder, List<? extends TowerTeam> teamList, BiConsumer<Player, TowerTeam> biConsumer, Element lastElement) {
        super(plugin, TextUtil.noItalic(name), team -> {
            ItemStack item = team.getRepresentation();
            ItemMeta meta = item.getItemMeta();
            meta.lore(loreBuilder.apply(team));
            item.setItemMeta(meta);
            return item;
        }, teamList.stream().map(TowerTeam.class::cast).toList(), biConsumer, lastElement);
    }

    public TeamGui(Plugin plugin, Component name, List<Component> lore, List<TowerTeam> teamList, BiConsumer<Player, TowerTeam> biConsumer, Element lastElement) {
        this(plugin, name, team -> lore, teamList, biConsumer, lastElement);
    }

}