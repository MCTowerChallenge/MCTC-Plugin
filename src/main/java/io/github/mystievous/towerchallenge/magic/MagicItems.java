package io.github.mystievous.towerchallenge.magic;

import io.github.mystievous.towerchallenge.NBTUtils;
import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.decoration.presents.PresentEntityHandler;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.page.PresetGui;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class MagicItems {

    public static final ItemStack speedBoots = new ItemStack(Material.LEATHER_BOOTS) {{
        ItemMeta meta = getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "move-speed", 0.4, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET));
        meta.displayName(TextUtil.noItalic("Boots of Swiftness"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        setItemMeta(meta);
    }};

    public static final ItemStack greaterSpeedBoots = new ItemStack(Material.LEATHER_BOOTS) {{
        ItemMeta meta = getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "move-speed", 0.75, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET));
        meta.displayName(TextUtil.noItalic("Greater Boots of Swiftness"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        setItemMeta(meta);
    }};

    public static final Wand snowballWand = new Wand("snowball", new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
        LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
        meta.displayName(TextUtil.noItalic("Snow Shooter"));
        meta.setColor(Color.fromRGB(0xbff2fb));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(1005);
        setItemMeta(meta);
    }}, playerInteractEvent -> {
        Player player = playerInteractEvent.getPlayer();
        World world = player.getWorld();
        int numPerEvent = 4;
        for (int i = 0; i < numPerEvent+1; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.me, () -> {
                player.launchProjectile(Snowball.class);
                world.playSound(player, Sound.ENTITY_SNOWBALL_THROW, 1f, 1f);
            }, i*(4/numPerEvent));
        }
    });

    public static final Wand presentWand = new Wand("present-wand", new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
        LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
        meta.displayName(TextUtil.noItalic("Present Summoner"));
        meta.setCustomModelData(1001);
        meta.setColor(Color.WHITE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        setItemMeta(meta);
    }}, playerInteractEvent -> {
        PresentEntityHandler.summonPresent(playerInteractEvent.getPlayer());
    });

    public static ItemStack randomUUID(ItemStack itemStack) {
        return NBTUtils.setUniqueID(itemStack, UUID.randomUUID());
    }

    public static PresetGui getGui() {
        PresetGui gui = new PresetGui(Component.text("Magic Items"), 3);
        gui.placeElement(1, 1, new ButtonElement(snowballWand.getItem(), player -> {
            player.getInventory().addItem(randomUUID(snowballWand.getItem()));
        }));
        gui.placeElement(2, 1, new ButtonElement(presentWand.getItem(), player -> {
            player.getInventory().addItem(randomUUID(presentWand.getItem()));
        }));
        gui.placeElement(1, 3, new ButtonElement(speedBoots, player -> {
            player.getInventory().addItem(speedBoots);
        }));
        gui.placeElement(2, 3, new ButtonElement(greaterSpeedBoots, player -> {
            player.getInventory().addItem(greaterSpeedBoots);
        }));
        return gui;
    }
}
