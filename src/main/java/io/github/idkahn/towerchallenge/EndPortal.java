package io.github.idkahn.towerchallenge;

import io.github.idkahn.towerchallenge.towering.GodTeam;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.naming.Name;
import javax.xml.validation.SchemaFactoryConfigurationError;
import java.time.Duration;

public class EndPortal implements Listener {

    private EventManager eventManager;

    public EndPortal(EventManager eventManager) {
        this.eventManager = eventManager;
        Bukkit.getServer().getPluginManager().registerEvents(this, eventManager.getPlugin());
    }

    public void openPortal() {
        Bukkit.getServer().sendMessage(Component.text("<", NamedTextColor.WHITE)
                .append(Component.text("Herobrine", NamedTextColor.DARK_RED))
                .append(Component.text("> You don't know what you did....", NamedTextColor.WHITE)));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            TowerTeam team = eventManager.getTowerListener().getPlayerTeam(player);
            if (block.getType().equals(Material.END_PORTAL_FRAME)) {
                if (!((EndPortalFrame) block.getBlockData()).hasEye()) {
                    if (event.getItem().getType().equals(Material.ENDER_EYE)) {
                        event.setCancelled(true);
                        if (block.getLocation().equals(team.getFrameLocation())) {
                            team.placeEye();
                        } else if (team instanceof GodTeam) {
                            eventManager.getTowerListener().getTeams().forEach((key, checkTeam) -> {
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
