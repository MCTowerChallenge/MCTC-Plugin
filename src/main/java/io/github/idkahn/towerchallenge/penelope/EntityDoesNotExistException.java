package io.github.idkahn.towerchallenge.penelope;

public class EntityDoesNotExistException extends Exception {
    public EntityDoesNotExistException() {
        super("The selected entity does not exist.");
    }
}
