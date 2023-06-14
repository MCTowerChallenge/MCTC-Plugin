package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.minesweeper;

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

public class MineSweeperUtil {

    public static final Map<Integer, Color> COLORS = new HashMap<>(){{
        put(1, new Color(0x0000ff));
        put(2, new Color(0x007b00));
        put(3, new Color(0xff0000));
    }};

    public static Component formatText(int value) {
        Color color = COLORS.getOrDefault(value, new Color(0xffffff));
        return Component.translatable(String.format("space.-%d", String.valueOf(value).length()))
                .append(Component.text(value)).color(color.toTextColor());
    }

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
