package io.github.mystievous.towerchallenge.gods.godgui.regionteleports;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.ListGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldIndividualGui extends ListGui {

    private static @NotNull ItemStack candyItem() {
        ItemStack candy = new ItemStack(Material.SCUTE);
        ItemMeta meta = candy.getItemMeta();
        meta.setCustomModelData(2);
        meta.displayName(Component.text("Candy Village").decoration(TextDecoration.ITALIC, false));
        candy.setItemMeta(meta);
        return candy;
    }

    private static @NotNull ItemStack spawnItem() {
        ItemStack ice = new ItemStack(Material.PACKED_ICE);
        ItemMeta meta = ice.getItemMeta();
        meta.displayName(Component.text("Spawn").decoration(TextDecoration.ITALIC, false));
        ice.setItemMeta(meta);
        return ice;
    }

    private static @NotNull ItemStack steveItem() {
        ItemStack skull = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = skull.getItemMeta();
        meta.setCustomModelData(1);
        meta.displayName(Component.text("steve skellington").decoration(TextDecoration.ITALIC, false));
        skull.setItemMeta(meta);
        return skull;
    }

    private static @NotNull ItemStack pixieItem() {
        ItemStack hat = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        LeatherArmorMeta meta = (LeatherArmorMeta) hat.getItemMeta();
        meta.setCustomModelData(1);
        meta.displayName(Component.text("Pixie's Hut").decoration(TextDecoration.ITALIC, false));
        meta.setColor(Color.fromRGB(0xed12d0));
        hat.setItemMeta(meta);
        return hat;
    }

    private static @NotNull ItemStack netherPortalItem() {
        ItemStack obsidian = new ItemStack(Material.CRYING_OBSIDIAN);
        ItemMeta meta = obsidian.getItemMeta();
        meta.setCustomModelData(0);
        meta.displayName(Component.text("Nether Portal").decoration(TextDecoration.ITALIC, false));
        obsidian.setItemMeta(meta);
        return obsidian;
    }

    private static @NotNull ItemStack commandBlockItem() {
        ItemStack commandBlock = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = commandBlock.getItemMeta();
        meta.displayName(Component.text("Command Blocks/Particles").decoration(TextDecoration.ITALIC, false));
        commandBlock.setItemMeta(meta);
        return commandBlock;
    }

    /**
     * All items that represent world regions
     */
    private static final Map<String, ItemStack> items = new HashMap<>() {{
        put("spawn", spawnItem());
        put("steve", steveItem());
        put("candy-village", candyItem());
        put("pixie-hut", pixieItem());
        put("command-blocks", commandBlockItem());
        put("nether-portal", netherPortalItem());
    }};

    /**
     * Shows preset regions for a specific world
     *
     * @param world         The world to show
     * @param regionManager The region manager of the world
     * @param name          Name for the GUI
     * @param exitElement   Element to use for exiting the GUI
     */
    public WorldIndividualGui(World world, RegionManager regionManager, Component name, Element exitElement) {
        super(name, new ArrayList<>(), exitElement);

        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            String key = entry.getKey();
            if (regionManager.hasRegion(key)) {
                ProtectedRegion region = regionManager.getRegion(key);
                if (region != null) {
                    this.addElement(new ButtonElement(entry.getValue(), player -> {
                        com.sk89q.worldedit.util.Location teleLoc = region.getFlag(Flags.TELE_LOC);
                        if (teleLoc != null) {
                            Location location = BukkitAdapter.adapt(teleLoc);
                            player.teleport(location);
                        } else {
                            BlockVector3 blockVector3 = region.getMaximumPoint().subtract(region.getMinimumPoint()).divide(2).add(region.getMinimumPoint());
                            Location location = new Location(world, blockVector3.getX(), blockVector3.getY(), blockVector3.getZ());
                            player.setGameMode(GameMode.SPECTATOR);
                            player.teleport(location);
                        }
                    }));
                }
            }
        }

    }
}
