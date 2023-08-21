package io.github.mystievous.towerchallenge.eventspecific.jun2023.gallery;

import org.bukkit.Location;

/**
 * Custom exception for when a target is attempted to be activated
 * but is already active.
 */
public class TargetAlreadyActivateException extends Exception {

    private final Location location;

    /**
     * Constructs a new instance of the exception.
     *
     * @param location The location of the target that is already active.
     */
    public TargetAlreadyActivateException(Location location) {
        super("Target is already active and cannot be activated until it is done.");
        this.location = location;
    }

    /**
     * Gets the location of the target that caused the exception.
     *
     * @return The location of the active target.
     */
    public Location getLocation() {
        return location;
    }
}
