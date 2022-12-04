package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.gods.GodTeam;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
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

    private final ChallengeManager challengeManager;

    public EndPortal(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
        Bukkit.getServer().getPluginManager().registerEvents(this, challengeManager.getPlugin());
    }

    public void openPortal() {
        for (int x = -116; x <= -114; x++) {
            for (int z = -191; z <= -187; z++) {
                Location block = new Location(Bukkit.getWorld("December MCTC_nether"), x, 93, z);
                block.getBlock().setType(Material.END_PORTAL);
            }
        }
//        Bukkit.getServer().sendMessage(Component.text("<", NamedTextColor.WHITE)
//                .append(Component.text("Herobrine", NamedTextColor.DARK_RED))
//                .append(Component.text("> You don't know what you did....", NamedTextColor.WHITE)));
        Bukkit.getServer().sendMessage(Component.text("End Portal ").color(TextColor.fromHexString("#f0ffd0"))
                .append(Component.text("has been opened!").color(NamedTextColor.WHITE)));
        Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.end_portal.spawn"), Sound.Source.MASTER, 100, 1));
        final Title title = Title.title(Component.text("End Portal").color(TextColor.fromHexString("#f0ffd0")), Component.text("has been opened!").color(NamedTextColor.WHITE));
        Bukkit.getServer().showTitle(title);
    }

    public void resetPortal() {
        for (int x = -116; x <= -114; x++) {
            for (int z = -191; z <= -187; z++) {
                Location block = new Location(Bukkit.getWorld("December MCTC_nether"), x, 93, z);
                block.getBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            TowerTeam team = challengeManager.getTowerListener().getPlayerTeam(player);
            assert block != null;
            if (block.getType().equals(Material.END_PORTAL_FRAME)) {
                if (!((EndPortalFrame) block.getBlockData()).hasEye()) {
                    if (event.getItem().getType().equals(Material.ENDER_EYE)) {
                        event.setCancelled(true);
                        if (team instanceof ParticipantTeam participantTeam
                                && block.getLocation().equals(participantTeam.getFrameLocation())) {
                            player.getInventory().setItem(event.getHand(), player.getInventory().getItem(event.getHand()).subtract(1));
                            participantTeam.placeEye();
                        } else if (team instanceof GodTeam) {
                            challengeManager.getTowerListener().getTeams().forEach((key, checkTeam) -> {
                                if (block.getLocation().equals(checkTeam.getFrameLocation())) {
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
