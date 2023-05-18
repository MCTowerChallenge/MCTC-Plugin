package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.quests.QuestItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class NetherHeart {

    public static final String NETHER_HEART = "nether-heart";
    public static final Component NAME = Component.text("Nether Heart");

    private final ShapedRecipe netherHeartRecipe;

    public NetherHeart(Plugin plugin) {


        ItemStack netherHeart = GuiUtil.formatItem(NAME, Material.NETHER_STAR, 0);
        TextUtil.appendQuestItemLore(netherHeart);
        NBTUtils.setNoUse(plugin, netherHeart);
        NBTUtils.setBool(plugin, NETHER_HEART, netherHeart);

        netherHeartRecipe = new ShapedRecipe(new NamespacedKey(plugin, "nether_heart"), netherHeart);
        netherHeartRecipe.shape("TQT",
                "QGQ",
                "TQT");
        netherHeartRecipe.setIngredient('T', Material.GHAST_TEAR);
        netherHeartRecipe.setIngredient('Q', Material.QUARTZ);
        netherHeartRecipe.setIngredient('G', Material.GLOWSTONE);
        Bukkit.getServer().addRecipe(netherHeartRecipe);

        QuestItems.putItem(NETHER_HEART, netherHeart);

    }

    public ShapedRecipe getRecipe() {
        return netherHeartRecipe;
    }
}
