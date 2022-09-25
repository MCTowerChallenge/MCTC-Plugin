package io.github.idkahn.towerchallenge.Wands;

import de.tr7zw.nbtapi.NBTEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.mozilla.javascript.debug.DebuggableObject;

public class WandListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getAction() == Action.PHYSICAL || item == null)
            return;
        if (WandUtil.isWand(item)) {
            switch (WandUtil.getMagic(item)) {
                case(0):
                    smite(player);
                    break;
                case(1):
                    cow(player);
                    break;
            }
        }
    }

    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        NBTEntity nbtFirework = new NBTEntity(firework);
        if (nbtFirework.getPersistentDataContainer().getBoolean("isMagic")) {
            for (Entity passenger : firework.getPassengers()) {
                passenger.remove();
            }
            firework.getLocation().createExplosion(5, false, false);
            firework.remove();
            event.setCancelled(true);
        }
    }

    public void smite(Player player) {
        RayTraceResult rayTrace = player.rayTraceBlocks(50);
        if (rayTrace != null) {
            player.getWorld().strikeLightning(rayTrace.getHitBlock().getLocation());
        }
    }

    public void cow(Player player) {
        Entity cow = player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.COW);
        NBTEntity nbtCow = new NBTEntity(cow);
        nbtCow.setByte("NoAI", (byte) 1);
        nbtCow.setByte("Invulnerable", (byte) 1);
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.FIREWORK);
        NBTEntity nbtFirework = new NBTEntity(firework);
        nbtFirework.getPersistentDataContainer().setBoolean("isMagic", true);
        firework.setSilent(true);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(2);
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.FUCHSIA).trail(false).flicker(false).with(FireworkEffect.Type.BALL).build());
        firework.setFireworkMeta(fireworkMeta);
        firework.addPassenger(cow);
        firework.setShotAtAngle(true);
        firework.setVelocity(player.getEyeLocation().getDirection().multiply(1.25));
    }

}
