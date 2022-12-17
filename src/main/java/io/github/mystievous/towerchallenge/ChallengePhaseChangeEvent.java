package io.github.mystievous.towerchallenge;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChallengePhaseChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private ChallengeManager.ChallengePhase challengePhase;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public ChallengePhaseChangeEvent(ChallengeManager.ChallengePhase challengePhase) {
        this.challengePhase = challengePhase;
    }

    public ChallengeManager.ChallengePhase getChallengePhase() {
        return challengePhase;
    }
}
