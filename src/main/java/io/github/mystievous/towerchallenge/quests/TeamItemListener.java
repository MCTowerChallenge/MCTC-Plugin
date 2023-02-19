package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class TeamItemListener implements Listener {

    private final TeamManager teamManager;

    public TeamItemListener(TowerChallenge plugin, TeamManager teamManager) {
        this.teamManager = teamManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean teamCannotInteract(Player player, ItemStack itemStack) {
        TowerTeam team = teamManager.getPlayerTeam(player);

        if (NBTUtils.hasTeam(itemStack)) {
            return team == null || (!NBTUtils.matchTeam(itemStack, team) && !(team instanceof GodTeam));
        }

        return false;
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(final PlayerAttemptPickupItemEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        if (teamCannotInteract(player, item)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (event.isCancelled())
            return;

        if (event.getWhoClicked() instanceof Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE))
                return;

            ItemStack item = event.getCurrentItem();

            if (teamCannotInteract(player, item)) {
                event.setCancelled(true);
            }
        }
    }

}
