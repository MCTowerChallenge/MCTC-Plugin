package io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.quests.minesweeper;

import io.github.mystievous.mysticore.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling Minesweeper-related tasks.
 */
public class MineSweeperUtil {

    /**
     * A map of integer values to corresponding colors for formatting text.
     */
    public static final Map<Integer, Color> COLORS = new HashMap<>(){{
        put(1, new Color(0x0000ff));
        put(2, new Color(0x007b00));
        put(3, new Color(0xff0000));
    }};

    /**
     * Formats an integer value as colored text.
     *
     * @param value The value to format.
     * @return A formatted Component containing the value in its corresponding color.
     */
    public static Component formatText(int value) {
        Color color = COLORS.getOrDefault(value, new Color(0xffffff));
        return Component.translatable(String.format("space.-%d", String.valueOf(value).length()))
                .append(Component.text(value)).color(color.toTextColor());
    }

    /**
     * Places formatted text above a block.
     *
     * @param block The block to place the text above.
     * @param value The value to display in the text.
     * @param tag   The scoreboard tag to apply to the displayed text entity.
     */
    public static void placeText(Block block, int value, String tag) {
        Location textLocation = block.getLocation().clone().add(1, 1, 0);
        TextDisplay textDisplay = (TextDisplay) block.getWorld().spawnEntity(textLocation, EntityType.TEXT_DISPLAY);
        textDisplay.text(formatText(value));
        textDisplay.setTransformation(new Transformation(
                new Vector3f(0.2f, 0f, 0.5f),
                new Quaternionf(0f, -0.707f, 0f, 0.707f),
                new Vector3f(2f, 2f, 2f),
                new Quaternionf(-0.707f, 0f, 0f, 0.707f)
        ));
        textDisplay.setRotation(90, 0);
        textDisplay.addScoreboardTag(tag);
        Bukkit.dispatchCommand(Bukkit.createCommandSender(component -> {
        }), String.format("data modify entity %s background set value 0", textDisplay.getUniqueId()));
    }

}
