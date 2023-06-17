package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArmorTrimsGui implements Openable {

    public static final Map<TrimPattern, Material> PATTERNS = new HashMap<>() {{
        put(TrimPattern.SENTRY, Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.VEX, Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.WILD, Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.COAST, Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.DUNE, Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.WAYFINDER, Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.RAISER, Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.SHAPER, Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.HOST, Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.WARD, Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.SILENCE, Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.TIDE, Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.SNOUT, Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.RIB, Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.EYE, Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        put(TrimPattern.SPIRE, Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
    }};

    public static final Map<Material, Map<EquipmentSlot, Material>> ARMORS = new HashMap<>() {{
        put(Material.CHAINMAIL_HELMET, new HashMap<>() {{
            put(EquipmentSlot.HEAD, Material.CHAINMAIL_HELMET);
            put(EquipmentSlot.CHEST, Material.CHAINMAIL_CHESTPLATE);
            put(EquipmentSlot.LEGS, Material.CHAINMAIL_LEGGINGS);
            put(EquipmentSlot.FEET, Material.CHAINMAIL_BOOTS);
        }});
        put(Material.LEATHER_HELMET, new HashMap<>() {{
            put(EquipmentSlot.HEAD, Material.LEATHER_HELMET);
            put(EquipmentSlot.CHEST, Material.LEATHER_CHESTPLATE);
            put(EquipmentSlot.LEGS, Material.LEATHER_LEGGINGS);
            put(EquipmentSlot.FEET, Material.LEATHER_BOOTS);
        }});
        put(Material.GOLDEN_HELMET, new HashMap<>() {{
            put(EquipmentSlot.HEAD, Material.GOLDEN_HELMET);
            put(EquipmentSlot.CHEST, Material.GOLDEN_CHESTPLATE);
            put(EquipmentSlot.LEGS, Material.GOLDEN_LEGGINGS);
            put(EquipmentSlot.FEET, Material.GOLDEN_BOOTS);
        }});
        put(Material.IRON_HELMET, new HashMap<>() {{
            put(EquipmentSlot.HEAD, Material.IRON_HELMET);
            put(EquipmentSlot.CHEST, Material.IRON_CHESTPLATE);
            put(EquipmentSlot.LEGS, Material.IRON_LEGGINGS);
            put(EquipmentSlot.FEET, Material.IRON_BOOTS);
        }});
        put(Material.DIAMOND_HELMET, new HashMap<>() {{
            put(EquipmentSlot.HEAD, Material.DIAMOND_HELMET);
            put(EquipmentSlot.CHEST, Material.DIAMOND_CHESTPLATE);
            put(EquipmentSlot.LEGS, Material.DIAMOND_LEGGINGS);
            put(EquipmentSlot.FEET, Material.DIAMOND_BOOTS);
        }});
        put(Material.NETHERITE_HELMET, new HashMap<>() {{
            put(EquipmentSlot.HEAD, Material.NETHERITE_HELMET);
            put(EquipmentSlot.CHEST, Material.NETHERITE_CHESTPLATE);
            put(EquipmentSlot.LEGS, Material.NETHERITE_LEGGINGS);
            put(EquipmentSlot.FEET, Material.NETHERITE_BOOTS);
        }});
    }};

    public static final Map<Material, TrimMaterial> TRIM_MATERIALS = new HashMap<>() {{
        put(Material.LAPIS_LAZULI, TrimMaterial.LAPIS);
        put(Material.QUARTZ, TrimMaterial.QUARTZ);
        put(Material.REDSTONE, TrimMaterial.REDSTONE);
        put(Material.IRON_INGOT, TrimMaterial.IRON);
        put(Material.GOLD_INGOT, TrimMaterial.GOLD);
        put(Material.COPPER_INGOT, TrimMaterial.COPPER);
        put(Material.AMETHYST_SHARD, TrimMaterial.AMETHYST);
        put(Material.DIAMOND, TrimMaterial.DIAMOND);
        put(Material.NETHERITE_INGOT, TrimMaterial.NETHERITE);
        put(Material.EMERALD, TrimMaterial.EMERALD);
    }};

    private final Plugin plugin;

    public ArmorTrimsGui(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Gui getGui(Player player) {
        ListGui listGui = new ListGui(plugin, Component.text("Select an armor trim"), Element.blank());
        for (Map.Entry<TrimPattern, Material> trimPattern : PATTERNS.entrySet()) {
            listGui.addElement(new ButtonElement(new ItemStack(trimPattern.getValue()), player1 -> {
                ListGui armorGui = new ListGui(plugin, Component.text("Pick an armor set"), Element.blank());
                for (Map.Entry<Material, Map<EquipmentSlot, Material>> armor : ARMORS.entrySet()) {
                    armorGui.addElement(new ButtonElement(new ItemStack(armor.getKey()), player2 -> {
                        ListGui giveGui = new ListGui(plugin, Component.text("Pick a material"), Element.blank());
                        for (Map.Entry<Material, TrimMaterial> trimMaterial : TRIM_MATERIALS.entrySet()) {
                            giveGui.addElement(new ButtonElement(new ItemStack(trimMaterial.getKey()), player3 -> {
                                Inventory inventory = player3.getInventory();
                                for (Map.Entry<EquipmentSlot, Material> armorPiece : armor.getValue().entrySet()) {
                                    ItemStack itemStack = new ItemStack(armorPiece.getValue());
                                    ArmorMeta meta = (ArmorMeta) itemStack.getItemMeta();
                                    meta.setTrim(new ArmorTrim(trimMaterial.getValue(), trimPattern.getKey()));
                                    itemStack.setItemMeta(meta);
                                    inventory.addItem(itemStack);
                                }
                            }));
                        }
                        giveGui.openInventory(player2);
                    }));
                }
                armorGui.openInventory(player1);
            }));
        }
        return listGui;
    }

}
