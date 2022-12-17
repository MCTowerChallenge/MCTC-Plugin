package io.github.mystievous.towerchallenge.gui.element;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.function.BiConsumer;

public class PlayerElement extends Element implements Clickable {

    private OfflinePlayer player;
    private final BiConsumer<Player, OfflinePlayer> consumer;

    public PlayerElement(ItemStack item, OfflinePlayer player, List<Component> lore, BiConsumer<Player, OfflinePlayer> biConsumer) {
        super(item);
        ItemStack skull = getItem();
        if (!(skull.getItemMeta() instanceof SkullMeta skullMeta)) {
            throw new IllegalArgumentException("Template item must be a skull!");
        }
        skullMeta.setOwningPlayer(player);
        String playerName = player.getName();
        String itemName = playerName != null ? player.getName() : player.getUniqueId().toString();
        skullMeta.displayName(Component.text(itemName).decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(lore);
        skullMeta.setCustomModelData(1);
        skull.setItemMeta(skullMeta);
        this.player = player;
        this.consumer = biConsumer;
    }

    public void use(Player fromPlayer) {
        if (consumer != null) {
            consumer.accept(fromPlayer, player);
        }
    }

}
