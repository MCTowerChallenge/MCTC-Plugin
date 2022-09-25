package io.github.idkahn.towerchallenge.Commands;

import java.util.HashMap;

public class CommandArgument {

    // Instance Variables
    private String name;
    private HashMap<String, CommandArgument> children;

    // Constructor
    public CommandArgument(String name) {
        this.name = name;
        this.children = new HashMap<>();
    }

    // Accessors and Mutators
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a child argument to this component
     * @return Child object that was added
     */
    public CommandArgument addChild(CommandArgument newArgument) {
        children.put(newArgument.getName(), newArgument);
        return children.get(newArgument.getName());
    }

    public HashMap<String, CommandArgument> getChildren() {
        return children;
    }
}
