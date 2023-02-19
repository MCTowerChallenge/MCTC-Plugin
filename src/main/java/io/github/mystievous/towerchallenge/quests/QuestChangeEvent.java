package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.teams.TowerTeam;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private boolean isCancelled;
    private final TowerTeam team;
    private final Quest quest;
    private final Quest prevQuest;

    public QuestChangeEvent(TowerTeam team, @Nullable Quest quest, @Nullable Quest prevQuest) {
        this.team = team;
        this.quest = quest;
        this.prevQuest = prevQuest;
        isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public TowerTeam getTeam() {
        return team;
    }

    public @Nullable Quest getQuest() {
        return quest;
    }

    public @Nullable Quest getPrevQuest() {
        return prevQuest;
    }
}
