package io.github.mystievous.towerchallenge.wands;

import io.github.mystievous.towerchallenge.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.hats.HatGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WandGUI implements Listener {

    public static final String UI_NAME = "Pick a wand!";
    private final Plugin plugin;
    private final List<ItemStack> wands;
    private Inventory inventory;

    public WandGUI(Plugin plugin) {
        this.plugin = plugin;
        this.wands = new ArrayList<>();
        List wands = YamlConfiguration.loadConfiguration(TowerChallenge.wandConfigFile).getList("Wands");
        int numHats = 0;
        if (wands != null) {
            numHats = wands.size();
        }
        this.inventory = Bukkit.createInventory(null, HatGUI.getInventorySize(numHats), Component.text(UI_NAME));
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        this.loadWands();
    }

//    public static void getWand(Player player, int id) {
//        ItemStack stick = WandUtil.setMagic(new ItemStack(Material.STICK), id);
//        ItemMeta stickMeta = stick.getItemMeta();
//        stickMeta.displayName(Component.text("Wand").decoration(TextDecoration.ITALIC, false));
//        stick.setItemMeta(stickMeta);
//        player.getInventory().addItem(stick);
//    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (!event.getInventory().equals(inventory))
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir())
            return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (WandUtil.isWand(event.getCurrentItem())) {
            player.getInventory().addItem(event.getCurrentItem());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::closeInventory, 1);
        }
    }

    public void openInventory(Player player) {
        randomIds();
        player.openInventory(inventory);
    }

    public void randomIds() {
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = NBTUtils.noStack(items[i]);
            inventory.setItem(i, item);
        }
    }

    public void loadWands() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.wandConfigFile);
        List<HashMap> configWands = (List<HashMap>) config.getList("Wands");
        if (configWands == null)
            return;
        int numHats = configWands.size();
        int newSize = HatGUI.getInventorySize(numHats);

        this.wands.clear();
        this.inventory.clear();
        if (this.inventory.getSize() != newSize) {
            this.inventory = Bukkit.createInventory(null, newSize, Component.text(UI_NAME));
        }

        for (HashMap wand : configWands) {
            String name = (String) wand.get("name");
            String type = ((String) wand.get("item")).toUpperCase();
            int magic = (int) wand.get("magic");
            int model = (int) wand.get("model");
            String author = (String) wand.get("author");
            ItemStack item = WandUtil.setMagic(new ItemStack(Material.getMaterial(type)), magic, model);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            if (author != null) {
                lore.add(Component.text("Model by " + author + "").decoration(TextDecoration.ITALIC, false));
//            } else {
//                lore.add(Component.text("Model author unknown").decoration(TextDecoration.ITALIC, false));
            }
            itemMeta.lore(lore);
            item.setItemMeta(itemMeta);
            this.wands.add(item);
            this.inventory.addItem(item);
        }
    }

}
