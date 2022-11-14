package io.github.idkahn.towerchallenge.wands;

import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.RayTraceResult;

public class WandListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getAction() == Action.PHYSICAL
                || event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK
                || item == null)
            return;
        if (WandUtil.isWand(item)) {
            switch (WandUtil.getMagic(item)) {
                case (1) -> cow(player);
                case (2) -> smite(player);
                case (3) -> firework(player);
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
            player.getWorld().strikeLightning(rayTrace.getHitBlock().getLocation().add(0, 1, 0));
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

    public void firework(Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.FIREWORK);
        firework.setSilent(true);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(2);
        fireworkMeta.clearEffects();
        fireworkMeta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL).withColor(Color.RED).withFade(Color.RED).
                .build());
//        fireworkMeta.addEffect(FireworkEffect.builder()
//                .with(FireworkEffect.Type.BALL).withColor(Color.ORANGE).withFade(Color.ORANGE)
//                .build());
//        fireworkMeta.addEffect(FireworkEffect.builder()
//                .with(FireworkEffect.Type.BALL).withColor(Color.YELLOW).withFade(Color.YELLOW)
//                .build());
//        fireworkMeta.addEffect(FireworkEffect.builder()
//                .with(FireworkEffect.Type.BALL).withColor(Color.GREEN).withFade(Color.GREEN)
//                .build());
//        fireworkMeta.addEffect(FireworkEffect.builder()
//                .with(FireworkEffect.Type.BALL).withColor(Color.BLUE).withFade(Color.BLUE)
//                .build());
//        fireworkMeta.addEffect(FireworkEffect.builder()
//                .with(FireworkEffect.Type.BALL).withColor(Color.PURPLE).withFade(Color.PURPLE)
//                .build());
        firework.setFireworkMeta(fireworkMeta);
        firework.setShotAtAngle(true);
        firework.setVelocity(player.getEyeLocation().getDirection().multiply(1));
    }

}
