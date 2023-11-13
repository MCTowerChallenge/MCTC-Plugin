package io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.quests.minesweeper.gamepieces;

import io.github.mystievous.mysticore.SoundUtil;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.quests.minesweeper.MineHandler;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.quests.minesweeper.MineSweeperUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.data.Brushable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class GamePiece {

    private final Plugin plugin;
    private final MineHandler mineHandler;
    private final Location location;
    private final int mines;

    private boolean swept;

    private Flag flag;

    public GamePiece(Plugin plugin, MineHandler mineHandler, Location location, int mines) {
        this.plugin = plugin;
        this.mineHandler = mineHandler;
        this.location = location;
        this.mines = mines;

        Block block = location.getBlock();
        block.setType(Material.SUSPICIOUS_SAND);

        this.swept = false;

    }

    /**
     * Gets the location of the game piece.
     *
     * @return The location of the game piece.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the number of mines adjacent to the game piece.
     *
     * @return The number of adjacent mines.
     */
    public int getMines() {
        return mines;
    }

    /**
     * Checks if the game piece has been swept.
     *
     * @return True if the game piece has been swept, false otherwise.
     */
    public boolean isSwept() {
        return swept;
    }

    /**
     * Sets the item displayed on the game piece.
     *
     * @param item The item to set.
     */
    public void setItem(ItemStack item) {
        Block block = location.getBlock();
        if (block.getState() instanceof BrushableBlock brushableBlock && mines < 0) {
            brushableBlock.setItem(item);
            brushableBlock.update();
        }
    }

    /**
     * Sweeps the game piece, revealing its content and performing appropriate actions.
     * If the piece contains a mine, it triggers an explosion; otherwise, it updates the block and nearby pieces.
     */
    public void sweep() {
        if (!swept && !hasFlag()) {
            swept = true;
            if (mines == -1) {
                Brushable brushable = (Brushable) location.getBlock().getBlockData();
                if (brushable.getDusted() != 2) {
                    brushable.setDusted(2);
                    location.getBlock().setBlockData(brushable);
                }
                SoundUtil.errorNoise(Bukkit.getServer(), getLocation());
                setItem(new ItemStack(Material.TNT));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    location.clone().add(0.5, 1, 0.5).createExplosion(1, false, false);
                    location.getBlock().setType(Material.TNT);
                }, 15);
            } else {
                Block block = location.getBlock();
                Material type = block.getType();
                block.setType(type.equals(Material.SUSPICIOUS_SAND) ? Material.SANDSTONE : Material.GRAVEL);
                if (mines == 0) {
                    mineHandler.clearAround(this);
                } else {
                    MineSweeperUtil.placeText(location.getBlock(), mines, mineHandler.getTeamRemoveTag());
                    mineHandler.clearAround(this);
                }
            }
        }
    }

    /**
     * Checks if the game piece has a flag.
     *
     * @return True if the game piece has a flag, false otherwise.
     */
    public boolean hasFlag() {
        return flag != null;
    }

    /**
     * Places a flag on the game piece using the provided item stack.
     *
     * @param itemStack The item stack representing the flag.
     * @return True if the flag was placed successfully, false otherwise.
     */
    public boolean placeFlag(ItemStack itemStack) {
        if (flag == null && !swept) {
            flag = new Flag(plugin, mineHandler, itemStack, location);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the flag from the game piece.
     */
    public void removeFlag() {
        if (flag != null) {
            flag.remove();
            flag = null;
        }
    }

}
