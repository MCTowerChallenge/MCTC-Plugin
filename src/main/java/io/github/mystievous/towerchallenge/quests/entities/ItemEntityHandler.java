package io.github.mystievous.towerchallenge.quests.entities;

import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ItemEntityHandler implements Listener {

    public static final String CONFIG_LABEL = "CollectedItems";

    private final TeamManager teamManager;
    private final String tag;
    private final ItemStack itemStack;
    private final double yOffset;
    private Key soundKey;
    private final String requiredQuest;
    private Consumer<Player> eventHandler;

    public ItemEntityHandler(TeamManager teamManager, String tag, @Nullable String requiredQuest, ItemStack itemStack) {
        this.teamManager = teamManager;
        this.tag = tag;
        this.requiredQuest = requiredQuest;
        this.itemStack = itemStack;
        this.yOffset = -1.5;
        this.soundKey = Key.key(Key.MINECRAFT_NAMESPACE, "block.amethyst_cluster.place");
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.getInstance());
    }

    public void setEventHandler(Consumer<Player> eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void setSoundKey(Key soundKey) {
        this.soundKey = soundKey;
    }

    public @NotNull ArmorStand summonArmorStand(@NotNull Location location) {
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

    public @NotNull ArmorStand summonArmorStand(@NotNull Player player) {
        Location location = player.getLocation().add(0, yOffset, 0);
        return summonArmorStand(location);
    }

    public ItemStack getItem(@NotNull TowerTeam team) {
        return NBTUtils.setTeam(itemStack, team);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled())
            return;
        CraftingInventory inventory = event.getInventory();
        for (ItemStack item : inventory.getMatrix()) {
//            event.getWhoClicked().sendMessage("Craft");
            if (NBTUtils.boolState(tag, item)) {
                event.getWhoClicked().sendMessage(Component.text("You can't craft with that!"));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        TowerTeam team = teamManager.getPlayerTeam(player);
        if (team == null) {
            player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
            return;
        }

        if (entity instanceof LivingEntity livingEntity && livingEntity.getScoreboardTags().contains(tag)) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
            String questPath = team.getTextName() + ".CurrentQuest";
            if (requiredQuest != null) {
                if (!config.isString(questPath) || !config.getString(questPath).equals(requiredQuest)) {
                    player.sendActionBar(CommandUtils.errorMessage("You do not have the quest for this item!"));
                    return;
                }
            }
            String tagPath = team.getTextName() + "." + CONFIG_LABEL + "." + tag;
            if (config.isList(tagPath)) {
                List<String> tagConfig = config.getStringList(tagPath);
                if (tagConfig.contains(livingEntity.getUniqueId().toString())) {
                    player.spawnParticle(Particle.CRIT, livingEntity.getEyeLocation(), 10, 0.225, 0.225, 0.225, 0);
                    player.playSound(net.kyori.adventure.sound.Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.snare"), Sound.Source.MASTER, 100, 0));
                    player.sendActionBar(CommandUtils.errorMessage("You've already collected that item!"));
                    return;
                }
            }
            HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(getItem(team));
            if (!leftoverItems.isEmpty()) {
                player.sendMessage(CommandUtils.errorMessage("You do not have enough inventory space to pick that up."));
            } else {
                if (eventHandler != null) {
                    eventHandler.accept(player);
                }
                config = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
                player.spawnParticle(Particle.COMPOSTER, livingEntity.getEyeLocation(), 10, 0.225, 0.225, 0.225);
                player.playSound(Sound.sound(soundKey, Sound.Source.MASTER, 100, 1));
                List<String> tagConfig = config.getStringList(tagPath);
                tagConfig.add(livingEntity.getUniqueId().toString());
                config.set(tagPath, tagConfig);
                try {
                    config.save(Config.teamDataConfigFile);
                } catch (IOException e) {
                    Bukkit.getLogger().info("Failed to save Team Data Config");
                }
            }
        }
    }

}
