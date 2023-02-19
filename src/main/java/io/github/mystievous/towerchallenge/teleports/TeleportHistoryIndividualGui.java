package io.github.mystievous.towerchallenge.teleports;

import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.ListGui;
import io.github.mystievous.towerchallenge.gui.page.Openable;
import io.github.mystievous.towerchallenge.utility.Palette;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeleportHistoryIndividualGui extends ListGui implements Openable {

    public TeleportHistoryIndividualGui(TeleportHistoryManager teleportHistoryManager, OfflinePlayer player, Gui leaveGui) {
        super(Component.text(player.getName()).append(Component.text("'s Teleport History")), new ButtonElement(ButtonElement.backItem(), leaveGui::openInventory));
        List<TeleportLocation> locations = teleportHistoryManager.get(player);
        for (int i = locations.size()-1; i >= 0; i--) {
            TeleportLocation location = locations.get(i);
            ItemStack item = location.getReason().getItem();
            ItemMeta meta = item.getItemMeta();
            meta.lore(new ArrayList<>(){{
                add(TextUtil.formatText("Teleport Cause: " + location.getCause().name()));

                add(Component.text("Biome: ")
                        .append(Component.translatable(location.getBiome().translationKey()))
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                );

                add(Component.text(location.getBlockX()).append(Component.space())
                        .append(Component.text(location.getBlockY())).append(Component.space())
                        .append(Component.text(location.getBlockZ()))
                        .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                );

                add(Component.text("Click to teleport!").color(Palette.PRIMARY.toTextColor()));
            }});
            item.setItemMeta(meta);
            addElement(new ButtonElement(item, clickingPlayer -> {
                if (location.getReason().equals(TeleportLocation.Reason.PORTAL)) {
                    clickingPlayer.setGameMode(GameMode.SPECTATOR);
                }
                clickingPlayer.teleport(location);
                clickingPlayer.closeInventory();
            }));
        }
    }

    @Override
    public Gui getGui(Player player) {
        return this;
    }
}
