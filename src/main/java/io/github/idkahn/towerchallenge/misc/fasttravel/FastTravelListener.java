package io.github.idkahn.towerchallenge.misc.fasttravel;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.NBTUtils;
import io.github.idkahn.towerchallenge.TextUtil;
import io.github.idkahn.towerchallenge.TowerChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class FastTravelListener implements Listener {

    public static final String COOKIE_TAG = "fasttravel_cookie";

    public FastTravelListener() {
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
    }

    public ItemStack getCookie() {
        ItemStack cookie = NBTUtils.setBool(COOKIE_TAG, new ItemStack(Material.COOKIE));
        ItemMeta meta = cookie.getItemMeta();
        meta.displayName(Component.text("Portal Cookie").decoration(TextDecoration.ITALIC, false));
        meta.lore(TextUtil.formatText("Eat me to teleport between", "Spawn and Sweetsburg!"));
        cookie.setItemMeta(meta);
        return cookie;
    }

    public void teleportEffect(Location location) {
        World world = location.getWorld();
        world.spawnParticle(Particle.REVERSE_PORTAL, location, 20, 0.3, 0.5, 0.3, 0.5);
        world.playSound(location, Sound.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getScoreboardTags().contains("cookie")) {
            player.getInventory().addItem(getCookie());
        }
    }

    @EventHandler
    public void onPlayerEat(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        ItemStack item = event.getItem();
        RegionManager overworldManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(TowerChallenge.WORLD()));
        if (event.isCancelled())
            return;
        if (!(entity instanceof Player player))
            return;
        if (!NBTUtils.boolState(COOKIE_TAG, item))
            return;
        if (overworldManager == null)
            return;
        ApplicableRegionSet playerRegions = overworldManager.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()).toVector().toBlockPoint());
        for (ProtectedRegion region : playerRegions.getRegions()) {
            if (region.getId().equals("candy-village")) {
                if (overworldManager.hasRegion("spawn")) {
                    ProtectedRegion spawn = overworldManager.getRegion("spawn");
                    teleportEffect(player.getLocation());
                    player.teleport(BukkitAdapter.adapt(spawn.getFlag(Flags.TELE_LOC)));
                    teleportEffect(player.getLocation());
                    return;
                }
            }
            if (region.getId().equals("spawn")) {
                if (overworldManager.hasRegion("candy-village")) {
                    ProtectedRegion candyVillage = overworldManager.getRegion("candy-village");
                    teleportEffect(player.getLocation());
                    player.teleport(BukkitAdapter.adapt(candyVillage.getFlag(Flags.TELE_LOC)));
                    teleportEffect(player.getLocation());
                    return;
                }
            }
        }
        player.sendMessage(Component.text("The magic tastes weak...").decoration(TextDecoration.ITALIC, true).color(TowerChallenge.NEGATIVE_COLOR));
    }

}
