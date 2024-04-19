package io.github.mctowerchallenge.mctcplugin.decoration.customblock;

import com.sk89q.worldedit.math.Vector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CustomBlock {

    private final ItemStack model;

    private Vector3f scale;
    private Vector3f translation;
    private Quaternionf leftRot;
    private Quaternionf rightRot;

    public CustomBlock(ItemStack model) {
        this.model = model;
        scale = new Vector3f(1, 1, 1);
        translation = new Vector3f();
        leftRot = new Quaternionf(0, 0, 0, 1);
        rightRot = new Quaternionf(0, 0, 0, 1);
    }

    public ItemStack getModel() {
        return model;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public void setRotation(Quaternionf leftRot, Quaternionf rightRot) {
        this.leftRot = leftRot;
        this.rightRot = rightRot;
    }

    public void placeBlock(Location blockLocation) {
        Block block = blockLocation.getBlock();
        block.setType(Material.BARRIER);
        Location customLocation = block.getLocation().add(0.5, 0.5, 0.5).setDirection(blockLocation.getDirection());
        ItemDisplay itemDisplay = (ItemDisplay) customLocation.getWorld().spawnEntity(customLocation, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(model);
        itemDisplay.setTransformation(new Transformation(
                translation,
                leftRot,
                scale,
                rightRot
        ));
        itemDisplay.addScoreboardTag(CustomBlockManager.CUSTOM_BLOCK_TAG);
    }
}
