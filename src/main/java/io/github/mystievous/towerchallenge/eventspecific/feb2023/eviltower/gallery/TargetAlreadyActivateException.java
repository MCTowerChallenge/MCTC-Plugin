package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.gallery;

import org.bukkit.Location;

public class TargetAlreadyActivateException extends Exception {

    private final Location location;

    public TargetAlreadyActivateException(Location location) {
        super("Target is already active and cannot be activated until it is done.");
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
