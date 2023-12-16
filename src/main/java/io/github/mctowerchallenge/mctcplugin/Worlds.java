package io.github.mctowerchallenge.mctcplugin;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class Worlds {

    public static World Oct2023_the_end() {
        return Bukkit.getWorld("Oct2023_the_end");
    }

    public static World Oct2023_tower() {
        return Bukkit.getWorld("Oct2023_tower");
    }

    public static World WORLD() {
        return Bukkit.getWorld("world");
    }

    public static World NETHER() {
        return Bukkit.getWorld("world_nether");
    }

    public static World THE_END() {
        return Oct2023_the_end();
    }

    public static World TOWER() {
        return Oct2023_tower();
    }

}
