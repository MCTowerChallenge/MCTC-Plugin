package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.bottlepuzzle;

import io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.Apr2023QuestInstance;
import io.github.mystievous.towerchallenge.quests.QuestItems;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BottleManager {

    private final Apr2023QuestInstance instance;

    private final BottleDisplay[] bottleDisplays;

    public BottleManager(Plugin plugin, Apr2023QuestInstance instance, Location leftBottle) {
        this.instance = instance;
        TowerTeam team = instance.getTeam();
        this.bottleDisplays = new BottleDisplay[]{
                new BottleDisplay(plugin, this, team, 1, leftBottle, BottleDisplay.BottleColor.RED),
                new BottleDisplay(plugin, this, team, 2, leftBottle.clone().add(0, 0, 0.75), BottleDisplay.BottleColor.ORANGE),
                new BottleDisplay(plugin, this, team, 3, leftBottle.clone().add(0, 0, 1.5), BottleDisplay.BottleColor.YELLOW),
                new BottleDisplay(plugin, this, team, 4, leftBottle.clone().add(0, 0, 2.25), BottleDisplay.BottleColor.GREEN),
                new BottleDisplay(plugin, this, team, 5, leftBottle.clone().add(0, 0, 3.0), BottleDisplay.BottleColor.BLUE),
                new BottleDisplay(plugin, this, team, 6, leftBottle.clone().add(0, 0, 3.75), BottleDisplay.BottleColor.PURPLE),
                new BottleDisplay(plugin, this, team, 7, leftBottle.clone().add(0, 0, 4.5), BottleDisplay.BottleColor.BLACK)
        };

        for (BottleDisplay.BottleColor color : BottleDisplay.BottleColor.values()) {
            QuestItems.putItem(color.getLabel(), BottleDisplay.createPotionItem(plugin, color));
        }

    }

    public void reset() {
        List<Chunk> chunks = Arrays.stream(bottleDisplays).map(BottleDisplay::getLocation).map(Location::getChunk).toList();
        for (Chunk chunk : chunks) {
            if (!chunk.isEntitiesLoaded()) {
                chunk.load();
            }
        }
        for (BottleDisplay display : bottleDisplays) {
            display.reset();
        }
        for (Chunk chunk : chunks) {
            if (chunk.isLoaded()) {
                chunk.unload(true);
            }
        }
    }

    /**
     * Checks all criteria for the bottle
     * puzzle being complete
     */
    public void checkBottles() {

        int redPos = -1;
        int orangePos = -1;
        int yellowPos = -1;
        int greenPos = -1;
        int bluePos = -1;
        int purplePos = -1;
        int blackPos = -1;

        for (int i = 0; i < bottleDisplays.length; i++) {
            BottleDisplay.BottleColor color = bottleDisplays[i].getColor();

            if (color == null) {
                return;
            }

            switch (color) {
                case RED -> redPos = i;
                case ORANGE -> orangePos = i;
                case YELLOW -> yellowPos = i;
                case GREEN -> greenPos = i;
                case BLUE -> bluePos = i;
                case PURPLE -> purplePos = i;
                case BLACK -> blackPos = i;
            }
        }

//        Bukkit.getServer().sendMessage(Component.text(String.format("Bottles: R%d O%d Y%d G%d B%d P%d B%d", redPos, orangePos, yellowPos, greenPos, bluePos, purplePos, blackPos)));

        if (redPos == -1 || orangePos == -1 || yellowPos == -1 || greenPos == -1 || bluePos == -1 || purplePos == -1 || blackPos == -1) {
            return;
        }

        // Red must be not next to Blue, Green, or Yellow;
        if (Math.abs(redPos - bluePos) <= 1 || Math.abs(redPos - greenPos) <= 1 || Math.abs(redPos - yellowPos) <= 1) {
            return;
        }

        // Blue must be to the left of green
        if (bluePos >= greenPos) {
            return;
        }

        // Purple must be to the left of orange
        if (purplePos >= orangePos) {
            return;
        }

        // Black must be next to green
        if (Math.abs(blackPos - greenPos) > 1) {
            return;
        }

        // Yellow must be to the right of Green and Red
        if (yellowPos < redPos || yellowPos < greenPos) {
            return;
        }

        // Orange must be to the right of blue
        if (orangePos <= bluePos) {
            return;
        }

        // Green must not be at either end of the shelf
        if (greenPos == 0 || greenPos == bottleDisplays.length - 1) {
            return;
        }

        instance.badCellar.openDoor();

    }

}
