package io.github.mctowerchallenge.mctcplugin.magic;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mctowerchallenge.mctcplugin.Database;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.decoration.WaterDrips;
import io.github.mctowerchallenge.mctcplugin.gui.Icons;
import io.github.mctowerchallenge.mctcplugin.gui.page.TeamGui;
import io.github.mctowerchallenge.mctcplugin.hats.HatUtil;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.CharacterManager;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import io.github.mctowerchallenge.mctcplugin.utility.WorldNotStoredException;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.EndGateway;
import org.bukkit.block.data.type.EndPortalFrame;
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

/**
 * Manages various magical items and their behaviors in the Tower Challenge plugin.
 */
public class MagicItems implements Openable {

    private final Plugin plugin;

    public final ItemStack speedBoots;
    public final ItemStack greaterSpeedBoots;
    public final Wand snowballWand;
    public final Wand cowWand;
    public final Wand lightningWand;
    public final Wand goatHat;
    public final Wand endGatewayWand;
    public final Wand endPortalWand;
    public final Wand waterWand;
    public final Wand portalFrameWand;
    public final Wand locationWand;
    public final EntityWand entityLocationWand;
    public final Element setNPCs;
    public final EntityWand testNPCWand;

    /**
     * Initializes a new instance of the MagicItems class.
     *
     * @param plugin        The main plugin instance.
     * @param database      The database used for storing data.
     * @param teamManager   The team manager.
     * @param characterManager The character manager.
     * @param waterDrips    The manager for water drips decoration.
     */
    public MagicItems(Plugin plugin, Database database, TeamManager teamManager, CharacterManager characterManager, WaterDrips waterDrips) {
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(MCTCPlugin.getInstance(), () -> {
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(MCTCPlugin.getInstance(), () -> {
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

        goatHat = new Wand(plugin, GoatHat.GOAT_HAT, NBTUtils.setBool(MCTCPlugin.getInstance(), GoatHat.GOAT_HAT, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
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

        endGatewayWand = new Wand(plugin, "gateway-wand", new ItemStack(Material.STICK) {{
            ItemMeta meta = getItemMeta();
            meta.displayName(TextUtil.formatText("End Gateway Wand"));
            setItemMeta(meta);
        }}, playerInteractEvent -> {
            if (playerInteractEvent.getHand().equals(EquipmentSlot.OFF_HAND))
                return;

            Block clickBlock = playerInteractEvent.getClickedBlock();
            if (clickBlock == null) {
                return;
            }
            clickBlock.setType(Material.END_GATEWAY);
            clickBlock = clickBlock.getLocation().getBlock();
            if (clickBlock.getState() instanceof EndGateway endGateway) {
                endGateway.setAge(-922337203685477580L);
                endGateway.update();
            }
        });

        endPortalWand = new Wand(plugin, "portal-wand",
                GuiUtil.formatItem("End Portal Wand", Material.STICK, 0),
                playerInteractEvent -> {
                    if (playerInteractEvent.getHand().equals(EquipmentSlot.OFF_HAND))
                        return;

                    Block clickBlock = playerInteractEvent.getClickedBlock();
                    if (clickBlock == null) {
                        return;
                    }
                    clickBlock.setType(Material.END_PORTAL);
                }
        );

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

        portalFrameWand = new Wand(plugin, "set-portal-frame", GuiUtil.formatItem("End Portal Frame Setter", Material.PRISMARINE_SHARD, 0), event -> {
            Block block = event.getClickedBlock();
            if (block != null && block.getBlockData() instanceof EndPortalFrame portalFrame) {
                Gui teamGui = new TeamGui(plugin, Component.text("Set which team?"), team -> null, teamManager.getParticipantTeams(), (player, team) -> {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            String message = String.format("%d rows updated.", database.upsertPortalFrame((ParticipantTeam) team, block.getLocation(), portalFrame.getFacing()));
                            player.sendMessage(TextUtil.formatText(message));
                        } catch (SQLException e) {
                            player.sendMessage(CommandUtils.errorMessage(e.getMessage()));
                            Bukkit.getLogger().warning(e.getMessage());
                        }
                    });
                }, new ButtonElement(Icons.exitItem(), HumanEntity::closeInventory));
                teamGui.openInventory(event.getPlayer());
            } else {
                event.getPlayer().sendMessage(CommandUtils.errorMessage("You did not click on a portal frame."));
            }
        });

        locationWand = new Wand(plugin, "java-location", GuiUtil.formatItem("Get Java Location", Material.PAPER, 12), event -> {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            if (block != null) {
                Location location;
                if (player.isSneaking()) {
                    BlockFace face = event.getBlockFace();
                    location = block.getLocation().add(face.getDirection());
                    player.sendMessage(TextUtil.formatText(String.format("Grabbed location on the %s side of the clicked block:", event.getBlockFace().name())));
                } else {
                    location = block.getLocation();
                    player.sendMessage(TextUtil.formatText("Grabbed location of the clicked block:"));
                }
                String text = String.format("new Location(Worlds.%s(), %d, %d, %d)", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                player.sendMessage(Component.text(text).color(Palette.SECONDARY.toTextColor())
                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy!")))
                        .clickEvent(ClickEvent.copyToClipboard(text)));
            }
        });

        entityLocationWand = new EntityWand(plugin, "entity-java-location", GuiUtil.formatItem("Get Entity Java Location", Material.PAPER, 12), event -> {
            Entity entity = event.getRightClicked();
            Player player = event.getPlayer();
            Location location;

            location = entity.getLocation();
            player.sendMessage(TextUtil.formatText("Grabbed location of the clicked entity:"));

            String text = String.format("new Location(Worlds.%s(), %fd, %fd, %fd, %ff, %ff)", location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            player.sendMessage(Component.text(text).color(Palette.SECONDARY.toTextColor())
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy!")))
                    .clickEvent(ClickEvent.copyToClipboard(text)));
        });

        setNPCs = new ButtonElement(GuiUtil.formatItem("Set Entities as NPC", Material.SKELETON_SPAWN_EGG, 0), player -> {
            ListGui npcGui = new ListGui(plugin, Component.text("Choose an NPC"), new ButtonElement(Icons.backItem(), player1 -> getGui(player1).openInventory(player1)));
            for (QuestCharacter character : CharacterManager.getCharacters()) {
                SpawnCharacterWand characterWand = new SpawnCharacterWand(plugin, character);
                npcGui.addElement(new ButtonElement(characterWand.getItem(), player1 -> player1.getInventory().addItem(characterWand.getItem())));
            }
            npcGui.openInventory(player);
        });

        testNPCWand = new EntityWand(plugin, "entity-test", GuiUtil.formatItem("Test wand for NPCs", Material.PAPER, 4), event -> {
            Entity entity = event.getRightClicked();
            Player player = event.getPlayer();

            if (!entity.hasMetadata("NPC")) {
                player.sendMessage(CommandUtils.errorMessage("Entity is not an NPC!"));
                return;
            }

            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setUseDisplayEntities(true);

            player.sendMessage(TextUtil.formatText("Hologram Trait set to use Text Display"));

        });

    }

    /**
     * Generates a random UUID for the given ItemStack and sets it as a unique identifier.
     *
     * @param itemStack The ItemStack to modify.
     * @return An ItemStack with a unique UUID.
     */
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

        gui.placeElement(1, 8, new ButtonElement(testNPCWand.getItem(), player -> player.getInventory().addItem(testNPCWand.getItem())));
        gui.placeElement(1, 9, setNPCs);
        gui.placeElement(2, 8, new ButtonElement(entityLocationWand.getItem(), player -> player.getInventory().addItem(entityLocationWand.getItem())));
        gui.placeElement(2, 9, new ButtonElement(locationWand.getItem(), player -> player.getInventory().addItem(locationWand.getItem())));

        gui.placeElement(3, 6, new ButtonElement(portalFrameWand.getItem(), player -> player.getInventory().addItem(portalFrameWand.getItem())));
        gui.placeElement(3, 7, new ButtonElement(waterWand.getItem(), player -> player.getInventory().addItem(waterWand.getItem())));
        gui.placeElement(3, 8, new ButtonElement(endPortalWand.getItem(), player -> player.getInventory().addItem(endPortalWand.getItem())));
        gui.placeElement(3, 9, new ButtonElement(endGatewayWand.getItem(), player -> player.getInventory().addItem(endGatewayWand.getItem())));
        return gui;
    }

}
