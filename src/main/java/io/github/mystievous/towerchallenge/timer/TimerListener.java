package io.github.mystievous.towerchallenge.timer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TimerListener implements Listener {

    private final Timer timer;

    public TimerListener(Timer timer) {
        this.timer = timer;
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        if (timer.isBossBarShown()) {
            event.getPlayer().showBossBar(timer.getBossBar());
        }
    }

}
