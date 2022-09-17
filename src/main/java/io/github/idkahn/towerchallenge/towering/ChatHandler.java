package io.github.idkahn.towerchallenge.towering;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Team;

public class ChatHandler implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Team playerTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(event.getPlayer());

        ComponentBuilder message = Component.text();

        if (playerTeam != null) {
            message.append(playerTeam.prefix());
        }

        message.append(Component.text("<").append(event.getPlayer().name()).append(Component.text("> ")))
                .append(event.message())
                .build();

        for (Audience audience : event.viewers()) {
            audience.sendMessage(message);
        }

        event.setCancelled(true);

    }

}
