package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.ValentinesUtil;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.maze.Maze;
import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.gallery.ShootingGallery;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.ocean.MermaidsGrove;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;

public class EvilTower implements Listener {

    private static final Location baseEnterLocation = new Location(Worlds.eviltowers(), 1.5, 99, -8.5, -90, 0);
    private static final Location baseTopEnterLocation = new Location(Worlds.eviltowers(), 20, 99, -8.5, 90, 0);

    public static final Location baseGalleryButtonLocation = new Location(Worlds.eviltowers(), 4, 101, -13);
    public static final Location[] baseGalleryGateCorners = {new Location(Worlds.eviltowers(), 5, 99, -15), new Location(Worlds.eviltowers(), 6, 103, -15)};
    public static final Location baseGalleryLockLocation = new Location(Worlds.eviltowers(), 19, 100, -10);
    public static final Location baseGalleryArmorStandLocation = new Location(Worlds.eviltowers(), 19.97, 98.63, -9.50);

    public static final Location baseMazeButtonLocation = new Location(Worlds.eviltowers(), 9, 101, -13);
    public static final Location[] baseMazeGateCorners = {new Location(Worlds.eviltowers(), 10, 99, -15), new Location(Worlds.eviltowers(), 11, 103, -15)};
    public static final Location baseMazeLockLocation = new Location(Worlds.eviltowers(), 19, 100, -9);
    public static final Location baseMazeArmorStandLocation = new Location(Worlds.eviltowers(), 19.97, 98.63, -8.50);

    public static final Location baseOceanButtonLocation = new Location(Worlds.eviltowers(), 14, 101, -13);
    public static final Location[] baseOceanGateCorners = {new Location(Worlds.eviltowers(), 15, 99, -15), new Location(Worlds.eviltowers(), 16, 103, -15)};
    public static final Location baseOceanLockLocation = new Location(Worlds.eviltowers(), 19, 100, -8);
    public static final Location baseOceanArmorStandLocation = new Location(Worlds.eviltowers(), 19.97, 98.63, -7.50);

    public static final Location[] baseFinalGateCorners = {new Location(Worlds.eviltowers(), 20, 99, -10), new Location(Worlds.eviltowers(), 20, 102, -8)};

    private final Vector offset;
    private final Location enterLocation;
    private final Location topEnterLocation;
    private final int teamId;
    private final TowerChallenge plugin;
    private final TeamManager teamManager;
    private final QuestManager questManager;

    private final Dialogue pickGallery;
    private final Dialogue pickMaze;
    private final Dialogue pickOcean;

    public EvilTower(TowerChallenge plugin, QuestManager questManager, TeamManager teamManager, Vector offset, int teamId) {
        this.offset = offset;
        enterLocation = offsetLocation(baseEnterLocation);
        topEnterLocation = offsetLocation(baseTopEnterLocation);
        this.teamId = teamId;
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.questManager = questManager;

        new TowerPortalManager(this, teamManager, teamId);
        new ShootingGallery(plugin, teamManager, questManager, this, teamId);
        new MermaidsGrove(plugin, questManager, this, teamManager, teamId);
        new Maze(plugin, teamManager, questManager, teamId);

        NPC spirit = questManager.getSpirit();

        pickGallery = new Dialogue(teamManager, spirit.formatMessage("75 points in 20 seconds, there I set the bar."), 5.5d);
        pickGallery.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.set_the_bar"));
        {
            Dialogue askYourself = new Dialogue(teamManager, spirit.formatMessage("But you should ask yourself, are your skills up to par?"), 4.5);
            askYourself.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.ask_yourself"));
            pickGallery.setNext(askYourself);
        }

        pickMaze = new Dialogue(teamManager, spirit.formatMessage("Welcome to my Archive of Histories, where one can discover mysteries untold."), 6.0d);
        pickMaze.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.archive_of_histories"));
        {
            Dialogue victimToTheCold = new Dialogue(teamManager, spirit.formatMessage("If you can find your way around, and not fall victim to the cold."), 5.5);
            victimToTheCold.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.victim_to_the_cold"));
            pickMaze.setNext(victimToTheCold);

            Dialogue dontTouchAnything = new Dialogue(teamManager, spirit.formatMessage("And don't touch anything!"), 2.0d);
            dontTouchAnything.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.dont_touch_anything"));
            victimToTheCold.setNext(dontTouchAnything);
        }

        pickOcean = new Dialogue(teamManager, spirit.formatMessage("A mermaid has stolen my heart... Alas in this reef it lies, all torn apart."), 9.0d);
        pickOcean.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.mermaid_stole_heart"));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Location getEnterLocation() {
        return enterLocation;
    }

