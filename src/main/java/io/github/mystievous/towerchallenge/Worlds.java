package io.github.mystievous.towerchallenge;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class Worlds {

    public static World Oct2022() {
        return Bukkit.getWorld("Oct2022");
    }
    public static World Oct2022_nether() {
        return Bukkit.getWorld("Oct2022_nether");
    }

    public static World Dec2022() {
        return Bukkit.getWorld("Dec2022");
    }

    public static World Feb2023() {
        return Bukkit.getWorld("Feb2023");
    }
    public static World Feb2023_nether() {
        return Bukkit.getWorld("Feb2023_nether");
    }
    public static World Feb2023_the_end() {
        return Bukkit.getWorld("Feb2023_the_end");
    }
    public static World Feb2023_tower() {
        return Bukkit.getWorld("Feb2023_tower");
    }

    public static World GodInterviews() {
        return Bukkit.getWorld("GodInterviews");
    }

    public static World eviltowers() {
        return Bukkit.getWorld("eviltowers");
    }

    public static World WORLD() {
        return Feb2023();
    }

    public static World NETHER() {
        return Feb2023_nether();
    }

    public static World THE_END() {
        return Feb2023_the_end();
    }
}
