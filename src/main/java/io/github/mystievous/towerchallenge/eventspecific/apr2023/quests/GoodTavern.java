package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests;

import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.mystievous.towerchallenge.quests.QuestManager.STEVE_COLOR;

public class GoodTavern {

    public static final Location baseTrapdoor = new Location(Worlds.Apr2023_quest(), -5.5, 64.1, 21.5, -90, 0);

    public static final String GOOD_TRAPDOOR_INTERACT = "good-trapdoor-interact";
    public static final String CHAIR_INTERACT = "chair-interact";

    public static final Location baseChair = new Location(Worlds.Apr2023_quest(), -1.5, 65, 12.5);
    public static final Location baseChairSparkle = new Location(Worlds.Apr2023_quest(), -2, 62, 12);
    public static final Location baseChairMove = new Location(Worlds.Apr2023_quest(), -0.5, 65, 12.5);
    public static final Location baseDoor = new Location(Worlds.Apr2023_quest(), -2.4, 65, 12.5);
    public static final Location baseDoorBlock = new Location(Worlds.Apr2023_quest(), -3, 65, 12);
    public static final Location baseSteve = new Location(Worlds.Apr2023_quest(), -4.5, 65, 12.5);

    public static final Location[] baseClickDoor = new Location[]{new Location(Worlds.Apr2023_quest(), -3, 65, 12), new Location(Worlds.Apr2023_quest(), -3, 66, 12)};

    public static final String ROOM_TAG = "goodtavern";

    private final Apr2023QuestInstance instance;

    private final Stairs stairs;
    private Interaction doorBlock;
    private Interaction chairMove;
    private Skeleton steve;
    private final String teamRoomTag;
    private final Location instanceChair;
    private final Location instanceDoor;
    private final Location instanceSteve;
    private boolean entered;
    private final List<Location> instanceDoorClicks;

    public GoodTavern(Apr2023QuestInstance instance) {

        this.instance = instance;

        teamRoomTag = TeamUtils.toTeamTag(instance.getTeam(), ROOM_TAG);

        instanceChair = instance.offsetLocation(baseChair);
        instanceDoor = instance.offsetLocation(baseDoor);
        instanceSteve = instance.offsetLocation(baseSteve);

        stairs = (Stairs) Bukkit.createBlockData(Material.MANGROVE_STAIRS);
        stairs.setFacing(BlockFace.NORTH);
        stairs.setHalf(Bisected.Half.BOTTOM);
        stairs.setShape(Stairs.Shape.STRAIGHT);
        stairs.setWaterlogged(false);

        entered = false;

        instanceDoorClicks = Arrays.stream(baseClickDoor).map(instance::offsetLocation).collect(Collectors.toList());

        resetEntities();

    }

    public void setEntities() {
        Location instanceTrapdoor = instance.offsetLocation(baseTrapdoor);
        Interaction trapdoorInteraction = (Interaction) instanceTrapdoor.getWorld().spawnEntity(instanceTrapdoor, EntityType.INTERACTION);
        trapdoorInteraction.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        trapdoorInteraction.addScoreboardTag(GOOD_TRAPDOOR_INTERACT);
        trapdoorInteraction.addScoreboardTag(teamRoomTag);
        trapdoorInteraction.setInteractionWidth(1f);
        trapdoorInteraction.setInteractionHeight(1f);
        trapdoorInteraction.setResponsive(true);


        chairMove = (Interaction) instanceChair.getWorld().spawnEntity(instanceChair, EntityType.INTERACTION);
        chairMove.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        chairMove.addScoreboardTag(CHAIR_INTERACT);
        chairMove.addScoreboardTag(teamRoomTag);
        chairMove.setResponsive(true);
        chairMove.setInteractionHeight(1.1f);
        chairMove.setInteractionWidth(1.1f);


        doorBlock = (Interaction) instanceDoor.getWorld().spawnEntity(instanceDoor, EntityType.INTERACTION);
        doorBlock.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        doorBlock.addScoreboardTag(teamRoomTag);
        doorBlock.setInteractionHeight(2f);
        doorBlock.setInteractionWidth(1f);


        steve = (Skeleton) instanceSteve.getWorld().spawnEntity(instanceSteve, EntityType.SKELETON, false);
        steve.addScoreboardTag(QuestManager.STEVE);
        steve.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        steve.addScoreboardTag(teamRoomTag);
        ItemStack goggles = GuiUtil.formatItem("Potion Goggles", Material.LEATHER_HORSE_ARMOR, 30);
        LeatherArmorMeta meta = (LeatherArmorMeta) goggles.getItemMeta();
        meta.setColor(STEVE_COLOR.toBukkitColor());
        goggles.setItemMeta(meta);
        steve.getEquipment().setHelmet(goggles);
        steve.setPersistent(true);
        steve.setInvulnerable(true);
        steve.customName(Component.text("steve skellington"));

        setChair();
    }

    public List<Location> getInstanceDoorClicks() {
        return instanceDoorClicks;
    }

    public void init() {
        if (!entered) {
            entered = true;
            resetEntities();
        }
    }

    public void resetEntities() {
        Chunk chair = instanceChair.getChunk();
        Chunk door = instanceDoor.getChunk();
        Chunk steve = instanceSteve.getChunk();

        if ((chair.isEntitiesLoaded() || chair.load()) && (door.isEntitiesLoaded() || door.load()) && (steve.isEntitiesLoaded() || steve.load())) {
            List<Entity> entities = Bukkit.selectEntities(Apr2023QuestManager.sender, String.format("@e[tag=%s]", teamRoomTag));
            for (Entity entity : entities) {
                entity.remove();
            }
            setEntities();

            chair.unload(true);
            door.unload(true);
            steve.unload(true);

        }

    }

    public void removeSteve() {
        Chunk steveChunk = instanceSteve.getChunk();
        if (steveChunk.isEntitiesLoaded() || steveChunk.load()) {
            steve.remove();
        }
        steveChunk.unload(true);
    }

    public void moveChair() {
        doorBlock.remove();
        chairMove.remove();

        Block redstone = instance.offsetLocation(baseChairSparkle).getBlock();
        redstone.setType(Material.AIR);

        Block stair = instance.offsetLocation(baseChair).getBlock();
        stair.setType(Material.AIR);

        Block air = instance.offsetLocation(baseChairMove).getBlock();
        air.setBlockData(stairs);
    }

    public void setChair() {
        Block redstone = instance.offsetLocation(baseChairSparkle).getBlock();
        redstone.setType(Material.AIR);
        redstone.setType(Material.REDSTONE_BLOCK);

        Block stair = instance.offsetLocation(baseChair).getBlock();
        stair.setBlockData(stairs);

        Block air = instance.offsetLocation(baseChairMove).getBlock();
        air.setType(Material.AIR);

        Block block = instance.offsetLocation(baseDoorBlock).getBlock();
        Door door = (Door) block.getBlockData();
        door.setOpen(false);
        block.setBlockData(door);

    }

}
