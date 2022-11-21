package io.github.idkahn.towerchallenge.hats;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.towering.GodTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.*;

public class HatGUI implements Listener {

    public static final String UI_NAME = "Select a Hat!";
    private final EventManager eventManager;
    private final Plugin plugin;
    private final Color color;
    private final Map<UUID, Inventory> inventories;
    private List<String> winners;

    public static int getInventorySize(int NumberOfItems) {
        return 9*(((NumberOfItems-1)/9)+1);
    }

    public HatGUI(EventManager eventManager, Color color) {
        this.plugin = eventManager.getPlugin();
        this.eventManager = eventManager;
        this.inventories = new HashMap<>();
        this.winners = new ArrayList<>();
        this.color = color;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public HatGUI(EventManager eventManager, String hexColor) {
        this(eventManager, Color.fromRGB(Integer.parseInt(hexColor.replaceAll("#", ""), 16)));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (!event.getInventory().equals(getInventory((Player) event.getWhoClicked())))
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir())
            return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (HatUtil.isHat(event.getCurrentItem())) {
            player.getInventory().setHelmet(event.getCurrentItem());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, player::closeInventory, 1);
        }
    }

    public List<String> getWinners() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.hatConfigFile);
        winners = config.getStringList("Winners");
        return winners;
    }

    public void addWinner(Player player) {
        addWinner(player.getUniqueId().toString());
    }

    public void addWinner(String uuid) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.hatConfigFile);
        winners = config.getStringList("Winners");
        winners.add(uuid);
        config.set("Winners", winners);
        try {
            config.save(TowerChallenge.hatConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeWinner(String uuid) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.hatConfigFile);
        winners = config.getStringList("Winners");
        winners.remove(uuid);
        config.set("Winners", winners);
        try {
            config.save(TowerChallenge.hatConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeWinners(Player player) {
        removeWinner(player.getUniqueId().toString());
    }

    public void openInventory(Player player) {
        player.openInventory(createInventory(player));
    }

    public Inventory getInventory(Player player) {
        return inventories.get(player.getUniqueId());
    }

    public Inventory createInventory(Player player) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.hatConfigFile);
        List<HashMap> hats = new ArrayList<>();
        // gets Normal Hats
        hats.addAll((List<HashMap>) config.getList("Hats"));
        // gets God hats, if applicable
        if (eventManager.getTowerListener().getPlayerTeam(player) instanceof GodTeam) {
            hats.addAll((List<HashMap>) config.getList("GodHats"));
        }
        // gets Player's hats, if applicable
        List<HashMap> playerHats = (List<HashMap>) config.getList("PlayerHats."+player.getUniqueId());
        if (playerHats != null) {
            hats.addAll(playerHats);
        }
        winners = config.getStringList("Winners");
        if (winners.contains(player.getUniqueId().toString())) {
            hats.addAll((List<HashMap>) config.getList("WinnerHats"));
        }

        int numHats = hats.size();
        int newSize = getInventorySize(numHats);

        List<ItemStack> hatItems = new ArrayList<>();

        for (HashMap hat : hats) {
            String name = (String) hat.get("name");
            String item = ((String) hat.get("item")).toUpperCase();
            String author = ((String) hat.get("author"));
            String inspired = ((String) hat.get("inspired"));
            int customModelData = (int) hat.get("custom_model_data");
            String colorString = (String) hat.get("color");
            Color color = this.color;
            if (colorString != null) {
                color = Color.fromRGB(Integer.parseInt(colorString.replaceAll("#", ""), 16));
            }
            hatItems.add(getHat(name, Material.getMaterial(item), author, inspired, customModelData, color));
        }

        Inventory inventory = Bukkit.createInventory(null, newSize, Component.text(UI_NAME));
        inventories.put(player.getUniqueId(), inventory);

        for (int i = 0; i < hatItems.size(); i++) {
            inventory.setItem(i, hatItems.get(i));
        }

        return inventory;
    }

    public ItemStack getHat(String name, Material type, String author, String inspired, int customModelData) {
        return getHat(name, type, author, inspired, customModelData, color);
    }

    public ItemStack getHat(String name, Material type, String author, String inspired, int customModelData, Color color) {
        ItemStack hat = HatUtil.setHat(new ItemStack(type));
        ItemMeta hatMeta = hat.getItemMeta();

        hatMeta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        hatMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        hatMeta.addItemFlags(ItemFlag.HIDE_DYE);
        hatMeta.setCustomModelData(customModelData);
        List<Component> lore = new ArrayList<>();
        if (author != null) {
            lore.add(Component.text("Model by " + author + "").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR));
        }
        if (inspired != null) {
            lore.add(Component.text("Inspired by " + inspired + "").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR));
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
        if (hatMeta instanceof LeatherArmorMeta armorMeta && color != null) {
            armorMeta.setColor(color);
            hat.setItemMeta(armorMeta);
        } else {
            hat.setItemMeta(hatMeta);
        }
        return hat;
    }

}
