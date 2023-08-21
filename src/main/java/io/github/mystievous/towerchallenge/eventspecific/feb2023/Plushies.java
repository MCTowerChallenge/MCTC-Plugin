package io.github.mystievous.towerchallenge.eventspecific.feb2023;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.towerchallenge.Database;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.god.GodTeam;
import io.github.mystievous.towerchallenge.interaction.InteractableTaggedEntity;
import io.github.mystievous.towerchallenge.interaction.InteractableTagManager;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
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
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;

public class Plushies extends InteractableTaggedEntity {

    public static final String TAG = "plushy-pile";
    public static final String ITEM_TAG = "plushy";
    private static final int[] PLUSHY_IDS = new int[]{31, 32, 33};

    private static final Random RANDOM = new Random();

    private final Database database;
    private final Set<UUID> players;

    private final TowerChallenge plugin;

    public Plushies(TowerChallenge plugin, TeamManager teamManager, Database database) {
        super(TAG);
        this.plugin = plugin;
        this.database = database;
        this.players = new HashSet<>();
        setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Player player = playerInteractEntityEvent.getPlayer();
            Entity entity = playerInteractEntityEvent.getRightClicked();
            try {
                if (teamManager.getPlayerTeam(player) instanceof GodTeam || !players.contains(player.getUniqueId())) {
                    player.getInventory().addItem(randomPlushy(null));
                    players.add(player.getUniqueId());
                    player.playSound(entity.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1f, 1.2f);
                } else {
                    EquipmentSlot hand = playerInteractEntityEvent.getHand();
                    ItemStack currentItem = player.getInventory().getItem(hand);
                    if (NBTUtils.boolState(plugin, ITEM_TAG, currentItem)) {
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
        CommandSender sender = Bukkit.createCommandSender(component -> {
        });
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[TAG=%s]", TAG));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, livingEntity.getEyeLocation(), 1, 0.225, 0.225, 0.225);
                }
            }
        }, 0, 20);
        InteractableTagManager.registerTag(this);
    }

    public ItemStack randomPlushy(@Nullable Integer exclude) throws SQLException {
        List<Integer> randoms = new ArrayList<>();
        for (int id : PLUSHY_IDS) {
            if (exclude == null || id != exclude) {
                randoms.add(id);
            }
        }
        ItemStack item = database.getModel(randoms.get(RANDOM.nextInt(randoms.size())), true, false).getItem();
        NBTUtils.setUniqueID(plugin, item, null);
        NBTUtils.setBool(plugin, ITEM_TAG, item);
        return item;
    }

}
