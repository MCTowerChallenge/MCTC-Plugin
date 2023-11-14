package io.github.mctowerchallenge.mctcplugin;

import io.github.mystievous.mystimer.Timer;
import io.github.mystievous.mystimer.exception.TimerUnsetException;
import io.github.mctowerchallenge.mctcplugin.portal.PortalControllers;
import io.github.mctowerchallenge.mctcplugin.timer.TimerCommands;
import org.bukkit.Bukkit;

/**
 * Manages the flow of the game events and intermissions.
 */
public class GameFlowManager {

    private final Timer timer;
    private final PortalControllers portalControllers;

    /**
     * Creates a new GameFlowManager instance.
     *
     * @param timer The timer instance.
     * @param portalControllers Controllers for portals
     */
    public GameFlowManager(Timer timer, PortalControllers portalControllers) {
        this.timer = timer;
        this.portalControllers = portalControllers;
    }

    /**
     * Starts the game event.
     */
    public void startEvent() {
        try {
            timer.reset();
            timer.startTimer();
        } catch (TimerUnsetException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

    /**
     * Ends the intermission and resumes the timer.
     */
    public void endIntermission() {
        portalControllers.getNetherPortal().openPortal();
        try {
            timer.startTimer();
            TimerCommands.announceResume();
        } catch (TimerUnsetException e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }

}
