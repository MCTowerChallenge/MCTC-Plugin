package io.github.mystievous.towerchallenge.hats;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.minesweeper.gamepieces.Flag;
import io.github.mystievous.towerchallenge.magic.GoatHat;
import io.github.mystievous.towerchallenge.magic.MagicItems;
import io.github.mystievous.towerchallenge.quests.utils.FullInventory;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HatElement extends ButtonElement {

    public HatElement(Plugin plugin, Component name, Material material, @Nullable Integer customModelData, @Nullable Color color, @Nullable String author, @Nullable String referenced, boolean handheld) {
        super(new ItemStack(material) {{
            ItemMeta meta = getItemMeta();
            if (!handheld) {
                HatUtil.setHat(plugin, meta, true);
                meta.addAttributeModifier(
                        Attribute.GENERIC_ARMOR,
                        new AttributeModifier(
                                UUID.randomUUID(),
                                "armor",
                                3.0,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HEAD
                        )
                );
                meta.addAttributeModifier(
                        Attribute.GENERIC_ARMOR_TOUGHNESS,
                        new AttributeModifier(
                                UUID.randomUUID(),
                                "armor",
                                2.0,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HEAD
                        )
                );
            } else {
                NBTUtils.setNoUse(meta);
            }
            meta.displayName(name);
            meta.setCustomModelData(customModelData);
            List<Component> lore = new ArrayList<>();
            if (author != null) {
                lore.add(TextUtil.formatText("Model by " + author));
            }
            if (referenced != null) {
                lore.add(TextUtil.formatText("Inspiration from " + referenced));
            }
            if (handheld) {
                lore.add(TextUtil.formatText("Handheld").decoration(TextDecoration.ITALIC, true));
            }
            if (handheld && material.equals(Material.SCUTE)) {
//                NBTUtils.setBool(plugin, Flag.FLAG_TAG, meta, true);
                Flag.makeItemFlag(plugin, meta);
            }
            meta.lore(lore);
            if (color != null && meta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(color.toBukkitColor());
            }
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            setItemMeta(meta);
        }});
        setConsumer(player -> {
            if (!handheld) {
                PlayerInventory inventory = player.getInventory();
                ItemStack helmet = inventory.getHelmet();
                if (NBTUtils.boolState(TowerChallenge.getInstance(), GoatHat.GOAT_HAT, helmet)) {
                    FullInventory.givePlayerItems(player, helmet);
                }
                inventory.setHelmet(getItem());
                Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), player::closeInventory, 1);
            } else {
                player.getInventory().addItem(getItem());
            }
        });
    }

    public HatElement(Plugin plugin, Component name, Material material, @Nullable Integer customModelData, @Nullable Color color, @Nullable String author, @Nullable String referenced) {
        this(plugin, name, material, customModelData, color, author, referenced, false);
    }

    public HatElement(Plugin plugin, String name, Material material, @Nullable Integer customModelData, @Nullable Color color, @Nullable String author, @Nullable String referenced, boolean handheld) {
        this(plugin, TextUtil.noItalic(name), material, customModelData, color, author, referenced, handheld);
    }

    public HatElement(Plugin plugin, String name, Material material, @Nullable Integer customModelData, @Nullable Color color, @Nullable String author, @Nullable String referenced) {
        this(plugin, TextUtil.noItalic(name), material, customModelData, color, author, referenced, false);
    }

}
