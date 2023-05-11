package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests;

import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.bottlepuzzle.BottleManager;
import io.github.mystievous.towerchallenge.quests.instances.QuestRegion;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;

public class BadCellar {

    public static final String REGION_TAG = "bad-cellar";
    public static final Location[] baseRegionBounds = new Location[]{new Location(Worlds.Apr2023_quest(), -22, 65, 36), new Location(Worlds.Apr2023_quest(), -11, 71, 44)};

    public static final String EXIT_PORTAL = "bad-celler-exit";
    public static final Location baseEnterDestination = new Location(Worlds.Apr2023_quest(), -19.5, 67, 40.5, -90, 0);
    public static final Location[] baseExitPortalBounds = new Location[]{new Location(Worlds.Apr2023_quest(), -21, 67, 40), new Location(Worlds.Apr2023_quest(), -21, 70, 40)};

    public static final Location basePotionEnterDestination = new Location(Worlds.Apr2023_quest(), -14.5, 65, 40.5, -90, 0);

    public static final Location baseLeftBottle = new Location(Worlds.Apr2023_quest(), -11.50, 66.00, 38.2475);

    public static final Location baseOpenDoor = new Location(Worlds.Apr2023_quest(), -11, 65, 34);


    public static final String ROOM_TAG = "badcellar";

    private final Apr2023QuestInstance instance;
    private final BottleManager bottleManager;

    private final QuestRegion region;

    private final String teamRoomTag;

    private boolean entered;

    public BadCellar(Plugin plugin, Apr2023QuestInstance instance) {
        this.instance = instance;

        instance.offsetLocation(baseOpenDoor).getBlock().setType(Material.AIR);

        instance.initPortal(EXIT_PORTAL, baseExitPortalBounds, instance.offsetLocation(BadTavern.baseTrapdoor).add(0, 1, 0).setDirection(new Vector(1, 0, 0)));

        bottleManager = new BottleManager(plugin, instance, instance.offsetLocation(baseLeftBottle));

        region = new QuestRegion(plugin, instance, baseRegionBounds, REGION_TAG);

        entered = false;
        teamRoomTag = TeamUtils.toTeamTag(instance.getTeam(), ROOM_TAG);

    }

    public void init() {
        if (!entered) {
            entered = true;
            List<Entity> entities = Bukkit.selectEntities(Apr2023QuestManager.sender, String.format("@e[tag=%s]", teamRoomTag));
            for (Entity entity : entities) {
                entity.remove();
            }
            bottleManager.reset();
        }
    }

    public void teleport(Player player) {
        player.teleport(instance.offsetLocation(baseEnterDestination), PlayerTeleportEvent.TeleportCause.PLUGIN);
        init();
    }

    public void potionTeleport(Player player) {
        player.teleport(instance.offsetLocation(basePotionEnterDestination), PlayerTeleportEvent.TeleportCause.PLUGIN);
        init();
    }

    public void openDoor() {
        instance.offsetLocation(baseOpenDoor).getBlock().setType(Material.REDSTONE_TORCH);
    }

    public QuestRegion getRegion() {
        return region;
    }
}
