package io.github.mctowerchallenge.towerchallenge.quest.util;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.Palette;
import io.github.mctowerchallenge.towerchallenge.TowerChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utility class for Block Vouchers
 */
public class BlockVoucher {

    public static final String VOUCHER_TAG = "block-voucher";
    public static final Component VOUCHER_NAME = Component.text("Block Voucher").decoration(TextDecoration.ITALIC, false).color(Palette.PRIMARY.toTextColor());

    /**
     * Gets a certain number of Block Voucher items in game
     *
     * @param number The amount of items to get
     * @return The stack of vouchers
     */
    public static ItemStack getVouchers(int number) {
        ItemStack vouchers = new ItemStack(Material.PAPER, number);
        ItemMeta voucherMeta = vouchers.getItemMeta();
        voucherMeta.displayName(VOUCHER_NAME);
        voucherMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        voucherMeta.addEnchant(Enchantment.MENDING, 1, false);
        NBTUtils.setBool(TowerChallenge.getInstance(), VOUCHER_TAG, voucherMeta, true);
        vouchers.setItemMeta(voucherMeta);
        return vouchers;
    }

    public static boolean isVoucher(ItemStack itemStack) {
        return NBTUtils.boolState(TowerChallenge.getInstance(), VOUCHER_TAG, itemStack.getItemMeta());
    }



}
