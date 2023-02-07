package io.github.mystievous.towerchallenge.towering;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.hats.HatUtil;
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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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

    private final Location overworldPortalLocation = new Location(Worlds.Feb2023(), 97.5, 66, -2114.5);
    private final Location netherPortalLocation = new Location(Worlds.Feb2023_nether(), -23.5, 41, -388.5);

//    @EventHandler
//    public void onChangeWorld(final PlayerChangedWorldEvent event) {
//        event.
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPortal(final EntityPortalEvent event) {
        if (event.isCancelled())
            return;
        if (event.getTo().getWorld().equals(Worlds.Feb2023_nether())) {
            event.setCancelled(true);
            event.getEntity().teleport(netherPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
//                event.setTo(netherPortalLocation);
        } else if (event.getTo().getWorld().equals(Worlds.Feb2023())) {
            event.setCancelled(true);
            event.setTo(overworldPortalLocation);
            event.getEntity().teleport(overworldPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            if (event.getTo().getWorld().equals(Worlds.Feb2023_nether())) {
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
            } else if (event.getTo().getWorld().equals(Worlds.Feb2023())) {
                event.setCancelled(true);
                event.setTo(overworldPortalLocation);
                player.teleport(overworldPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
            }
        }
    }

}
