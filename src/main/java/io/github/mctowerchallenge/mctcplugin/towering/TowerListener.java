package io.github.mctowerchallenge.mctcplugin.towering;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import io.github.mctowerchallenge.mctcplugin.ChallengeManager;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.hats.HatUtil;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
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

        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        ItemStack item = event.getCurrentItem();
        int slot = event.getSlot();

        if (HatUtil.isHat(item)) {
            if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                event.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.getInventory().clear(slot), 1);
            }
        }
    }

    @EventHandler
    public void onPlayerSpawnSet(final PlayerSetSpawnEvent event) {
        if (!event.getCause().equals(PlayerSetSpawnEvent.Cause.PLAYER_RESPAWN)) {
            event.setCancelled(true);
        }
    }

    private final Location spawnPortalLocation = new Location(Worlds.Jan2024(), -1379+0.5, 69, -478+0.5, 90f, 16f);
    private final Location netherPortalLocation = new Location(Worlds.Jan2024_nether(), -190+0.5, 76, -128+0.5, 90f, 16f);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPortal(final EntityPortalEvent event) {
        if (event.isCancelled())
            return;
        Location toLocation = event.getTo();
        if (toLocation != null) {
            if (toLocation.getWorld().equals(netherPortalLocation.getWorld())) {
                event.setCancelled(true);
                event.getEntity().teleport(netherPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
            } else if (toLocation.getWorld().equals(spawnPortalLocation.getWorld())) {
                event.setCancelled(true);
                event.setTo(spawnPortalLocation);
                event.getEntity().teleport(spawnPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            if (event.getTo().getWorld().equals(netherPortalLocation.getWorld())) {
                event.setCancelled(true);
                player.teleport(netherPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                Advancement enterNetherAdvancement = this.plugin.getServer().getAdvancement(NamespacedKey.minecraft("story/enter_the_nether"));
                if (enterNetherAdvancement != null) {
                    String enterNetherCriteria = "entered_nether";
                    AdvancementProgress advancementProgress = player.getAdvancementProgress(enterNetherAdvancement);
                    if (!advancementProgress.isDone()) {
                        advancementProgress.awardCriteria(enterNetherCriteria);
                    }
                }
            } else if (event.getTo().getWorld().equals(spawnPortalLocation.getWorld())) {
                event.setCancelled(true);
                event.setTo(spawnPortalLocation);
                player.teleport(spawnPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
            }
        }
    }

}
