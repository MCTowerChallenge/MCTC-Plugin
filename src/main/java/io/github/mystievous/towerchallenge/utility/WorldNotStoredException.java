package io.github.mystievous.towerchallenge.utility;

/**
 * Thrown when a world is not currently
 * defined in the database.
 */
public class WorldNotStoredException extends Exception {

    public WorldNotStoredException(String string) {
        super(string);
    }
}
