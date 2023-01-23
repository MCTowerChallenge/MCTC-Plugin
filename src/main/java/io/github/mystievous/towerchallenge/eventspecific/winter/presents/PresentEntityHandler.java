package io.github.mystievous.towerchallenge.eventspecific.winter.presents;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.quests.entities.ItemEntityHandler;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PresentEntityHandler extends ItemEntityHandler {

    public static final String PRESENT_TAG = "present";

    /**
     * Presets of present types
     */
    public static final List<ItemStack> PRESENTS = new ArrayList<>() {{
        // tall
        add(NBTUtils.setBool(PRESENT_TAG, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
            ItemMeta meta = getItemMeta();
            meta.setCustomModelData(1001);
            setItemMeta(meta);
        }}));

        // wide
        add(NBTUtils.setBool(PRESENT_TAG, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
            ItemMeta meta = getItemMeta();
            meta.setCustomModelData(1002);
            setItemMeta(meta);
        }}));

        // box
        add(NBTUtils.setBool(PRESENT_TAG, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
            ItemMeta meta = getItemMeta();
            meta.setCustomModelData(1003);
            setItemMeta(meta);
        }}));

        // flat
        add(NBTUtils.setBool(PRESENT_TAG, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
            ItemMeta meta = getItemMeta();
            meta.setCustomModelData(1004);
            setItemMeta(meta);
        }}));
    }};

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Gets a present to put in a player's inventory.
     *
     * @return The present item
     */
    public static @NotNull ItemStack getPresentItem() {
        ItemStack present = NBTUtils.setBool(PRESENT_TAG, new ItemStack(Material.SCUTE));
        ItemMeta meta = present.getItemMeta();
        meta.setCustomModelData(10);
        meta.displayName(TextUtil.noItalic("Present"));
        present.setItemMeta(meta);
        return present;
    }

    /**
     * Gets a horse armor present to
     * put on an armor stand
     *
     * @param present The present preset of the model you want.
     * @param color   The color of the resulting present.
     * @return The present.
     */
    @Contract("_, _ -> param1")
    public static @NotNull ItemStack getPresent(@NotNull ItemStack present, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) present.getItemMeta();
        meta.setColor(color);
        meta.displayName(TextUtil.noItalic("Present"));
        present.setItemMeta(meta);
        return present;
    }

    /**
     * Gets a horse armor present with
     * a random type and color to put
     * on an armor stand.
     *
     * @return The present
     */
    public static @NotNull ItemStack getPresent() {
        int index = RANDOM.nextInt(PRESENTS.size() - 1);
        ItemStack present = PRESENTS.get(index).clone();
        Color color = Color.fromRGB(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255));
        return getPresent(present, color);
    }

    /**
     * Summons an armor stand with a random present
     * on its head, and sets it as a usable
     *
     * @param location The location to summon the armor stand.
     * @return The summoned armor stand.
     */
    public static @NotNull ArmorStand summonPresent(@NotNull Location location) {
        World world = location.getWorld();
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND, false);
        armorStand.setItem(EquipmentSlot.HEAD, getPresent());
        armorStand.addScoreboardTag(PRESENT_TAG);
        armorStand.setGravity(false);
        armorStand.setInvisible(true);
        armorStand.addDisabledSlots(EquipmentSlot.values());
        armorStand.setInvulnerable(true);
        return armorStand;
    }

    /**
     * Summons a present armor stand at the player's
     * feet with a random present on it's head, and
     * sets it as a usable.
     *
     * @param player The player at which to summon the armor stand.
     * @return The summoned armor stand.
     */
    public static @NotNull ArmorStand summonPresent(@NotNull Player player) {
        return summonPresent(player.getLocation().add(0, -1.38, 0));
    }

    public PresentEntityHandler(TeamManager teamManager) {
        super(teamManager, PRESENT_TAG, null, getPresentItem());
    }

    /**
     * Summons an armor stand with a random present
     * on its head, and sets it as a usable
     *
     * @param location The location to summon the armor stand.
     * @return The summoned armor stand.
     */
    @Override
    public @NotNull ArmorStand summonArmorStand(@NotNull Location location) {
        return summonPresent(location);
    }

    /**
     * Summons a present armor stand at the player's
     * feet with a random present on it's head, and
     * sets it as a usable.
     *
     * @param player The player at which to summon the armor stand.
     * @return The summoned armor stand.
     */
    @Override
    public @NotNull ArmorStand summonArmorStand(@NotNull Player player) {
        return summonPresent(player);
    }

    /**
     * Disallows PRESENTS from being interacted
     * with in a horse's inventory
     */
    @EventHandler
    public void clickInInventory(final @NotNull InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (event.getView().getTopInventory() instanceof AbstractHorseInventory) {
            if (NBTUtils.boolState(PRESENT_TAG, event.getCurrentItem())) {
                event.setCancelled(true);
            }
        }

    }

}
