package io.github.mystievous.towerchallenge.spawncompass;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.mysticore.Palette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpawnCompass implements Listener {

    public static final Location OVERWORLD_LOCATION = new Location(Worlds.Jun2023(), 199, 63, -2203);
    public static final Location NETHER_LOCATION = new Location(Worlds.Jun2023_nether(), 62, 72, -293);
    public static final Location THE_END_LOCATION = new Location(Worlds.THE_END(), 0.0d, 0.0d, 0.0d);

    /**
     * Sets the player's compass location
     * to point to spawn.
     * <p></p>
     * If the player is in the overworld,
     * will set the regular player compass
     * target. Otherwise, will make the
     * compass a lodestone compass
     * targeted there.
     *
     * @param player  The player whose compass target to set.
     * @param compass The compass to set the target of.
     * @return The changed compass item.
     */
    public static ItemStack refreshPlayerDestination(Player player, ItemStack compass) {
        String playerWorldName = player.getLocation().getWorld().getName();
        if (playerWorldName.equals(Worlds.NETHER().getName())) {
            if (compass.getItemMeta() instanceof CompassMeta compassMeta) {
                compassMeta.setLodestone(NETHER_LOCATION);
                compassMeta.setLodestoneTracked(false);
                compass.setItemMeta(compassMeta);
            }
            player.setCompassTarget(NETHER_LOCATION);
        } else if (playerWorldName.equals(Worlds.THE_END().getName())) {
            if (compass.getItemMeta() instanceof CompassMeta compassMeta) {
                compassMeta.setLodestone(THE_END_LOCATION);
                compassMeta.setLodestoneTracked(false);
                compass.setItemMeta(compassMeta);
            }
            player.setCompassTarget(THE_END_LOCATION);
        } else {
            if (compass.getItemMeta() instanceof CompassMeta compassMeta) {
                compassMeta.setLodestone(null);
                compassMeta.setLodestoneTracked(false);
                compass.setItemMeta(compassMeta);
            }
            player.setCompassTarget(OVERWORLD_LOCATION);
        }
        return compass;
    }

    /**
     * Scans the inventory of a player
     * for any compasses, and refreshes
     * them if it finds any.
     *
     * @param player The player to check.
     */
    public static void refreshAllPlayer(Player player) {
        HashMap<Integer, ? extends ItemStack> compasses = player.getInventory().all(Material.COMPASS);

        for (Map.Entry<Integer, ? extends ItemStack> entry : compasses.entrySet()) {
            player.getInventory().setItem(entry.getKey(), refreshPlayerDestination(player, entry.getValue()));
        }

    }

    /**
     * Gets a compass targeted at spawn.
     *
     * @return The compass.
     */
    public static ItemStack getCompass() {
        ItemStack item = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        meta.displayName(Component.text("home home"));
        meta.lore(new ArrayList<>() {{
            add(Component.keybind("key.use")
                    .append(Component.text(" to point home :)"))
                    .color(Palette.PRIMARY.toTextColor()).decoration(TextDecoration.ITALIC, false)
            );
        }});
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public SpawnCompass() {
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    /**
     * Refreshes a player's compass when they right-click it.
     *
     * @param event The interact event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getAction() == Action.PHYSICAL
                || event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK
                || item == null)
            return;
        if (item.getType().equals(Material.COMPASS)) {
            refreshAllPlayer(player);
        }
    }

    @EventHandler
    public void onDimensionChange(PlayerPortalEvent event) {
        refreshAllPlayer(event.getPlayer());
    }

}
