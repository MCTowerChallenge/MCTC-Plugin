package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class TeamItemListener implements Listener {

    public TeamItemListener() {
        Bukkit.getPluginManager().registerEvents(this, TowerChallenge.me);
    }

    public static boolean teamCanInteract(Player player, ItemStack itemStack) {
        TowerTeam team = TowerChallenge.me.getChallengeManager().getPlayerTeam(player);

        if (NBTUtils.hasTeam(itemStack)) {
            return team != null && (NBTUtils.matchTeam(itemStack, team) || team instanceof GodTeam);
        }

        return true;
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(final PlayerAttemptPickupItemEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        if (!teamCanInteract(player, item)) {
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

            if (!teamCanInteract(player, item)) {
                event.setCancelled(true);
            }
        }
    }

}
