package io.github.mystievous.towerchallenge.quest.util;

import io.github.mystievous.mysticore.Palette;
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
public class BlockVoucher extends ItemStack {

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
        vouchers.setItemMeta(voucherMeta);
        return vouchers;
    }

}
