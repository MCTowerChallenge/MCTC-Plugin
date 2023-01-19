package io.github.mystievous.towerchallenge.magic;

import io.github.mystievous.towerchallenge.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Goat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.security.SecureRandom;

public class GoatHat implements Listener {

    public static final String GOAT_HAT = "goat-hat";

    private static final SecureRandom RANDOM = new SecureRandom();

    public GoatHat() {
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    /**
     * Creates a goat at a random location 10 blocks up
     * and within 3 blocks of the input location
     *
     * @param location The location of the summoning point
     */
    public void createGoat(Location location) {
        // 3 blocks out each direction, spawn 8-10 blocks up
        Location goatLocation = location.add(RANDOM.nextInt(6) - 3, 10, RANDOM.nextInt(6) - 3);
        Goat goat = (Goat) goatLocation.getWorld().spawnEntity(location, EntityType.GOAT);
        goat.clearLootTable();
        goat.setInvulnerable(true);
        goat.setScreaming(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), () -> {
            goat.setHealth(0.0d);
        }, 100);
    }

    @EventHandler
    public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            if (NBTUtils.boolState(GOAT_HAT, event.getPlayer().getEquipment().getHelmet())) {
                createGoat(event.getPlayer().getLocation());
            }
        }
    }


}
