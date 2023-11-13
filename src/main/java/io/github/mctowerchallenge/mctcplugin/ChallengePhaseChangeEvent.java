package io.github.mctowerchallenge.mctcplugin;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is called when the event phase changes.
 */
public class ChallengePhaseChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final ChallengeManager.ChallengePhase challengePhase;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Constructs a new ChallengePhaseChangeEvent.
     *
     * @param challengePhase The new challenge phase.
     */
    public ChallengePhaseChangeEvent(ChallengeManager.ChallengePhase challengePhase) {
        this.challengePhase = challengePhase;
    }

    /**
     * Gets the new challenge phase.
     *
     * @return The new challenge phase.
     */
    public ChallengeManager.ChallengePhase getChallengePhase() {
        return challengePhase;
    }
}
