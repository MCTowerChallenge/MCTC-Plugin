package io.github.mystievous.towerchallenge.quests.entities;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Consumer;

public class ItemEntityHandler implements Listener {

    private final TowerChallenge plugin;
    private final TeamManager teamManager;
    private final String tag;
    private final ItemStack itemStack;
    private final double yOffset;
    private Key soundKey;
    private final String requiredQuest;
    private Consumer<Player> eventHandler;

    public ItemEntityHandler(TowerChallenge plugin, TeamManager teamManager, String tag, @Nullable String requiredQuest, ItemStack itemStack) {
        this.plugin = plugin;
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

    protected TeamManager getTeamManager() {
        return teamManager;
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
        return TeamUtils.setTeam(plugin, itemStack, team);
    }

    public String getTag() {
        return tag;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled())
            return;
        CraftingInventory inventory = event.getInventory();
        for (ItemStack item : inventory.getMatrix()) {
            if (NBTUtils.boolState(plugin, tag, item)) {
                event.getWhoClicked().sendMessage(Component.text("You can't craft with that!"));
                event.setCancelled(true);
                return;
            }
        }
    }

    public boolean hasCollected(TowerTeam team, String check) throws SQLException {
        return teamManager.getDatabase().isItemCollected(team, tag, check);
    }

    public void setItemCollected(TowerTeam team, String check) throws SQLException {
        teamManager.getDatabase().setItemCollected(team, tag, check, true);
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof LivingEntity livingEntity && livingEntity.getScoreboardTags().contains(tag)) {
            Bukkit.getScheduler().runTaskAsynchronously(teamManager.getPlugin(), () -> {
                TowerTeam team = teamManager.getPlayerTeam(player);
                if (team == null) {
                    player.sendMessage(CommandUtils.errorMessage("You are not on a team!"));
                    return;
                }
                if (requiredQuest != null) {
                    if (team.getCurrentQuestId().equals(requiredQuest)) {
                        player.sendActionBar(CommandUtils.errorMessage("You do not have the quest for this item!"));
                        return;
                    }
                }
                try {
                    if (hasCollected(team, livingEntity.getUniqueId().toString())) {
                        player.spawnParticle(Particle.CRIT, livingEntity.getEyeLocation(), 10, 0.225, 0.225, 0.225, 0);
                        player.playSound(net.kyori.adventure.sound.Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.snare"), Sound.Source.MASTER, 100, 0));
                        player.sendActionBar(CommandUtils.errorMessage("You've already collected that item!"));
                        return;
                    }
                    HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(getItem(team));
                    if (!leftoverItems.isEmpty()) {
                        player.sendMessage(CommandUtils.errorMessage("You do not have enough inventory space to pick that up."));
                    } else {
                        if (eventHandler != null) {
                            eventHandler.accept(player);
                        }
                        player.spawnParticle(Particle.COMPOSTER, livingEntity.getEyeLocation(), 10, 0.225, 0.225, 0.225);
                        player.playSound(Sound.sound(soundKey, Sound.Source.MASTER, 100, 1));
                        setItemCollected(team, livingEntity.getUniqueId().toString());
                    }
                } catch (SQLException e) {
                    player.spawnParticle(Particle.CRIT, livingEntity.getEyeLocation(), 10, 0.225, 0.225, 0.225, 0);
                    player.playSound(net.kyori.adventure.sound.Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.snare"), Sound.Source.MASTER, 100, 0));
                    player.sendActionBar(CommandUtils.errorMessage("There was an error using the database!"));
                    Bukkit.getLogger().warning("Error reading database: " + e.getMessage());
                }
            });
        }
    }

}
