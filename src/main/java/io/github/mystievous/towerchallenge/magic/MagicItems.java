package io.github.mystievous.towerchallenge.magic;

import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.eventspecific.winter.presents.PresentEntityHandler;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.page.PresetGui;
import io.github.mystievous.towerchallenge.hats.HatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.util.RayTraceResult;

import java.util.UUID;

public class MagicItems {

    public static final ItemStack speedBoots = new ItemStack(Material.LEATHER_BOOTS) {{
        ItemMeta meta = getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "move-speed", 0.4, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET));
        meta.displayName(TextUtil.noItalic("Boots of Swiftness"));
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.setUnbreakable(true);
        setItemMeta(meta);
    }};

    public static final ItemStack greaterSpeedBoots = new ItemStack(Material.LEATHER_BOOTS) {{
        ItemMeta meta = getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "move-speed", 0.75, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET));
        meta.displayName(TextUtil.noItalic("Greater Boots of Swiftness"));
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.setUnbreakable(true);
        setItemMeta(meta);
    }};

    public static final Wand snowballWand = new Wand("snowball", new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
        LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
        meta.displayName(TextUtil.noItalic("Snow Shooter"));
        meta.setColor(Color.fromRGB(0xbff2fb));
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.setCustomModelData(1005);
        setItemMeta(meta);
    }}, playerInteractEvent -> {
        Player player = playerInteractEvent.getPlayer();
        World world = player.getWorld();
        int numPerEvent = 4;
        for (int i = 0; i < numPerEvent + 1; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), () -> {
                player.launchProjectile(Snowball.class);
                world.playSound(player, Sound.ENTITY_SNOWBALL_THROW, 1f, 1f);
            }, i * (4 / numPerEvent));
        }
    });

    public static final Wand cowWand = new Wand("cow", new ItemStack(Material.STICK) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(TextUtil.noItalic("Cow Wand"));
        meta.setCustomModelData(1);
        setItemMeta(meta);
    }}, playerInteractEvent -> {
        Player player = playerInteractEvent.getPlayer();
        Cow cow = (Cow) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.COW);
        cow.setVelocity(player.getEyeLocation().getDirection().multiply(3.0d));
        cow.setInvulnerable(true);
        cow.customName(Component.text("Moo-ssile"));
        cow.setLootTable(LootTables.EMPTY.getLootTable());
        Bukkit.getScheduler().scheduleSyncDelayedTask(TowerChallenge.getInstance(), () -> {
            cow.getLocation().createExplosion(5, false, false);
            cow.setHealth(0);
        }, 60);
    });

    public static final Wand lightningWand = new Wand("lightning", new ItemStack(Material.STICK) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(TextUtil.noItalic("Lightning Wand"));
        meta.setCustomModelData(2);
        setItemMeta(meta);
    }}, playerInteractEvent -> {
        Player player = playerInteractEvent.getPlayer();
        RayTraceResult rayTrace = player.rayTraceBlocks(50);
        if (rayTrace != null) {
            player.getWorld().strikeLightning(rayTrace.getHitPosition().toLocation(player.getWorld()));
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

    public static final Wand goatHat = new Wand(GoatHat.GOAT_HAT, NBTUtils.setBool(GoatHat.GOAT_HAT, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
        LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.setCustomModelData(804);
        meta.displayName(TextUtil.noItalic("Goat Horns"));
        meta.lore(TextUtil.formatTexts(Component.text("Woah, Goat Horns!"), Component.text("Maybe you should try"), Component.text("sneaking with this on."), Component.text(""), Component.keybind("key.use").append(Component.text(" with this")), Component.text("in your hand to equip")));
        setItemMeta(meta);
    }}), playerInteractEvent -> {
        Player player = playerInteractEvent.getPlayer();
        EntityEquipment equipment = player.getEquipment();
        ItemStack helmet = equipment.getHelmet();
        ItemStack hand = equipment.getItemInMainHand();
        equipment.setHelmet(hand);
        if (!HatUtil.isHat(helmet)) {
            equipment.setItemInMainHand(helmet);
        } else {
            equipment.setItemInMainHand(null);
        }

    });

    public static final Wand portalReplaceWand = new Wand("portal-replace", new ItemStack(Material.STICK) {{
        ItemMeta meta = getItemMeta();
        meta.displayName(TextUtil.formatText("Portal Gateway Wand"));
        meta.lore(TextUtil.formatTexts("!! WARNING !!", "Replaces the block", "you're looking at", "with an end gateway."));
        setItemMeta(meta);
    }}, playerInteractEvent -> {
        Player player = playerInteractEvent.getPlayer();
        Block lookBlock = player.getTargetBlock(10);
        if (lookBlock != null) {
            lookBlock.setType(Material.END_GATEWAY);
            lookBlock = lookBlock.getLocation().getBlock();
            if (lookBlock.getState() instanceof EndGateway endGateway) {
//                player.sendMessage("yay");
                endGateway.setAge(-922337203685477580L);
                endGateway.update();
//                lookBlock.setBlockData(endGateway.getBlockData());
            }
        }
    });

    public static ItemStack randomUUID(ItemStack itemStack) {
        return NBTUtils.setUniqueID(itemStack, UUID.randomUUID());
    }

    /**
     * Gets the GUI with all the magic items inside
     *
     * @return the Gui
     */
    public static PresetGui getGui() {
        PresetGui gui = new PresetGui(Component.text("Magic Items"), 3);
        gui.placeElement(1, 1, new ButtonElement(snowballWand.getItem(), player -> {
            player.getInventory().addItem(randomUUID(snowballWand.getItem()));
        }));
        gui.placeElement(2, 1, new ButtonElement(cowWand.getItem(), player -> {
            player.getInventory().addItem(randomUUID(cowWand.getItem()));
        }));
        gui.placeElement(3, 1, new ButtonElement(lightningWand.getItem(), player -> {
            player.getInventory().addItem(randomUUID(lightningWand.getItem()));
        }));
        gui.placeElement(4, 1, new ButtonElement(presentWand.getItem(), player -> {
            player.getInventory().addItem(randomUUID(presentWand.getItem()));
        }));
        gui.placeElement(1, 3, new ButtonElement(speedBoots, player -> {
            player.getInventory().addItem(speedBoots);
        }));
        gui.placeElement(2, 3, new ButtonElement(greaterSpeedBoots, player -> {
            player.getInventory().addItem(greaterSpeedBoots);
        }));
        gui.placeElement(4, 3, new ButtonElement(goatHat.getItem(), player -> {
            player.getInventory().addItem(goatHat.getItem());
        }));

        gui.placeElement(9, 3, new ButtonElement(portalReplaceWand.getItem(), player -> {
            player.getInventory().addItem(portalReplaceWand.getItem());
        }));
        return gui;
    }
}
