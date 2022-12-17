//package io.github.idkahn.towerchallenge.misc.entitybottles;
//
//import io.github.idkahn.towerchallenge.NBTUtils;
//import io.github.idkahn.towerchallenge.TextUtil;
//import io.github.idkahn.towerchallenge.TowerChallenge;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.format.TextDecoration;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.World;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.entity.CreatureSpawnEvent;
//import org.bukkit.event.player.PlayerInteractEntityEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class BottleManager implements Listener {
//
//    private Map<UUID, BottleEntity> entities;
//
//    public static final String FILLED_BOTTLE_TAG = "filled_bottle";
//    public static final String BOTTLE_ID = "bottle_id";
//
//    public static ItemStack getEmpty() {
//        ItemStack bottle = NBTUtils.setUniqueID(BOTTLE_ID, new ItemStack(Material.GLASS_BOTTLE), UUID.randomUUID());
//        ItemMeta meta = bottle.getItemMeta();
//        meta.displayName(Component.text("Entity Bottle").decoration(TextDecoration.ITALIC, false));
//        meta.lore(TextUtil.formatText("Click on an entity", "to bottle it!"));
//        bottle.setItemMeta(meta);
//        return bottle;
//    }
//
//    public BottleManager() {
//        entities = new HashMap<>();
//        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
//    }
//
//    @EventHandler
//    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
//        if (event.isCancelled())
//            return;
//
//        Player player = event.getPlayer();
//        ItemStack item = player.getInventory().getItem(event.getHand());
//        Entity entity = event.getRightClicked();
//
//        UUID bottleId = NBTUtils.getUniqueID(BOTTLE_ID, item);
//
//        if (bottleId != null) {
//            if (!NBTUtils.boolState(FILLED_BOTTLE_TAG, item)) {
//
//                player.sendMessage(Component.text("Hi; "+entity.getName()));
//                BottleEntity bottleEntity = entities.getOrDefault(entity.getUniqueId(), new BottleEntity(bottleId, entity));
//                if (bottleEntity.saveData()) {
//                    entity.remove();
//                    NBTUtils.setBool(FILLED_BOTTLE_TAG, item, true);
//                }
//
//                event.setCancelled(true);
//            }
//        }
//    }
//
//    @EventHandler
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        Player player = event.getPlayer();
//        ItemStack item = event.getItem();
//        if (event.getAction() == Action.PHYSICAL
//                || event.getAction() == Action.LEFT_CLICK_AIR
//                || event.getAction() == Action.LEFT_CLICK_BLOCK
//                || event.getAction() == Action.RIGHT_CLICK_AIR
//                || event.getClickedBlock() == null
//                || item == null)
//            return;
//
//        UUID bottleId = NBTUtils.getUniqueID(BOTTLE_ID, item);
//
//        if (bottleId != null) {
//            BottleEntity bottleEntity = BottleEntity.loadData(bottleId);
//            if (bottleEntity != null) {
//                Location location = event.getClickedBlock().getLocation().add(event.getBlockFace().getDirection());
//                World world = player.getWorld();
//                bottleEntity.getBukkitEntity(world, location);
//                NBTUtils.setBool(FILLED_BOTTLE_TAG, item, false);
//            }
//        }
//    }
//
//}
