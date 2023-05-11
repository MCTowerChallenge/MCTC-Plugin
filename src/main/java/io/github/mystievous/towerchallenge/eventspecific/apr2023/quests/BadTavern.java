package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests;

import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class BadTavern {

    public static final String ENTER_DOOR = "tavern-enter-door";
    public static final String EXIT_DOOR = "tavern-exit-door";

    public static final Location baseEnterTeleport = new Location(Worlds.Apr2023_quest(), 3, 65, 32);

    public static final Location[] baseLeavePortalBounds = new Location[]{new Location(Worlds.Apr2023_quest(), 2, 65,31), new Location(Worlds.Apr2023_quest(), 3, 67, 31)};
    public static final Location leaveTeleport = new Location(Worlds.Apr2023(), -402, 63, -2541.5, 90, 0);


    public static final String CRATE_INTERACT = "crate-interact";
    public static final Location baseCrate = new Location(Worlds.Apr2023_quest(), -5.5, 65.5, 45.5);
    public static final Location baseCrateSparkle = new Location(Worlds.Apr2023_quest(), -7, 62, 45);
    public static final Location baseCrateMoved = new Location(Worlds.Apr2023_quest(), -5.5, 65.5, 46.5);

    public static final String TRAPDOOR_INTERACT = "trapdoor-interact";
    public static final Location baseTrapdoor = new Location(Worlds.Apr2023_quest(), -5.5, 64.1, 45.5);
    public static final Location baseTrapdoorSparkle = new Location(Worlds.Apr2023_quest(), -8, 62, 45);

    public static final String ROOM_TAG = "badtavern";

    private final Apr2023QuestInstance instance;

    private ItemDisplay crate;
    private Interaction crateInteraction;

    private boolean entered;
    private final String teamRoomTag;

    private final Location instanceCrate;
    private final Location instanceCrateMoved;

    public BadTavern(Apr2023QuestInstance instance) {
        this.instance = instance;

        instanceCrate = instance.offsetLocation(baseCrate);
        instanceCrateMoved = instance.offsetLocation(baseCrateMoved);

        instance.initPortal(EXIT_DOOR, baseLeavePortalBounds, leaveTeleport);

        instance.offsetLocation(baseCrateSparkle).getBlock().setType(Material.AIR);
        instance.offsetLocation(baseCrateSparkle).getBlock().setType(Material.REDSTONE_BLOCK);
        instance.offsetLocation(baseTrapdoorSparkle).getBlock().setType(Material.AIR);

        entered = false;
        teamRoomTag = TeamUtils.toTeamTag(instance.getTeam(), ROOM_TAG);

        resetEntities();

    }

    public void setEntities() {
        crate = (ItemDisplay) instanceCrate.getWorld().spawnEntity(instanceCrate, EntityType.ITEM_DISPLAY);
        crate.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        crate.addScoreboardTag(teamRoomTag);
        crate.setItemStack(GuiUtil.formatItem("Crate", Material.BARREL, 1));
        crate.setTransformation(new Transformation(
                new Vector3f(0.0f, -0.1f, 0.0f),
                new Quaternionf(0.0f, -0.2587648f, 0.0f, 0.96594703f),
                new Vector3f(0.81f, 0.81f, 0.81f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f)
        ));

        crateInteraction = (Interaction) instanceCrate.getWorld().spawnEntity(instanceCrate.clone().add(0, -0.5, 0), EntityType.INTERACTION);
        crateInteraction.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        crateInteraction.addScoreboardTag(CRATE_INTERACT);
        crateInteraction.addScoreboardTag(teamRoomTag);
        crateInteraction.setInteractionHeight(1f);
        crateInteraction.setInteractionWidth(1.1f);
        crateInteraction.setResponsive(true);


        Location instanceTrapdoor = instance.offsetLocation(baseTrapdoor);
        Interaction trapdoorInteraction = (Interaction) instanceTrapdoor.getWorld().spawnEntity(instanceTrapdoor, EntityType.INTERACTION);
        trapdoorInteraction.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        trapdoorInteraction.addScoreboardTag(TRAPDOOR_INTERACT);
        trapdoorInteraction.addScoreboardTag(teamRoomTag);
        trapdoorInteraction.setInteractionWidth(1f);
        trapdoorInteraction.setInteractionHeight(1f);
        trapdoorInteraction.setResponsive(true);
    }

    public void init() {
        if (!entered) {
            entered = true;
            resetEntities();
            instance.goodTavern.init();
        }
    }

    public void resetEntities() {
        Chunk crateChunk = instanceCrate.getChunk();
        Chunk crateMoveChunk = instanceCrate.getChunk();
        if ((crateChunk.isEntitiesLoaded() || crateChunk.load()) && (crateMoveChunk.isEntitiesLoaded() || crateMoveChunk.load())) {
            List<Entity> entities = Bukkit.selectEntities(Apr2023QuestManager.sender, String.format("@e[tag=%s]", teamRoomTag));
            for (Entity entity : entities) {
                entity.remove();
            }
            setEntities();

            crateChunk.unload(true);
            crateMoveChunk.unload(true);
        }
    }

    public void teleport(Player player) {
        player.teleport(instance.offsetLocation(baseEnterTeleport), PlayerTeleportEvent.TeleportCause.PLUGIN);
        init();
    }

    public void moveCrate() {
        if (crateInteraction != null) {
            crateInteraction.remove();
        }
        if (crate != null) {
            crate.teleport(instanceCrateMoved);
        }
        instance.offsetLocation(baseCrateSparkle).getBlock().setType(Material.AIR);
        instance.offsetLocation(baseTrapdoorSparkle).getBlock().setType(Material.REDSTONE_BLOCK);
    }

}