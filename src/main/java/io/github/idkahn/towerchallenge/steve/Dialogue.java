package io.github.idkahn.towerchallenge.steve;

import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Function;

public class Dialogue {

    private static final Component STEVE_HEADER = Component.text("<steve skellington> ").color(TowerChallenge.PRIMARY_COLOR);

    public static Component steveMessage(Component message) {
        return Component.text()
                .append(STEVE_HEADER)
                .append(message)
                .build();
    }

    public static Component steveMessage(String message) {
        return steveMessage(Component.text(message).color(NamedTextColor.GRAY));
    }

    private final SteveManager steveManager;
    private Dialogue next;
    private final Component message;
    private final long delay;
    private Consumer<Player> callback;

    public Dialogue(SteveManager steveManager, Component text, long delay, Consumer<Player> callback) {
        this.steveManager = steveManager;
        this.message = Component.text()
                .append(STEVE_HEADER)
                .append(text)
                .build();
        this.delay = delay * 20L;
        this.callback = callback;
    }

    public Dialogue(SteveManager steveManager, Component text, long delay) {
        this(steveManager, text, delay, null);
    }

    public Dialogue setNext(Dialogue next) {
        this.next = next;
        return this.next;
    }

    public Dialogue setCallback(Consumer<Player> callback) {
        this.callback = callback;
        return this;
    }

    public void play(Player player) {
        TowerTeam team = steveManager.getEventManager().getTowerListener().getPlayerTeam(player);
        if (team == null) {
            return;
        }
        steveManager.setTeamStage(team, -1);
        team.getAudience().sendMessage(message);
        if (next != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(steveManager.getEventManager().getPlugin(), () -> next.play(player), delay);
        } else {
            callback.accept(player);
//            steveManager.setTeamStage(team, 2);
        }
    }

}