    public Location getTopEnterLocation() {
        return topEnterLocation;
    }

    public Location offsetLocation(Location baseLocation) {
        return baseLocation.clone().add(offset);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        EquipmentSlot hand = event.getHand();
        if (hand != null && !hand.equals(EquipmentSlot.HAND))
            return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        Block block = event.getClickedBlock();
        if (block == null)
            return;
        TowerTeam team = teamManager.getTeam(teamId);
        if (team == null)
            return;

        if (!team.getCurrentQuestId().equals(QuestManager.PICK_TOWER_ROOM))
            return;

        if (block.getLocation().equals(offsetLocation(baseGalleryButtonLocation))) {
            if (team.getObjective(QuestManager.PICK_TOWER_ROOM, QuestManager.SHOOTING_GALLERY) == 0) {
                team.addObjectiveScore(QuestManager.PICK_TOWER_ROOM, QuestManager.SHOOTING_GALLERY, 1);
                questManager.setTeamQuest(team, QuestManager.SHOOTING_GALLERY);
                team.setInDialogue(true);
                pickGallery.play(team, () -> {
                    clearArea(baseGalleryGateCorners);
                    team.setInDialogue(false);
                });
            }
        }
        if (block.getLocation().equals(offsetLocation(baseMazeButtonLocation))) {
            if (team.getObjective(QuestManager.PICK_TOWER_ROOM, QuestManager.LIBRARY_MAZE) == 0) {
                team.addObjectiveScore(QuestManager.PICK_TOWER_ROOM, QuestManager.LIBRARY_MAZE, 1);
                questManager.setTeamQuest(team, QuestManager.LIBRARY_MAZE);
                team.setInDialogue(true);
                pickMaze.play(team, () -> {
                    clearArea(baseMazeGateCorners);
                    team.setInDialogue(false);
                });
            }
        }
        if (block.getLocation().equals(offsetLocation(baseOceanButtonLocation))) {
            if (team.getObjective(QuestManager.PICK_TOWER_ROOM, QuestManager.OCEAN_SEARCH) == 0) {
                team.addObjectiveScore(QuestManager.PICK_TOWER_ROOM, QuestManager.OCEAN_SEARCH, 1);
                questManager.setTeamQuest(team, QuestManager.OCEAN_SEARCH);
                team.setInDialogue(true);
                pickOcean.play(team, () -> {
                    clearArea(baseOceanGateCorners);
                    team.setInDialogue(false);
                });
            }
        }
    }

    public static final String lockTag = "tower_lock";

    private static void summonArmorStand(Location location, ItemStack itemStack) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND, false);
        armorStand.getEquipment().setHelmet(itemStack);
        armorStand.setDisabledSlots(EquipmentSlot.values());
        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
