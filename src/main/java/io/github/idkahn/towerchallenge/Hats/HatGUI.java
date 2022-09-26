package io.github.idkahn.towerchallenge.Hats;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class HatGUI implements Listener {

    private static final String UI_NAME = "Select a Hat!";
    private Plugin plugin;
    private List<ItemStack> hats;
    private Inventory inventory;
    private Color color;

    public static int getInventorySize(int NumberOfItems) {
        return 9*((NumberOfItems/9)+1);
    }

    public HatGUI(Plugin plugin, Color color) {
        this.plugin = plugin;
        this.hats = new ArrayList<>();
        int numHats = plugin.getConfig().getList("Hats").size();
        this.inventory = Bukkit.createInventory(null, getInventorySize(numHats), Component.text(UI_NAME));
        this.color = color;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        this.loadHats();
    }

    public HatGUI(Plugin plugin, String hexColor) {
        this(plugin, Color.fromRGB(Integer.parseInt(hexColor.replaceAll("#", ""), 16)));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory))
            return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (HatUtil.isHat(event.getCurrentItem())) {
            player.getInventory().setHelmet(event.getCurrentItem());
            player.closeInventory();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    public void loadHats() {
        plugin.reloadConfig();
        List<HashMap> configHats = (List<HashMap>) plugin.getConfig().getList("Hats");
        int numHats = configHats.size();
        int newSize = getInventorySize(numHats);

        this.hats.clear();
        this.inventory.clear();
        if (this.inventory.getSize() != newSize) {
            this.inventory = Bukkit.createInventory(null, newSize, Component.text(UI_NAME));
        }

        for (HashMap config : configHats) {
            String name = (String) config.get("name");
            String item = ((String) config.get("item")).toUpperCase();
            String author = ((String) config.get("author"));
            int customModelData = (int) config.get("custom_model_data");
            ItemStack hat = HatUtil.setHat(new ItemStack(Material.getMaterial(item)));
            ItemMeta hatMeta = hat.getItemMeta();

            hatMeta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
            hatMeta.setCustomModelData(customModelData);
            List<Component> lore = new ArrayList<>();
            if (author != null) {
                lore.add(Component.text("Hat by " + author + "").decoration(TextDecoration.ITALIC, false));
            } else {
                lore.add(Component.text("Hat author unknown").decoration(TextDecoration.ITALIC, false));
            }
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
            if (hatMeta instanceof LeatherArmorMeta && color != null) {
                LeatherArmorMeta armorMeta = (LeatherArmorMeta) hatMeta;
                armorMeta.setColor(color);
                hat.setItemMeta(armorMeta);
            } else {
                hat.setItemMeta(hatMeta);
            }
            this.hats.add(hat);
        }

        if (this.hats.size() <= 9) {
            for (int i = 0; i < this.hats.size(); i++) {
                this.inventory.setItem(i, this.hats.get(i));
            }
        }

    }

}
