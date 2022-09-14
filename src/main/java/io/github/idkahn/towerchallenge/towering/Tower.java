package io.github.idkahn.towerchallenge.towering;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;

import java.util.ArrayList;

public class Tower {

    ArrayList<BlockState> blocks;
    int[][] corners;
    int towerBase;

    public Tower() {
        corners = new int[][]{
                {0, 0},
                {0, 0}
        };
        towerBase = Bukkit.getServer().getWorld("world").getMinHeight();
        blocks = new ArrayList<>();
    }

    public void setCorner(int[] coords) {

    }

}
