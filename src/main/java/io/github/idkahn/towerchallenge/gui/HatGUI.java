package io.github.idkahn.towerchallenge.gui;

import io.github.idkahn.towerchallenge.towering.TowerListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class HatGUI implements Listener {

    private Plugin plugin;
    private List<ItemStack> hats;
    private Inventory inventory;
    private TowerListener towerListener;

    public HatGUI(Plugin plugin, TowerListener towerListener) {
        this.plugin = plugin;
        this.hats = new ArrayList<>();
        this.inventory = Bukkit.createInventory(null, 9, Component.text("Select a Hat!"));
        this.towerListener = towerListener;
        this.reloadHats();
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory))
            return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().displayName() == null) return;

//        if (event.getCurrentItem().getItemMeta().lore().get(0).contains(Component.text("Hat"))) {
            Player player = (Player) event.getWhoClicked();

            player.getInventory().setHelmet(event.getCurrentItem());
            player.closeInventory();
            event.setCancelled(true);
//        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void openInventory(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if (item.getItemMeta() instanceof LeatherArmorMeta) {
                    LeatherArmorMeta meta =  (LeatherArmorMeta) item.getItemMeta();
                    String color = towerListener.getTeam(scoreboard.getPlayerTeam(player).getName()).getColor();
                    meta.setColor(Color.fromRGB(Integer.parseInt(color.replaceAll("#", ""), 16)));
                    item.setItemMeta(meta);
                }
            }

        }
        player.openInventory(inventory);

    }

    public void reloadHats() {
        plugin.reloadConfig();
        List<HashMap> configHats = (List<HashMap>) plugin.getConfig().getList("Hats");

        this.hats.clear();
        this.inventory.clear();

        for (HashMap config : configHats) {
            String name = (String) config.get("name");
            String item = ((String) config.get("item")).toUpperCase();
            int customModelData = (int) config.get("custom_model_data");

            ItemStack hat = new ItemStack(Material.getMaterial(item));
            ItemMeta hatMeta = hat.getItemMeta();
            hatMeta.displayName(Component.text(name));
            hatMeta.setCustomModelData(customModelData);
            List<Component> lore = new ArrayList<Component>();
            lore.add(Component.text("Hat"));
            hatMeta.lore(lore);
            hatMeta.addAttributeModifier(
                    Attribute.GENERIC_ARMOR,
                    new AttributeModifier(
                            UUID.randomUUID(),
                            "armor",
                            3.0,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlot.HEAD
                    )
            );
            hatMeta.addAttributeModifier(
                    Attribute.GENERIC_ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            UUID.randomUUID(),
                            "armor",
                            2.0,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlot.HEAD
                    )
            );
            hat.setItemMeta(hatMeta);
            this.hats.add(hat);
        }

        if (this.hats.size() <= 9) {
            for (int i = 0; i < this.hats.size(); i++) {
                this.inventory.setItem(i, this.hats.get(i));
            }
        }

    }

}
