package io.github.idkahn.towerchallenge.misc.resourcepack;

import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourcePackListener implements Listener {

    private Map<UUID, Integer> attempts;

    public ResourcePackListener(ChallengeManager manager) {
        Bukkit.getPluginManager().registerEvents(this, manager.getPlugin());
        attempts = new HashMap<>();
    }

    @EventHandler
    public void onPlayerResourcePackChange(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)) {
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.RECORDS, 1f, 1f);
            attempts.putIfAbsent(uuid, 0);
            attempts.put(uuid, attempts.get(uuid)+1); // increment attempts for player
            int playerAttempts = attempts.get(uuid);
            if (playerAttempts > 3) {
                player.sendMessage(
                        Component.text("[ERROR] Unable to load resource pack! Manually reload with ")
                                .append(Component.text("/resourcepack").color(TowerChallenge.PRIMARY_COLOR))
                                .append(Component.text(" or "))
                                .append(Component.text("/rp").color(TowerChallenge.PRIMARY_COLOR))
                                .append(Component.text(" or try relogging."))
                                .color(NamedTextColor.DARK_RED));
            } else {
                player.sendMessage(
                        Component.text(String.format("Resource pack failed to load, attempting to retry... (%d)", playerAttempts))
                                .color(NamedTextColor.DARK_RED));
                ResourcePack.sendResourcePack(player);
            }
        } else if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)) {
            player.sendMessage(
                    Component.text("It looks like you have declined the resource pack.")
                            .append(Component.newline()).append(Component.newline())
                            .append(Component.text("For the best experience, please logout and change your server settings so it is enabled! :)"))
                            .color(NamedTextColor.RED));
        } else if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)) {
            player.sendMessage(
                    Component.text("If you have issues with the resource pack, manually reload with ")
                            .append(Component.text("/resourcepack").color(TowerChallenge.PRIMARY_COLOR))
                            .append(Component.text(" or "))
                            .append(Component.text("/rp").color(TowerChallenge.PRIMARY_COLOR))
                            .append(Component.text(" or try relogging."))
                            .color(NamedTextColor.DARK_RED));
        }
    }

}
