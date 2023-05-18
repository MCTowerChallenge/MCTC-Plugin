package io.github.mystievous.towerchallenge.quests.npcs;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class GodMountNPC extends NPC {

    private final TeamManager teamManager;

    public GodMountNPC(TeamManager teamManager, String name, String tag, Color nameColor, Color textColor) {
        super(teamManager, name, tag, nameColor, textColor);
        this.teamManager = teamManager;
    }

    /**
     * Stops non-gods from mounting a god mount
     */
    @EventHandler
    public void onEntityMount(final EntityMountEvent event) {

        if (event.getEntity() instanceof Player player) {
            if (event.getMount().getScoreboardTags().contains(getTag())) {
                if (!(teamManager.getPlayerTeam(player) instanceof GodTeam)) {
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
                if (!(teamManager.getPlayerTeam(player) instanceof GodTeam)) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
