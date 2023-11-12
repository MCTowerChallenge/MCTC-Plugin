package io.github.mctowerchallenge.towerchallenge.quest.requirement;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class TagRequirement extends MaterialRequirement {

    private final Plugin plugin;
    private final String tag;

    public TagRequirement(Plugin plugin, Material material, String tag, int required) {
        super(material, required);
        this.plugin = plugin;
        this.tag = tag;
    }

    @Override
    public Requirement copy() {
        return new TagRequirement(plugin, getMaterial(), tag, getRequired());
    }

    @Override
    public boolean matchItem(ItemStack item) {
        return NBTUtils.boolState(plugin, tag, item);
    }

    @Override
    public ItemStack getRepresentation() {
        int remaining = getRemaining();
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getName());
        item.setItemMeta(itemMeta);
        Component itemName = TextUtil.noItalic("Nether Heart").append(TextUtil.formatText(String.format(" (%d/%d)", getCurrent(), getRequired())));
        if (remaining <= 0) {
            return GuiUtil.formatItem(itemName, Material.PAPER, 1);
        } else {
            ItemStack guiItem = GuiUtil.formatItem(itemName, getMaterial(), 0);
            guiItem.setAmount(remaining);
            return guiItem;
        }
    }
}
