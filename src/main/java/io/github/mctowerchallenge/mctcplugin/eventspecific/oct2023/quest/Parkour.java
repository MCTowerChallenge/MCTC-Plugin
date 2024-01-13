package io.github.mctowerchallenge.mctcplugin.eventspecific.oct2023.quest;

import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.event.MVPortalEvent;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.quest.Quest;
import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.util.FullInventory;
import io.github.mctowerchallenge.mctcplugin.utility.MVPortalUtils;
import io.github.mctowerchallenge.mctcplugin.utility.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class Parkour implements Listener {

    public static final Location TEMPLATE_ENTER_LOCATION = new Location(Worlds.Oct2023_quest(), -13.0, 65.0, -35.0, -90.0f, 0.0f);
    public static final Location TEMPLATE_LEAVE_LOCATION = new Location(Worlds.Oct2023_quest(), -22.0, 73.0, -5.0, -90.0f, 0.0f);

    public static final String TEMPLATE_LEAVE_NAME = "Oct2023_fromParkour";
    public static final Location[] TEMPLATE_PARKOUR_LEAVE_PORTAL = new Location[]{
            new Location(Worlds.Oct2023_quest(), -15, 65, -36),
            new Location(Worlds.Oct2023_quest(), -15, 67, -35)
    };

    public static final String TEMPLATE_COMPLETE_NAME = "Oct2023_completeParkour";
    public static final Location[] TEMPLATE_PARKOUR_COMPLETE_PORTAL = new Location[]{
            new Location(Worlds.Oct2023_quest(), -35, 71, -46),
            new Location(Worlds.Oct2023_quest(), -35, 75, -44)
    };


    private final Oct2023QuestInstance questInstance;
    private final String instanceCompleteName;

    public Parkour(Oct2023QuestInstance questInstance) {
        this.questInstance = questInstance;

        MVPortalUtils.initPortal(TeamUtils.toTeamTag(questInstance.getTeam(), TEMPLATE_LEAVE_NAME), new Location[]{
                questInstance.offsetLocation(TEMPLATE_PARKOUR_LEAVE_PORTAL[0]),
                questInstance.offsetLocation(TEMPLATE_PARKOUR_LEAVE_PORTAL[1])
        }, questInstance.offsetLocation(TEMPLATE_LEAVE_LOCATION));

        this.instanceCompleteName = TeamUtils.toTeamTag(questInstance.getTeam(), TEMPLATE_COMPLETE_NAME);
        MVPortalUtils.initPortal(instanceCompleteName, new Location[]{
                questInstance.offsetLocation(TEMPLATE_PARKOUR_COMPLETE_PORTAL[0]),
                questInstance.offsetLocation(TEMPLATE_PARKOUR_COMPLETE_PORTAL[1])
        }, questInstance.offsetLocation(TEMPLATE_LEAVE_LOCATION));
    }

    public boolean complete(Player player) {
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPortal(final MVPortalEvent event) {
        MVPortal portal = event.getSendingPortal();
        if (portal.getName().equals(instanceCompleteName)) {
            Player player = event.getTeleportee();
            if (questInstance.getTeam().hasPlayer(player)) {
                Bukkit.getLogger().info("Complete");
                complete(player);
            }
        }
    }

}
