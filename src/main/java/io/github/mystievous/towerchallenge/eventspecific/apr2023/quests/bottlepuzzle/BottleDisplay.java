package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.bottlepuzzle;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.Apr2023QuestInstance;
import io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.Apr2023QuestManager;
import io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.BadCellar;
import io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.GoodCellar;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class BottleDisplay implements Listener {

    private final CommandSender sender = Bukkit.createCommandSender(component -> {
    });

    // -748 114 -2568

    /**
     * All options for bottle colors
     */
    public enum BottleColor {
        RED("Red Bottle", new Color(0x9e0303)),
        ORANGE("Orange Bottle", new Color(0xd88106)),
        YELLOW("Yellow Bottle", new Color(0xb5ac0c)),
        GREEN("Green Bottle", new Color(0x278e0e)),
        BLUE("Blue Bottle", new Color(0x1481ce)),
        PURPLE("Purple Bottle", new Color(0xa014ce)),
        BLACK("Black Bottle", new Color(0x262626));

        private final String label;
        private final Color color;

        BottleColor(String label, Color color) {
            this.label = label;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public Color getColor() {
            return color;
        }
    }

    /**
     * Offset from the bad cellar to the good cellar,
     * so that the bottles can be placed in both and
     * changed at the same time
     */
    public static final Vector goodOffset = GoodCellar.basePotionTeleport.clone().subtract(BadCellar.basePotionEnterDestination.clone()).toVector();


    private static final Material MATERIAL = Material.POTION;
    private static final int CUSTOM_MODEL = 1;

    /**
     * Generates the 3d modelled potion for the given color
     *
     * @param color The color for the potion
     * @return A potion item with the 3d custom model and the given color
     */
    private static @Nullable ItemStack createPotion(@Nullable Color color) {
        if (color == null) {
            return null;
        }
        ItemStack potion = new ItemStack(MATERIAL);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setColor(color.toBukkitColor());
        meta.setCustomModelData(CUSTOM_MODEL);
        potion.setItemMeta(meta);
        return potion;
    }

    public static final String COLOR_TAG = "potion-color";

    /**
     * Generates the regular potion item for the given color
     *
     * @param plugin Instance of the plugin
     * @param color  The color for the potion
     * @return Vanilla potion item with the given color.
     */
    public static @Nullable ItemStack createPotionItem(Plugin plugin, @Nullable BottleColor color) {
        if (color == null) {
            return null;
        }
        ItemStack potion = GuiUtil.formatItem(color.getLabel(), MATERIAL, 0);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setColor(color.getColor().toBukkitColor());
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES);
        potion.setItemMeta(meta);
        NBTUtils.setString(plugin, COLOR_TAG, potion, color.name());
        NBTUtils.setNoUse(plugin, potion);
        TextUtil.appendQuestItemLore(potion);
        return potion;
    }

    public static final String GENERIC_TAG = "apr2023_bottle_display";

    private final Plugin plugin;
    private final BottleManager manager;
    private final TowerTeam team;
    private final int number;
    private final String tag;
    private final Location location;
    private Interaction interaction;
    private ItemDisplay bottle;
    private ItemDisplay goodBottle;
    private BlockDisplay display;
    private BlockDisplay goodDisplay;
    private String label;
    private BottleColor color;

    /**
     * @param plugin   Current plugin instance
     * @param manager  Bottle Manager this belongs to
     * @param team     Team this belongs to
     * @param number   Number in the bottle sequence
     * @param location Location for the bottle to be generated at
     */
    public BottleDisplay(Plugin plugin, BottleManager manager, TowerTeam team, int number, Location location) {
        this.plugin = plugin;
        this.manager = manager;
        this.team = team;
        this.number = number;
        this.tag = String.format("%s-T%s-%d", GENERIC_TAG, team.getDatabaseId(), number);

        this.location = location;

        this.color = null;

        reset();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * @param plugin   Current plugin instance
     * @param manager  Bottle Manager this belongs to
     * @param team     Team this belongs to
     * @param number   Number in the bottle sequence
     * @param location Location for the bottle to be generated at
     * @param color    Color to initialize a bottle in the display with
     */
    public BottleDisplay(Plugin plugin, BottleManager manager, TowerTeam team, int number, Location location, @Nullable BottleColor color) {
        this(plugin, manager, team, number, location);
        setColor(color);
    }

    public Location getLocation() {
        return location;
    }

    public @Nullable BottleDisplay.BottleColor getColor() {
        return color;
    }

    /**
     * Sets the color of the bottle in the display
     *
     * @param color The color to set the display, or null to empty it.
     */
    public void setColor(@Nullable BottleDisplay.BottleColor color) {
        if (color == null) {
            this.color = null;
            label = null;
            bottle.setItemStack(null);
            goodBottle.setItemStack(null);
        } else {
            this.color = color;
            label = color.getLabel();
            ItemStack potion = createPotion(color.getColor());
            bottle.setItemStack(potion);
            goodBottle.setItemStack(potion);
        }
    }

    public void reset() {
        unload();
        this.interaction = (Interaction) location.getWorld().spawnEntity(location, EntityType.INTERACTION);
        this.interaction.addScoreboardTag(tag);
        this.interaction.addScoreboardTag(GENERIC_TAG);
        this.interaction.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        this.interaction.setInteractionWidth(0.7f);

        this.bottle = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        this.bottle.addScoreboardTag(tag);
        this.bottle.addScoreboardTag(GENERIC_TAG);
        this.bottle.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        this.bottle.setTransformation(new Transformation(
                new Vector3f(0.0f, 0.55f, 0.0f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f),
                new Vector3f(1f, 1f, 1f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f)
        ));

        this.goodBottle = (ItemDisplay) location.getWorld().spawnEntity(location.clone().add(goodOffset), EntityType.ITEM_DISPLAY);
        goodBottle.addScoreboardTag(tag);
        goodBottle.addScoreboardTag(GENERIC_TAG);
        goodBottle.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        goodBottle.setTransformation(bottle.getTransformation());

        this.display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
        this.display.addScoreboardTag(tag);
        this.display.addScoreboardTag(GENERIC_TAG);
        this.display.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        this.display.setTransformation(new Transformation(
                new Vector3f(-0.35f, -0.3f, -0.35f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f),
                new Vector3f(0.7f, 0.7f, 0.7f),
                new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f)
        ));
        BlockData slab = Bukkit.createBlockData(Material.SPRUCE_SLAB);
        this.display.setBlock(slab);

        goodDisplay = (BlockDisplay) location.getWorld().spawnEntity(location.clone().add(goodOffset), EntityType.BLOCK_DISPLAY);
        goodDisplay.addScoreboardTag(tag);
        goodDisplay.addScoreboardTag(GENERIC_TAG);
        goodDisplay.addScoreboardTag(Apr2023QuestManager.REMOVE_TAG);
        goodDisplay.setTransformation(display.getTransformation());
        goodDisplay.setBlock(slab);

        setColor(color);
    }

    public void unload() {
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", tag));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        EquipmentSlot hand = event.getHand();

        if (hand == EquipmentSlot.HAND && entity.getScoreboardTags().contains(tag) && entity instanceof Interaction) {
            ItemStack item = player.getInventory().getItem(hand);
            String color = NBTUtils.getString(plugin, COLOR_TAG, item);
            if (item.getType() == Material.POTION && color != null) {

                BottleColor prevColor = getColor();

                setColor(BottleColor.valueOf(color));

                player.getInventory().setItem(hand, createPotionItem(plugin, prevColor));

            } else if (item.getType().isAir()) {
                BottleColor prevColor = getColor();
                player.getInventory().setItem(hand, createPotionItem(plugin, prevColor));
                setColor(null);
            } else {
                player.sendMessage(CommandUtils.errorMessage("Please use an empty hand or a colored bottle."));
            }
            manager.checkBottles();
        }

    }

}