//        armorStand.setRotation(90, 0);
        armorStand.addScoreboardTag(lockTag);
    }

    public static final String lockObjective = "lock";

    @EventHandler
    private void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        if (event.isCancelled())
            return;
        EquipmentSlot hand = event.getHand();
        if (!hand.equals(EquipmentSlot.HAND))
            return;

        Entity entity = event.getRightClicked();
        Location entityLocation = entity.getLocation().toBlockLocation();
        Location galleryLock = offsetLocation(baseGalleryLockLocation);
        if (entityLocation.getBlockX() == galleryLock.getBlockX() &&
        entityLocation.getBlockY() == galleryLock.getBlockY() &&
        entityLocation.getBlockZ() == galleryLock.getBlockZ()) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItem(hand);
            TowerTeam team = teamManager.getTeam(teamId);
            if (team != null && NBTUtils.boolState(plugin, ValentinesUtil.GALLERY_TAG, item)) {
                player.getInventory().setItem(hand, item.subtract(1));
                summonArmorStand(offsetLocation(baseGalleryArmorStandLocation), ValentinesUtil.galleryKey);
                entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 1f, 1f);
                team.addObjectiveScore(QuestManager.SHOOTING_GALLERY, lockObjective, 1);
                checkFinal(team);
            }
        }
        Location mazeLock = offsetLocation(baseMazeLockLocation);
        if (entityLocation.getBlockX() == mazeLock.getBlockX() &&
                entityLocation.getBlockY() == mazeLock.getBlockY() &&
                entityLocation.getBlockZ() == mazeLock.getBlockZ()) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItem(hand);
            TowerTeam team = teamManager.getTeam(teamId);
            if (team != null && NBTUtils.boolState(plugin, ValentinesUtil.MAZE_TAG, item)) {
                player.getInventory().setItem(hand, item.subtract(1));
                summonArmorStand(offsetLocation(baseMazeArmorStandLocation), ValentinesUtil.mazeKey);
                entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 1f, 1f);
                team.addObjectiveScore(QuestManager.LIBRARY_MAZE, lockObjective, 1);
                checkFinal(team);
            }
        }
        Location oceanLock = offsetLocation(baseOceanLockLocation);
        if (entityLocation.getBlockX() == oceanLock.getBlockX() &&
                entityLocation.getBlockY() == oceanLock.getBlockY() &&
                entityLocation.getBlockZ() == oceanLock.getBlockZ()) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItem(hand);
            TowerTeam team = teamManager.getTeam(teamId);
            if (team != null && NBTUtils.boolState(plugin, ValentinesUtil.OCEAN_TAG, item)) {
                player.getInventory().setItem(hand, item.subtract(1));
                summonArmorStand(offsetLocation(baseOceanArmorStandLocation), ValentinesUtil.oceanKey);
                entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_FRAME_ADD_ITEM, 1f, 1f);
                team.addObjectiveScore(QuestManager.OCEAN_SEARCH, lockObjective, 1);
                checkFinal(team);
            }
        }

    }

    private void checkFinal(TowerTeam team) {
        if (team.getObjective(QuestManager.SHOOTING_GALLERY, lockObjective) == 0)
            return;
        if (team.getObjective(QuestManager.LIBRARY_MAZE, lockObjective) == 0)
            return;
        if (team.getObjective(QuestManager.OCEAN_SEARCH, lockObjective) == 0)
            return;

        clearArea(baseFinalGateCorners);
        Collection<Entity> entities = offsetLocation(baseMazeArmorStandLocation).getNearbyEntities(3, 3, 3);
        for (Entity entity : entities) {
            if (entity.getScoreboardTags().contains(lockTag)) {
                entity.remove();
            }
        }

        if (questManager.getTeamQuest(team).equals(QuestManager.PICK_TOWER_ROOM)) {
            questManager.setTeamQuest(team, QuestManager.TALK_TO_STEVE);
        }
    }

    private void clearArea(Location[] baseArea) {
        Location[] offsetArea = Arrays.stream(baseArea).map(this::offsetLocation).toArray(Location[]::new);
        for (int x = offsetArea[0].getBlockX(); x <= offsetArea[1].getBlockX(); x++) {
            for (int y = offsetArea[0].getBlockY(); y <= offsetArea[1].getBlockY(); y++) {
                for (int z = offsetArea[0].getBlockZ(); z <= offsetArea[1].getBlockZ(); z++) {
                    Location location = new Location(offsetArea[0].getWorld(), x, y, z);
                    Block block = location.getBlock();
                    block.setType(Material.AIR);
                }
            }
        }
        Location center = offsetArea[0].clone().add(offsetArea[1]).multiply(0.5);
        center.getWorld().playSound(center, Sound.BLOCK_ANVIL_LAND, SoundCategory.RECORDS, 1f, 1f);
    }

}
