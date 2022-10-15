package io.github.idkahn.towerchallenge.quests;

import io.github.idkahn.towerchallenge.candy.CandyUtils;
import io.github.idkahn.towerchallenge.wands.WandUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class QuestListener implements Listener {

    private QuestManager questManager;

    public QuestListener(QuestManager questManager) {
        this.questManager = questManager;
        Bukkit.getServer().getPluginManager().registerEvents(this, questManager.getEventManager().getPlugin());
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.isCancelled())
            return;
        CraftingInventory inventory = event.getInventory();
        for (ItemStack item : inventory.getMatrix()) {
//            event.getWhoClicked().sendMessage("Craft");
            if (QuestUtil.isQuestbook(item) || QuestUtil.isVoucher(item) || CandyUtils.isCandy(item)) {
                event.getWhoClicked().sendMessage(Component.text("Nice try ;)"));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (event.getAction() == Action.PHYSICAL
                || event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK
                || item == null)
            return;
        if (QuestUtil.isQuestbook(item)) {
            questManager.openQuestPicker(player);
        }
    }

}
