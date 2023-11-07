package io.github.mystievous.towerchallenge.eventspecific.oct2023.quest;

import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quest.QuestManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class Trivia {

    public static final Location TEMPLATE_ENTER_LOCATION = new Location(Worlds.Oct2023_quest(), -31.5, 65.0, -6.5, -90.0f, 0.0f);
    public static final Location TEMPLATE_LEAVE_LOCATION = new Location(Worlds.Oct2023_quest(), -10.0, 73.0, -5.0, 90.0f, 0.0f);

    public static final int BLOCK_HEIGHT_BETWEEN_ROOMS = 7;

    private final Oct2023QuestInstance questInstance;
    private final Location instanceLeaveLocation;
    private final Location instanceEnterLocation;
    
    public Trivia(Oct2023QuestInstance questInstance, Plugin plugin) {
        this.questInstance = questInstance;
        this.instanceLeaveLocation = questInstance.offsetLocation(TEMPLATE_LEAVE_LOCATION);
        this.instanceEnterLocation = questInstance.offsetLocation(TEMPLATE_ENTER_LOCATION);

        new TriviaRoom("Witch Huts", new int[]{2}, 0, this, questInstance, plugin, false);
        new TriviaRoom("Steve", new int[]{3}, 1, this, questInstance, plugin, false);
        new TriviaRoom("Pumpkins", new int[]{1}, 2, this, questInstance, plugin, false);
        new TriviaRoom("Nether", new int[]{2}, 3, this, questInstance, plugin, false);
        new TriviaRoom("Scary", new int[]{0}, 4, this, questInstance, plugin, false);
        new TriviaRoom("Heads", new int[]{3}, 5, this, questInstance, plugin, false);
        new TriviaRoom("Snowballs", new int[]{2}, 6, this, questInstance, plugin, false);
        new TriviaRoom("Mushrooms", new int[]{0}, 7, this, questInstance, plugin, false);
        new TriviaRoom("Fermented", new int[]{1}, 8, this, questInstance, plugin, false);
        new TriviaRoom("Fun", new int[]{0, 1, 2, 3}, 9, this, questInstance, plugin, true);

    }

    public Location getLeaveLocation() {
        return instanceLeaveLocation;
    }

    public Location getEnterLocation() {
        return instanceEnterLocation;
    }

    public void exitTeleport(Entity entity) {
        entity.teleport(questInstance.offsetLocation(TEMPLATE_LEAVE_LOCATION));
    }

}
