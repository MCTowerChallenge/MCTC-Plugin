package io.github.mystievous.towerchallenge.eventspecific.valentines;

import io.github.mystievous.towerchallenge.Database;
import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.Palette;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.*;

public class Plushies extends NPC {

    public static final String TAG = "plushy-pile";
    public static final String ITEM_TAG = "plushy";
    private static int[] PLUSHY_IDS = new int[]{31, 32, 33};

    private static SecureRandom RANDOM = new SecureRandom();

    private Database database;
    private Set<UUID> players;

    public Plushies(TowerChallenge plugin, TeamManager teamManager, Database database) {
        super(teamManager, TAG);
        this.database = database;
        this.players = new HashSet<>();
        setDefaultHandler(playerInteractAtEntityEvent -> {
            Player player = playerInteractAtEntityEvent.getPlayer();
            Entity entity = playerInteractAtEntityEvent.getRightClicked();
            try {
                if (teamManager.getPlayerTeam(player) instanceof GodTeam || !players.contains(player.getUniqueId())) {
                    player.getInventory().addItem(randomPlushy(null));
                    players.add(player.getUniqueId());
                    player.playSound(entity.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1f, 1.2f);
                } else {
                    EquipmentSlot hand = playerInteractAtEntityEvent.getHand();
                    ItemStack currentItem = player.getInventory().getItem(hand);
                    if (NBTUtils.boolState(ITEM_TAG, currentItem)) {
                        player.getInventory().setItem(hand, randomPlushy(database.getModelId(currentItem)));
                        player.playSound(entity.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1f, 1.2f);
                    } else {
                        player.sendMessage(TextUtil.formatText("Please turn in your current plushy to get a new one!"));
                        player.sendMessage(Component.text("If you believe this message was sent in error, please").decoration(TextDecoration.ITALIC, true).color(Palette.SECONDARY.toTextColor()));
                        player.sendMessage(Component.text("contact a god for assistance!").decoration(TextDecoration.ITALIC, true).color(Palette.SECONDARY.toTextColor()));
                    }
                }
            } catch (SQLException e) {
                player.sendMessage(CommandUtils.errorMessage("Could not get plushy, please contact a God for assistance"));
                Bukkit.getLogger().warning("Error getting Plushy model " + e.getMessage());
            }
        });
        CommandSender sender = Bukkit.createCommandSender(component -> {});
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", TAG));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, livingEntity.getEyeLocation(), 1, 0.225, 0.225, 0.225);
                }
            }
        }, 0, 20);
    }

    public ItemStack randomPlushy(@Nullable Integer exclude) throws SQLException {
        List<Integer> randoms = new ArrayList<>();
        for (int id : PLUSHY_IDS) {
            if (exclude == null || id != exclude) {
                randoms.add(id);
            }
        }
        ItemStack item = database.getModel(randoms.get(RANDOM.nextInt(randoms.size())), false).getItem();
        item = NBTUtils.setUniqueID(item, null);
        item = NBTUtils.setBool(ITEM_TAG, item);
        return item;
    }

}
