package io.github.mctowerchallenge.mctcplugin.portal;

import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.god.GodTeam;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * Manages the logic for the end portal in the game.
 */
public class EndPortal implements Listener {

    public static final Location overworldSpawn = Worlds.Oct2023().getSpawnLocation();

    private final TeamManager teamManager;

    /**
     * First corner of the inner-portal bounds.
     * This is where the actual portal blocks
     * are spawned.
     */
    public static final Location PORTAL_MIN = new Location(Worlds.Jan2024(), -1419, 64, -474);

    /**
     * Second corner of the inner-portal bounds.
     * This is where the actual portal blocks
     * are spawned.
     */
    public static final Location PORTAL_MAX = new Location(Worlds.Jan2024(), -1416, 64, -471);

    /**
     * Creates an EndPortal instance.
     *
     * @param plugin      The main plugin instance.
     * @param teamManager The TeamManager instance.
     */
    public EndPortal(Plugin plugin, TeamManager teamManager) {
        this.teamManager = teamManager;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Opens the end portal by setting the portal blocks in the specified bounds.
     * Announces the opening to the server with a message and sound.
     */
    public void openPortal() {
        for (int x = PORTAL_MIN.getBlockX(); x <= PORTAL_MAX.getBlockX(); x++) {
            for (int z = PORTAL_MIN.getBlockZ(); z <= PORTAL_MAX.getBlockZ(); z++) {
                Location block = new Location(PORTAL_MIN.getWorld(), x, PORTAL_MIN.getY(), z);
                block.getBlock().setType(Material.END_PORTAL);
            }
        }
        Bukkit.getServer().sendMessage(Component.text("End Portal ").color(TextColor.fromHexString("#f0ffd0"))
                .append(Component.text("has been opened!").color(NamedTextColor.WHITE)));
        Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.end_portal.spawn"), Sound.Source.MASTER, 100, 1));
        final Title title = Title.title(Component.text("End Portal").color(TextColor.fromHexString("#f0ffd0")), Component.text("has been opened!").color(NamedTextColor.WHITE));
        Bukkit.getServer().showTitle(title);
    }

    /**
     * Resets the end portal by clearing the portal blocks and resetting team portal frames.
     */
    public void resetPortal() {
        Bukkit.getLogger().info("Resetting End Portal");
        for (int x = PORTAL_MIN.getBlockX(); x <= PORTAL_MAX.getBlockX(); x++) {
            for (int z = PORTAL_MIN.getBlockZ(); z <= PORTAL_MAX.getBlockZ(); z++) {
                Location block = new Location(PORTAL_MIN.getWorld(), x, PORTAL_MIN.getY(), z);
                block.getBlock().setType(Material.AIR);
            }
        }
        teamManager.resetTeamPortalFrames();
    }

    public static Location spawnLocation() {
        return PortalControllers.randomPointInFlatRing(new Vector(0.5, 63, 0.5), 6, 13).toLocation(Worlds.Oct2023_the_end());
    }

    @EventHandler
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            if (event.getTo().getWorld().equals(Worlds.Oct2023_the_end())) {
                event.setCancelled(true);
                player.teleport(spawnLocation(), PlayerTeleportEvent.TeleportCause.END_PORTAL);
            }
        }
    }

    /**
     * Sets players to the overworld if they go through the end portal in the end
     * @param event the player respawn event caused by the end portal
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
//        if (event.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)) {
//            if (event.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)) {
//                event.setRespawnLocation(EndPortal.overworldSpawn);
//            }
//        }
    }

    /**
     * Handles the interaction of players with the end portal frames.
     *
     * @param event The PlayerInteractEvent.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            TowerTeam team = teamManager.getPlayerTeam(player);
            assert block != null;
            if (block.getType().equals(Material.END_PORTAL_FRAME)) {
                if (!((EndPortalFrame) block.getBlockData()).hasEye()) {
                    if (event.getItem().getType().equals(Material.ENDER_EYE)) {
                        event.setCancelled(true);
                        Location blockLocation = block.getLocation().toBlockLocation();
                        if (team instanceof ParticipantTeam participantTeam) {
                            Location teamBlockLocation = participantTeam.getFrameLocation().toBlockLocation();
                            if (blockLocation.clone().setDirection(teamBlockLocation.getDirection()).equals(teamBlockLocation)) {
                                player.getInventory().setItem(event.getHand(), player.getInventory().getItem(event.getHand()).subtract(1));
                                participantTeam.placeEye(this);
                            }
                        } else if (team instanceof GodTeam) {
                            for (ParticipantTeam checkTeam : teamManager.getParticipantTeams()) {
                                Location teamBlockLocation = checkTeam.getFrameLocation().toBlockLocation();
                                if (blockLocation.clone().setDirection(teamBlockLocation.getDirection()).equals(teamBlockLocation)) {
                                    checkTeam.placeEye(this);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
