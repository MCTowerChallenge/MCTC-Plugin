package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.minesweeper.gamepieces;

import io.github.mystievous.mysticore.SoundUtil;
import io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.minesweeper.MineHandler;
import io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.minesweeper.MineSweeperUtil;
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

    public Location getLocation() {
        return location;
    }

    public int getMines() {
        return mines;
    }

    public boolean isSwept() {
        return swept;
    }

    public void setItem(ItemStack item) {
        Block block = location.getBlock();
        if (block.getState() instanceof BrushableBlock brushableBlock && mines < 0) {
            brushableBlock.setItem(item);
            brushableBlock.update();
        }
    }

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

    public boolean hasFlag() {
        return flag != null;
    }

    public void placeFlag(ItemStack itemStack) {
        if (flag == null && !swept) {
            flag = new Flag(plugin, mineHandler, itemStack, location);
        }
    }

    public void removeFlag() {
        if (flag != null) {
            flag.remove();
            flag = null;
        }
    }

}
