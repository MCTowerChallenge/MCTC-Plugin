package io.github.idkahn.towerchallenge.spawncompass;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnCompass {

    public static void giveCompass(Player player) {
        player.setCompassTarget(new Location(player.getWorld(), -1330.0d, 68.0d, -1249.0d));
        ItemStack compass = new ItemStack(Material.COMPASS);
        player.getInventory().addItem(compass);
    }

}
