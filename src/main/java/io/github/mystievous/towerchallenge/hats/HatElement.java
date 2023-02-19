package io.github.mystievous.towerchallenge.hats;

import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.magic.MagicItems;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.towerchallenge.utility.Color;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HatElement extends ButtonElement {

    public HatElement(Component name, Material material, @Nullable Integer customModelData, @Nullable Color color, @Nullable String author, @Nullable String referenced) {
        super(HatUtil.setHat(new ItemStack(material) {{
            ItemMeta meta = getItemMeta();
            meta.displayName(name);
            meta.setCustomModelData(customModelData);
            List<Component> lore = new ArrayList<>();
            if (author != null) {
                lore.add(TextUtil.formatText("Model by "+author));
            }
            if (referenced != null) {
                lore.add(TextUtil.formatText("Inspiration from "+referenced));
            }
            meta.lore(lore);
            if (color != null && meta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(color.toBukkitColor());
            }
            meta.addItemFlags(ItemFlag.HIDE_DYE);
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
            setItemMeta(meta);
        }}));
        setConsumer(player -> {
            PlayerInventory inventory = player.getInventory();
            if (NBTUtils.boolState(MagicItems.goatHat.getTag(), inventory.getHelmet())) {
                player.sendMessage(CommandUtils.errorMessage("Please unequip the goat horns first!"));
            } else {
                inventory.setHelmet(getItem());
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), player::closeInventory, 1);
        });
    }

    public HatElement(String name, Material material, @Nullable Integer customModelData, @Nullable Color color, @Nullable String author, @Nullable String referenced) {
        this(TextUtil.noItalic(name), material, customModelData, color, author, referenced);
    }

}
