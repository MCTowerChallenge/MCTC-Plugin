package io.github.mystievous.towerchallenge.decoration.presents;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.NBTUtils;
import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.quests.entities.ItemEntityHandler;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PresentEntityHandler extends ItemEntityHandler {

    public static final String PRESENT_TAG = "present";

    public static final List<ItemStack> presents = new ArrayList<>(){{
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

    public static ItemStack getPresentItem() {
        ItemStack present = NBTUtils.setBool(PRESENT_TAG, new ItemStack(Material.SCUTE));
        ItemMeta meta = present.getItemMeta();
        meta.setCustomModelData(10);
        meta.displayName(TextUtil.noItalic("Present"));
        present.setItemMeta(meta);
        return present;
    }

    public static ItemStack getPresent(ItemStack present, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) present.getItemMeta();
        meta.setColor(color);
        meta.displayName(TextUtil.noItalic("Present"));
        present.setItemMeta(meta);
        return present;
    }

    public static ItemStack getPresent() {
        int index = RANDOM.nextInt(presents.size()-1);
        ItemStack present = presents.get(index).clone();
        Color color = Color.fromRGB(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255));
        return getPresent(present, color);
    }

    public static ArmorStand summonPresent(Location location) {
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

    public static ArmorStand summonPresent(Player player) {
        return summonPresent(player.getLocation().add(0, -1.38, 0));
    }

    public PresentEntityHandler(ChallengeManager challengeManager) {
        super(challengeManager, PRESENT_TAG, null, getPresent());
    }

    @Override
    public ArmorStand summonArmorStand(Location location) {
        ArmorStand armorStand = super.summonArmorStand(location);
        armorStand.setItem(EquipmentSlot.HEAD, getPresent());
        return armorStand;
    }

    @Override
    public ArmorStand summonArmorStand(Player player) {
        Location location = player.getLocation().add(0, 1.25, 0);
        return summonArmorStand(location);
    }

    @Override
    public ItemStack getItem(TowerTeam team, Entity entity) {
        return getPresentItem();
    }

    @EventHandler
    public void clickInInventory(final InventoryClickEvent event) {

        if (event.isCancelled())
            return;
        if (event.getView().getTopInventory() instanceof AbstractHorseInventory) {
            if (NBTUtils.boolState(PRESENT_TAG, event.getCurrentItem())) {
                event.setCancelled(true);
            }
        }

    }

}
