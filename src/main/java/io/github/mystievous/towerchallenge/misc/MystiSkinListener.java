package io.github.mystievous.towerchallenge.misc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class MystiSkinListener {

    private static final UUID MYSTI_UUID = UUID.fromString("999c184d-b267-4cc1-a4ee-31aa5906964f");

    private Integer currentValue = null;

    public MystiSkinListener(TowerChallenge plugin) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();
                if (player.getUniqueId().equals(MYSTI_UUID)) {
                    int value = packet.getIntegers().read(1) >> 6 & 1;
                    if (currentValue == null || value != currentValue) {
                        currentValue = value;
                        if (value == 1) {
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.addPotionEffect(
                                    new PotionEffect(
                                            PotionEffectType.NIGHT_VISION,
                                            1000000,
                                            1,
                                            false,
                                            false,
                                            false)
                            ), 0);
                        } else {
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1f, 2f);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.removePotionEffect(PotionEffectType.NIGHT_VISION), 0);
                        }
                    }
                }
            }

        });

    }

}
