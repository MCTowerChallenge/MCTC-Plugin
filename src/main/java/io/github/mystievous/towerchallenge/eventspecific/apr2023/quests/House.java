package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests;

import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class House {

    public static final String ENTER_DOOR = "house-enter-door";
    public static final String LEAVE_DOOR = "house-leave-door";

    public static final String INVITE = "house-invite";
    public static final Location baseInviteSparkleRedstone = new Location(Worlds.Apr2023_quest(), -25, 62, 15);
    public static final Location baseInviteLocation = new Location(Worlds.Apr2023_quest(), -25.5, 66.5, 15.5);
    public static final Location baseInviteInteractionLocation = new Location(Worlds.Apr2023_quest(), -25.08, 66.01, 15.71);

    public static final Location baseTeleport = new Location(Worlds.Apr2023_quest(), -33.5, 65, 10.5);
    public static final Location leaveTeleport = new Location(Worlds.Apr2023(), -747.5, 113, -2574.5);
    public static final Location baseLeaveDoor = new Location(Worlds.Apr2023_quest(), -33.50, 65.00, 8.70);

    public static final String ROOM_TAG = "house";

    private final Apr2023QuestInstance instance;
    private ItemDisplay inviteDisplay;

    private final Location instanceInviteLocation;
    private final Location instanceInviteInteraction;

    private ItemStack inviteMagnet;
    private boolean entered;
    private final String teamRoomTag;

    public House(Apr2023QuestInstance instance) {
        this.instance = instance;

        entered = false;
        teamRoomTag = TeamUtils.toTeamTag(instance.getTeam(), ROOM_TAG);

        instanceInviteLocation = instance.offsetLocation(baseInviteLocation);
        instanceInviteInteraction = instance.offsetLocation(baseInviteInteractionLocation);

        resetEntities();

    }

    public void setEntities() {
        Location instanceLeaveDoor = instance.offsetLocation(baseLeaveDoor);
        Interaction leaveInteraction = (Interaction) instanceLeaveDoor.getWorld().spawnEntity(instanceLeaveDoor, EntityType.INTERACTION);
        leaveInteraction.addScoreboardTag(LEAVE_DOOR);
        leaveInteraction.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        leaveInteraction.addScoreboardTag(teamRoomTag);
        leaveInteraction.setInteractionWidth(1.0f);
        leaveInteraction.setInteractionHeight(2.0f);
        leaveInteraction.setResponsive(true);


        inviteDisplay = (ItemDisplay) instanceInviteLocation.getWorld().spawnEntity(instanceInviteLocation, EntityType.ITEM_DISPLAY);
        inviteDisplay.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        inviteDisplay.addScoreboardTag(teamRoomTag);
        inviteDisplay.setTransformation(new Transformation(
                new Vector3f(0.1f, -0.3f, 0.2f),
                new Quaternionf(0.0f, -0.7071, 0.0f, 0.7071),
                new Vector3f(0.4f, 0.4f, 0.4f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f))
        );
        inviteMagnet = GuiUtil.formatItem("Party Invite", Material.WRITTEN_BOOK, 1);

        startInviteSparkle();


        Interaction inviteInteraction = (Interaction) instanceInviteInteraction.getWorld().spawnEntity(instanceInviteInteraction, EntityType.INTERACTION);
        inviteInteraction.addScoreboardTag(INVITE);
        inviteInteraction.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        inviteInteraction.addScoreboardTag(teamRoomTag);
        inviteInteraction.setInteractionHeight(0.4f);
        inviteInteraction.setInteractionWidth(0.4f);
    }

    public void init() {
        if (!entered) {
            entered = true;
            resetEntities();
        }
    }

    public void resetEntities() {
        Chunk interactionChunk = instanceInviteInteraction.getChunk();
        Chunk displayChunk = instanceInviteLocation.getChunk();
        if ((interactionChunk.isEntitiesLoaded() || interactionChunk.load()) && (displayChunk.isEntitiesLoaded() || displayChunk.load())) {
            List<Entity> entities = Bukkit.selectEntities(Apr2023QuestManager.sender, String.format("@e[tag=%s]", teamRoomTag));
            for (Entity entity : entities) {
                entity.remove();
            }
            setEntities();

            interactionChunk.unload(true);
            displayChunk.unload(true);
        }
    }

    public void teleport(Player player) {
        player.teleport(instance.offsetLocation(baseTeleport), PlayerTeleportEvent.TeleportCause.PLUGIN);
        init();
    }

    public void leave(Player player) {
        player.teleport(leaveTeleport, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void startInviteSparkle() {
        Block block = instance.offsetLocation(baseInviteSparkleRedstone).getBlock();
        block.setType(Material.AIR);
        block.setType(Material.REDSTONE_BLOCK);
        if (inviteDisplay != null && inviteMagnet != null) {
            inviteDisplay.setItemStack(inviteMagnet);
        }
    }

    public void collectInvite() {
        instance.offsetLocation(baseInviteSparkleRedstone).getBlock().setType(Material.AIR);
        if (inviteDisplay != null) {
            inviteDisplay.setItemStack(null);
        }
    }

}
