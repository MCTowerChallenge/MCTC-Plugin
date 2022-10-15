package io.github.idkahn.towerchallenge.quests;

import io.github.idkahn.towerchallenge.TowerChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockVoucher extends ItemStack {
    
    public static final Component VOUCHER_NAME = Component.text("Block Voucher").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR);

    public static ItemStack getVouchers(int number) {
        ItemStack vouchers = QuestUtil.setVoucher(new ItemStack(Material.PAPER, number));
        ItemMeta voucherMeta = vouchers.getItemMeta();
        voucherMeta.displayName(VOUCHER_NAME);
        voucherMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        voucherMeta.addEnchant(Enchantment.MENDING, 1, false);
        vouchers.setItemMeta(voucherMeta);
        return vouchers;
    }
    
    ItemStack voucherItem;

    public BlockVoucher(String name, int vouchers) {
        super(Material.PAPER);
        ItemMeta itemMeta = getItemMeta();
        if (name != null) {
            itemMeta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        } else {
            itemMeta.displayName(VOUCHER_NAME);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.MENDING, 1, false);
        setItemMeta(itemMeta);
        voucherItem = getVouchers(vouchers);
    }

    public ItemStack getVoucherItem() {
        return voucherItem;
    }
}
