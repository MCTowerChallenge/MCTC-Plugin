package io.github.idkahn.towerchallenge.halloween.penelope;

public class EntityDoesNotExistException extends Exception {
    public EntityDoesNotExistException() {
        super("The selected entity does not exist.");
    }
}
