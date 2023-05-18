package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests;

import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quests.instances.QuestRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class GoodCellar {

    public static final Location basePotionTeleport = new Location(Worlds.Apr2023_quest(), -14.5, 65, 16.5, -90, 0);
    public static final Location baseTrapdoorTeleport = new Location(Worlds.Apr2023_quest(), -19.5, 67, 16.5, -90, 0);

    public static final String EXIT_PORTAL = "good-celler-exit";
    public static final Location[] baseExitPortalBounds = new Location[]{new Location(Worlds.Apr2023_quest(), -21, 67, 16), new Location(Worlds.Apr2023_quest(), -21, 70, 16)};

    public static final String REGION_TAG = "good-cellar";
    public static final Location[] baseRegionBounds = new Location[]{new Location(Worlds.Apr2023_quest(), -22, 64, 10), new Location(Worlds.Apr2023_quest(), -11, 71, 20)};


    private final Apr2023QuestInstance instance;

    private final QuestRegion region;

    public GoodCellar(Plugin plugin, Apr2023QuestInstance instance) {
        this.instance = instance;

        instance.initPortal(EXIT_PORTAL, baseExitPortalBounds, instance.offsetLocation(GoodTavern.baseTrapdoor).add(0, 1, 0).setDirection(new Vector(1, 0, 0)));

        region = new QuestRegion(plugin, instance, baseRegionBounds, REGION_TAG);
    }

    public QuestRegion getRegion() {
        return region;
    }

    public void trapdoorTeleport(Player player) {
        player.teleport(instance.offsetLocation(baseTrapdoorTeleport), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void potionTeleport(Player player) {
        player.teleport(instance.offsetLocation(basePotionTeleport), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

}
