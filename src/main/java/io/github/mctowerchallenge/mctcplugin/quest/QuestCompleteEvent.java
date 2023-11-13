package io.github.mctowerchallenge.mctcplugin.quest;

import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestCompleteEvent extends Event implements Cancellable {

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

    /**
     * @param team      The team changing quests.
     * @param quest     The new quest being changed to.
     */
    public QuestCompleteEvent(TowerTeam team, Quest quest) {
        this.team = team;
        this.quest = quest;
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

    public Quest getQuest() {
        return quest;
    }
}
