package io.github.mystievous.towerchallenge.quest.npc;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.god.GodTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class GodMountLegacyNPC extends LegacyNPC {

    public GodMountLegacyNPC(TeamManager teamManager, String name, String tag, Color nameColor, Color textColor) {
        super(teamManager, name, tag, nameColor, textColor);
    }

    public GodMountLegacyNPC(TeamManager teamManager, String name, String tag) {
        super(teamManager, name, tag);
    }

    /**
     * Stops non-gods from mounting a god mount
     */
    @EventHandler
    public void onEntityMount(final EntityMountEvent event) {

        if (event.getEntity() instanceof Player player) {
            if (event.getMount().getScoreboardTags().contains(getTag())) {
                if (!(getTeamManager().getPlayerTeam(player) instanceof GodTeam)) {
                    event.setCancelled(true);
                }
            }
        }

    }

    /**
     * Stops non-gods from opening the inventory of a god mount
     */
    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Entity entity &&
                event.getPlayer() instanceof Player player) {
            if (entity.getScoreboardTags().contains(getTag())) {
                if (!(getTeamManager().getPlayerTeam(player) instanceof GodTeam)) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
