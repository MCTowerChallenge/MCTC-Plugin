package io.github.idkahn.towerchallenge.towering;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.quests.QuestUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class WinnerGUI implements Listener {

    private EventManager eventManager;
    private Inventory winnerUI;

    public WinnerGUI(EventManager eventManager) {

        this.eventManager = eventManager;
        eventManager.getPlugin().getServer().getPluginManager().registerEvents(this, eventManager.getPlugin());
        initWinnerUI();

    }

    public void initWinnerUI() {
        HashMap<String, ParticipantTeam> teams = eventManager.getTowerListener().getTeams();
        winnerUI = Bukkit.createInventory(null, HatGUI.getInventorySize(teams.size()+2), Component.text("Pick the Winning Team! "));

        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(2);
        exit.setItemMeta(exitMeta);
        winnerUI.setItem(winnerUI.getSize()-1, exit);

        for (Map.Entry<String, ParticipantTeam> entry : teams.entrySet()) {
            ParticipantTeam team = entry.getValue();
            ItemStack item = QuestUtil.setButton(new ItemStack(Material.valueOf(team.getDye()+"_CONCRETE")));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.displayName(team.getDisplayName().decoration(TextDecoration.ITALIC, false));
            itemMeta.setCustomModelData(1);
            item.setItemMeta(itemMeta);
            winnerUI.addItem(item);
        }
    }

    public void openUI(Player player) {
        player.openInventory(winnerUI);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir())
            return;
        if (event.getInventory().equals(winnerUI)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            ItemStack item = event.getCurrentItem();
            if (QuestUtil.isButton(item)) {
                if (event.getSlot() == winnerUI.getSize()-1) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(eventManager.getPlugin(), player::closeInventory, 1);
                } else {
                    String teamName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(item.getItemMeta().displayName()));
                    ParticipantTeam team = eventManager.getTowerListener().getTeam(teamName);
                    player.sendMessage(team.getDisplayName().append(Component.text(" set to the Winners!")));
                    for (String uuid : team.getHatGUI().getWinners()) {
                        player.sendMessage("Checking " + uuid);
                        Player prevWinner = Bukkit.getPlayer(UUID.fromString(uuid));
                        if (prevWinner != null && prevWinner.isOnline()) {
                            team.getHatGUI().removeWinner(uuid);
                            prevWinner.getInventory().setHelmet(null);
                        }
                    }
                    for (Player teamPlayer : team.getOnlinePlayers()) {
                        team.getHatGUI().addWinner(teamPlayer);
                        player.getInventory().setHelmet(team.getHatGUI().getHat("Winner's Crown", Material.LEATHER_HORSE_ARMOR, "apple270", null, 8));
                    }
                    Title title = Title.title(team.getDisplayName().color(team.getTextColor()).append(Component.text(" is the winner!").color(NamedTextColor.WHITE)), Component.empty());
                    Bukkit.getServer().showTitle(title);
                    Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "ui.toast.challenge_complete"), Sound.Source.MASTER, 100, 1));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(eventManager.getPlugin(), player::closeInventory, 1);
                }
            }

        }
    }

}
