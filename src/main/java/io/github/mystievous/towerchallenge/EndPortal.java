package io.github.mystievous.towerchallenge;

import io.github.mystievous.towerchallenge.gods.GodTeam;
import io.github.mystievous.towerchallenge.teams.ParticipantTeam;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
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

public class EndPortal implements Listener {

    private final TeamManager teamManager;

    public static final Location PORTAL_MIN = new Location(Worlds.Feb2023(), 108, 64, -2109);
    public static final Location PORTAL_MAX = new Location(Worlds.Feb2023(), 111, 64, -2106);

    public EndPortal(TowerChallenge plugin, TeamManager teamManager) {
        this.teamManager = teamManager;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

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

    public void resetPortal() {
        for (int x = PORTAL_MIN.getBlockX(); x <= PORTAL_MAX.getBlockX(); x++) {
            for (int z = PORTAL_MIN.getBlockZ(); z <= PORTAL_MAX.getBlockZ(); z++) {
                Location block = new Location(PORTAL_MIN.getWorld(), x, PORTAL_MIN.getY(), z);
                block.getBlock().setType(Material.AIR);
            }
        }
    }

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
                                participantTeam.placeEye();
                            }
                        } else if (team instanceof GodTeam) {
                            teamManager.getParticipantTeams().forEach(checkTeam -> {
                                Location teamBlockLocation = checkTeam.getFrameLocation().toBlockLocation();
                                if (blockLocation.clone().setDirection(teamBlockLocation.getDirection()).equals(teamBlockLocation)) {
                                    checkTeam.placeEye();
                                }
                            });
                        }
                    }
                }
            }
        }
    }

 }
