package io.github.mystievous.towerchallenge.magic;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mystievous.towerchallenge.Database;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.decoration.WaterDrips;
import io.github.mystievous.towerchallenge.hats.HatUtil;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.towerchallenge.utility.WorldNotStoredException;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;

import java.sql.SQLException;
import java.util.UUID;

public class MagicItems implements Openable {

    private final Plugin plugin;

    public final ItemStack speedBoots;
    public final ItemStack greaterSpeedBoots;
    public final Wand snowballWand;
    public final Wand cowWand;
    public final Wand lightningWand;
    public final Wand goatHat;
    public final Wand portalReplaceWand;
    public final Wand waterWand;
//    public final Wand raftWand;

    public MagicItems(Plugin plugin, Database database, WaterDrips waterDrips) {
        this.plugin = plugin;

        speedBoots = new ItemStack(Material.LEATHER_BOOTS) {{
            ItemMeta meta = getItemMeta();
            meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "move-speed", 0.4, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET));
            meta.displayName(TextUtil.noItalic("Boots of Swiftness"));
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            meta.setUnbreakable(true);
            setItemMeta(meta);
        }};

        greaterSpeedBoots = new ItemStack(Material.LEATHER_BOOTS) {{
            ItemMeta meta = getItemMeta();
            meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "move-speed", 0.75, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET));
            meta.displayName(TextUtil.noItalic("Greater Boots of Swiftness"));
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            meta.setUnbreakable(true);
            setItemMeta(meta);
        }};

        snowballWand = new Wand(plugin, "snowball", new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
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

        cowWand = new Wand(plugin, "cow", new ItemStack(Material.STICK) {{
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

        lightningWand = new Wand(plugin, "lightning", new ItemStack(Material.STICK) {{
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

        goatHat = new Wand(plugin, GoatHat.GOAT_HAT, NBTUtils.setBool(TowerChallenge.getInstance(), GoatHat.GOAT_HAT, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
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

        portalReplaceWand = new Wand(plugin, "portal-replace", new ItemStack(Material.STICK) {{
            ItemMeta meta = getItemMeta();
            meta.displayName(TextUtil.formatText("Portal Gateway Wand"));
            meta.lore(TextUtil.formatTexts("!! WARNING !!", "Replaces the block", "you're looking at", "with an end gateway."));
            setItemMeta(meta);
        }}, playerInteractEvent -> {
            Player player = playerInteractEvent.getPlayer();
            Block lookBlock = player.getTargetBlockExact(10);
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

        waterWand = new Wand(plugin, "water-wand", GuiUtil.formatItem("Water Wand", Material.TIPPED_ARROW, 0), event -> {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Block block = event.getClickedBlock();
                Player player = event.getPlayer();
                if (block != null) {
                    try {
                        boolean existed = database.removeWaterDrip(block.getLocation());
                        if (!existed) {
                            database.addWaterDrip(block.getLocation());
                            player.sendMessage(TextUtil.formatText("Added water drip"));
                        } else {
                            player.sendMessage(TextUtil.formatText("Removed water drip"));
                        }
                        waterDrips.loadDrips();
                    } catch (WorldNotStoredException e) {
                        event.getPlayer().sendMessage(CommandUtils.errorMessage("Error: " + e.getMessage()));
                        Bukkit.getLogger().warning("Error: " + e.getMessage());
                    } catch (SQLException e) {
                        event.getPlayer().sendMessage(CommandUtils.errorMessage("Error manipulating water drips: " + e.getMessage()));
                        Bukkit.getLogger().warning("Error manipulating water drips: " + e.getMessage());
                    }
                }
            });
        });

//        raftWand = new Wand(plugin, "raft-wand", GuiUtil.formatItem("Raft Wand", Material.BAMBOO, 0), event -> {
//            Player player = event.getPlayer();
//            if (player.isSneaking()) {
//                player.getWorld().spawnEntity(player.getLocation(), EntityType.)
//            } else {
//
//            }
//        });

    }

    public ItemStack randomUUID(ItemStack itemStack) {
        return NBTUtils.setUniqueID(plugin, itemStack, UUID.randomUUID());
    }

    @Override
    public Gui getGui(Player guiPlayer) {
        PresetGui gui = new PresetGui(plugin, Component.text("Magic Items"), 3);
        gui.placeElement(1, 1, new ButtonElement(snowballWand.getItem(), player -> player.getInventory().addItem(randomUUID(snowballWand.getItem()))));
        gui.placeElement(1, 2, new ButtonElement(cowWand.getItem(), player -> player.getInventory().addItem(randomUUID(cowWand.getItem()))));
        gui.placeElement(1, 3, new ButtonElement(lightningWand.getItem(), player -> player.getInventory().addItem(randomUUID(lightningWand.getItem()))));
        gui.placeElement(3, 1, new ButtonElement(speedBoots, player -> player.getInventory().addItem(speedBoots)));
        gui.placeElement(3, 2, new ButtonElement(greaterSpeedBoots, player -> player.getInventory().addItem(greaterSpeedBoots)));
        gui.placeElement(3, 4, new ButtonElement(goatHat.getItem(), player -> player.getInventory().addItem(goatHat.getItem())));

        gui.placeElement(3, 8, new ButtonElement(waterWand.getItem(), player -> player.getInventory().addItem(waterWand.getItem())));
        gui.placeElement(3, 9, new ButtonElement(portalReplaceWand.getItem(), player -> player.getInventory().addItem(portalReplaceWand.getItem())));
        return gui;
    }

}
