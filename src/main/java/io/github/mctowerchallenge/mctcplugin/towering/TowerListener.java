package io.github.mctowerchallenge.mctcplugin.towering;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import io.github.mctowerchallenge.mctcplugin.ChallengeManager;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class TowerListener implements Listener {

    private final JavaPlugin plugin;

    public TowerListener(ChallengeManager manager) {
        this.plugin = manager.getPlugin();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;

        if (event.getView().getTopInventory() instanceof AnvilInventory) {
            if (event.getCurrentItem().getItemMeta() instanceof BlockStateMeta blockStateMeta) {
                if (blockStateMeta.getBlockState() instanceof ShulkerBox) {
                    Component displayName = blockStateMeta.displayName();
                    if (displayName != null && PlainTextComponentSerializer.plainText().serialize(displayName).equals(ParticipantTeam.SHULKER_NAME)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSpawnSet(final PlayerSetSpawnEvent event) {
        if (!event.getCause().equals(PlayerSetSpawnEvent.Cause.PLAYER_RESPAWN)) {
            event.setCancelled(true);
        }
    }

}
