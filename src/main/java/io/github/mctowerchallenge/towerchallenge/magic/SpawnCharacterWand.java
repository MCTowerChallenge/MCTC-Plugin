package io.github.mctowerchallenge.towerchallenge.magic;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.QuestCharacter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SpawnCharacterWand extends Wand {

    public static final String COOLDOWN_TAG = "cooldown";

    public SpawnCharacterWand(Plugin plugin, QuestCharacter character) {
        super(plugin, character.getTextName(), new ItemStack(Material.STICK) {{
            ItemMeta meta = getItemMeta();
            meta.displayName(TextUtil.noItalic(character.getDisplayName()));
            String region = character.getWorldguardRegion();
            List<Component> lore;
            if (region == null) {
                lore = TextUtil.formatTexts("No Worldguard Region");
            } else {
                lore = TextUtil.formatTexts(String.format("Worldguard Region: %s", region));
            }
            meta.lore(lore);
            NBTUtils.setBool(plugin, COOLDOWN_TAG, meta, false);
            setItemMeta(meta);
        }}, playerInteractEvent -> {
            ItemStack item = playerInteractEvent.getItem();
            if (item == null)
                return;

            if (NBTUtils.boolState(plugin, COOLDOWN_TAG, item)) {
                return;
            }

            Location interactionPoint = playerInteractEvent.getInteractionPoint();
            if (!playerInteractEvent.hasBlock() || interactionPoint == null) {
                return;
            }

            character.createNPC().spawn(interactionPoint);
            NBTUtils.setBool(plugin, COOLDOWN_TAG, item, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    NBTUtils.setBool(plugin, COOLDOWN_TAG, item, false);
                }
            }.runTaskLater(plugin, 10L);
        });
    }



}
