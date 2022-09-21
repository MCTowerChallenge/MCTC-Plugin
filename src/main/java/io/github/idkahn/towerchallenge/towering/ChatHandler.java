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

        if (event.isCancelled()) {
            return;
        }

        Component prefix = Component.empty();
        Component name = Component.text(String.format("<%s> ", event.getPlayer().getName()));
        Component body = event.message();

        if (playerTeam != null && playerTeam.prefix() != null) {
            prefix = playerTeam.prefix();
        }

        Component message = prefix.append(name).append(body);

        for (Audience audience : event.viewers()) {
            audience.sendMessage(message);
        }

        event.setCancelled(true);

    }

}
