package io.github.idkahn.towerchallenge.spawncompass;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class SpawnCompass {

    public static void giveCompass(Player player) {
        player.setCompassTarget(new Location(player.getWorld(), -1330.0d, 68.0d, -1249.0d));
        ItemStack compass = new ItemStack(Material.COMPASS);
//        CompassMeta meta = (CompassMeta) compass.getItemMeta();
//        meta.setLodestone(new Location(player.getWorld(), -1330.0d, 68.0d, -1249.0d));
//        compass.setItemMeta(meta);
        player.getInventory().addItem(compass);
    }

}
