package io.github.idkahn.towerchallenge.quests;

import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.misc.CommandUtils;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ItemEntityHandler implements Listener {

    public static final String CONFIG_LABEL = "CollectedItems";

    private final ChallengeManager challengeManager;
    private final String tag;
    private ItemStack itemStack;
    private double yOffset;
    private Key soundKey;
    private Quest requiredQuest;

    public ItemEntityHandler(ChallengeManager challengeManager, String tag, ItemStack itemStack) {
        this.challengeManager = challengeManager;
        this.tag = tag;
        this.itemStack = itemStack;
        this.yOffset = -1.5;
        this.soundKey = Key.key(Key.MINECRAFT_NAMESPACE, "block.amethyst_cluster.place");
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
    }

    public ItemEntityHandler(ChallengeManager challengeManager, String tag, ItemStack itemStack, double yOffset) {
        this(challengeManager, tag, itemStack);
        this.yOffset = yOffset;
    }

    public String getTag() {
        return tag;
    }

    public void setSoundKey(Key soundKey) {
        this.soundKey = soundKey;
    }

    public ArmorStand summonArmorStand(Location location) {
        World world = location.getWorld();
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND, false);
        armorStand.setItem(EquipmentSlot.HEAD, itemStack);
        armorStand.addScoreboardTag(tag);
        armorStand.setGravity(false);
        armorStand.setInvisible(true);
        armorStand.addDisabledSlots(EquipmentSlot.values());
        armorStand.setInvulnerable(true);
        return armorStand;
    }

    public ArmorStand summonArmorStand(Player player) {
        Location location = player.getLocation().add(0, yOffset, 0);
        return summonArmorStand(location);
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        TowerTeam team = challengeManager.getPlayerTeam(player);
        if (team == null) {
            player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
            return;
        }

        if (entity instanceof LivingEntity livingEntity && livingEntity.getScoreboardTags().contains(tag)) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamDataConfigFile);
            String tagPath = team.getTextName()+"."+CONFIG_LABEL+"."+tag;
            if (config.isList(tagPath)) {
                List<String> tagConfig = config.getStringList(tagPath);
                if (tagConfig.contains(livingEntity.getUniqueId().toString())) {
                    player.spawnParticle(Particle.CRIT, livingEntity.getEyeLocation(), 10, 0.225, 0.225, 0.225, 0);
                    player.playSound(net.kyori.adventure.sound.Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.snare"), Sound.Source.MASTER, 100, 0));
                    player.sendActionBar(CommandUtils.errorMessage("You've already collected that item!"));
                    return;
                }
            }
            HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(itemStack);
            if (!leftoverItems.isEmpty()) {
                player.sendMessage(CommandUtils.errorMessage("You do not have enough inventory space to pick that up."));
            } else {
                player.spawnParticle(Particle.COMPOSTER, livingEntity.getEyeLocation(), 10, 0.225, 0.225, 0.225);
                player.playSound(Sound.sound(soundKey, Sound.Source.MASTER, 100, 1));
                List<String> tagConfig = config.getStringList(tagPath);
                tagConfig.add(livingEntity.getUniqueId().toString());
                config.set(tagPath, tagConfig);
                try {
                    config.save(TowerChallenge.teamDataConfigFile);
                } catch (IOException e) {
                    Bukkit.getLogger().info("Failed to save Candy Config");
                }
            }
//            ConfigurationSection entitySection = config.getConfigurationSection(tag+"."+entity.getUniqueId());
//            if (entitySection.contains(challengeManager.getPlayerTeam(player).getTextName()));
        }
    }

}
