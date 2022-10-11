package io.github.idkahn.towerchallenge.candy;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.commands.CommandUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Candy implements Listener {

    enum CandyType {
        LOLLIPOP1(1, "Lollipop"),
        LOLLIPOP2(2, "Lollipop"),
        CHOCOLATE1(3, "Chocolate Bar"),
        CANDYCORN(4, "Candycorn"),
        CHOCOLATE2(5, "Chocolate"),
        CANDY1(6, "Candy"),
        CANDY2(7, "Candy");

        private final int id;
        private final String name;

        CandyType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }

        private static final List<CandyType> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final SecureRandom RANDOM = new SecureRandom();

        public static CandyType randomType()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }

    }

    private static final SecureRandom RANDOM = new SecureRandom();

    public Candy(EventManager manager) {
        Bukkit.getServer().getPluginManager().registerEvents(this, manager.getPlugin());
    }

    private void acquiredCandy(Player player, ArmorStand armorStand, List<String> armorStands, YamlConfiguration config) {
        player.spawnParticle(Particle.COMPOSTER, armorStand.getEyeLocation(), 10, 0.225, 0.225, 0.225);
        player.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.amethyst_cluster.place"), Sound.Source.MASTER, 100, 1));
        player.sendActionBar(Component.text("You found a candy!"));
        armorStands.add(armorStand.getUniqueId().toString());
        config.set(player.getUniqueId().toString(), armorStands);
        try {
            config.save(TowerChallenge.candyConfigFile);
        } catch (IOException e) {
            Bukkit.getLogger().info("Failed to save Candy Config");
        }
    }

    public static ItemStack randomCandy() {
        ItemStack candy = CandyUtils.setCandy(new ItemStack(Material.SCUTE));
        ItemMeta candyMeta = candy.getItemMeta();
        CandyType type = CandyType.randomType();
        candyMeta.setCustomModelData(type.getId());
        candyMeta.displayName(Component.text(type.getName()).decoration(TextDecoration.ITALIC, false));
        candy.setItemMeta(candyMeta);
        return candy;
    }

    public static ItemStack randomPile() {
        ItemStack pile = CandyUtils.setCandy(new ItemStack(Material.APPLE));
        ItemMeta candyMeta = pile.getItemMeta();
        candyMeta.setCustomModelData(RANDOM.nextInt(5)+2);
        pile.setItemMeta(candyMeta);
        return pile;
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

//        player.sendMessage("Test");

        if (entity instanceof ArmorStand armorStand) {
            ItemStack helmet = armorStand.getItem(EquipmentSlot.HEAD);
            if (helmet.getType().equals(Material.APPLE) && helmet.getItemMeta().hasCustomModelData()) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.candyConfigFile);
                List<String> armorStands = config.getStringList(player.getUniqueId().toString());
                if (armorStands.contains(armorStand.getUniqueId().toString())) {
                    player.spawnParticle(Particle.CRIT, armorStand.getEyeLocation(), 10, 0.225, 0.225, 0.225, 0);
                    player.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.snare"), Sound.Source.MASTER, 100, 0));
                    player.sendActionBar(CommandUtils.errorMessage("You've already found that candy!"));
                    return;
                }
                ItemStack playerItem = player.getInventory().getItem(event.getHand());
                if (playerItem.getType().equals(Material.BUNDLE)) {
                    BundleMeta bundleMeta = (BundleMeta) playerItem.getItemMeta();
                    bundleMeta.addItem(randomCandy());
                    playerItem.setItemMeta(bundleMeta);
                    acquiredCandy(player, armorStand, armorStands, config);
                } else {
                    HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(randomCandy());
                    if (!leftoverItems.isEmpty()) {
                        player.sendMessage("You do not have enough inventory space to pick that up,");
                        player.sendMessage("Clear a slot, or use your bundle directly!");
                    } else {
                        acquiredCandy(player, armorStand, armorStands, config);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (item != null && item.getType().equals(Material.BUNDLE)) {
                if (event.getClickedBlock() != null && event.getClickedBlock().getType().isInteractable()) {
                    return;
                }
                event.setCancelled(true);
            }
        }

    }

}
