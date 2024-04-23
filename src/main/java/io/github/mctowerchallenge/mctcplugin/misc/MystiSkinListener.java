package io.github.mctowerchallenge.mctcplugin.misc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

public class MystiSkinListener {

    private static final UUID MYSTI_UUID = UUID.fromString("999c184d-b267-4cc1-a4ee-31aa5906964f");

    private Integer currentValue = null;

    private void visorOn(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f);
        player.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.NIGHT_VISION,
                        1000000,
                        1,
                        false,
                        false,
                        false)
        );
    }

    private void visorOff(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1f, 2f);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public MystiSkinListener(MCTCPlugin plugin) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();
//                Bukkit.getServer().sendMessage(Component.text(packet.getBytes().getValues().toString()));
                packet.getPlayerInfoDataLists().getValues();
                if (player.getUniqueId().equals(MYSTI_UUID) && packet.getIntegers().size() > 0) {
                    int value = packet.getIntegers().read(1) >> 6 & 1;
                    if (currentValue == null || value != currentValue) {
                        currentValue = value;
                        if (value == 1) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> visorOn(player), 0);
                        } else {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> visorOff(player), 0);
                        }
                    }
                }
            }

        });

//        manager.addPacketListener(new PacketAdapter(
//                plugin,
//                ListenerPriority.NORMAL,
//                PacketType.Play.Server.NAMED_SOUND_EFFECT
//        ) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                if (!event.getPlayer().getWorld().getName().equals(Worlds.May2024().getName())) {
//                    return;
//                }
//
//                PacketContainer packet = event.getPacket();
//                List<Sound> sounds = packet.getSoundEffects().getValues();
//                for (Sound sound : sounds) {
//                    if (sound != null && sound.equals(Sound.BLOCK_BEEHIVE_WORK)) {
//                        event.setCancelled(true);
//                        return;
//                    }
//                }
//            }
//        });

    }

}
