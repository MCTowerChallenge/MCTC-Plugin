package io.github.mystievous.towerchallenge.quest.requirement;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MaterialRequirement extends Requirement {

    private final Material material;

    /**
     * Requirement that needs a
     * specific material
     *
     * @param material The material to require
     * @param required How many of it are required
     */
    public MaterialRequirement(Material material, int required) {
        super(required);
        this.material = material;
    }

    @Override
    public Requirement copy() {
        return new MaterialRequirement(material, getRequired());
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public boolean matchItem(ItemStack item) {
        return item.getType().equals(material);
    }

    @Override
    public ItemStack getRepresentation() {
        int remaining = getRemaining();
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getName());
        item.setItemMeta(itemMeta);
        Component itemName = TextUtil.getItemName(item).append(TextUtil.formatText(String.format(" (%d/%d)", getCurrent(), getRequired())));
        if (remaining <= 0) {
            return GuiUtil.formatItem(itemName, Material.PAPER, 1);
        } else {
            ItemStack guiItem = GuiUtil.formatItem(itemName, getMaterial(), 0);
            guiItem.setAmount(remaining);
            return guiItem;
        }
    }
}
