package io.github.mystievous.towerchallenge.misc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
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

//        manager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                Player player = event.getPlayer();
//                if (player.getUniqueId().equals(MYSTI_UUID) && currentValue != null && currentValue == 1) {
//                    int entityId = event.getPacket().getIntegers().read(0);
//                    if (Bukkit.getOnlinePlayers().stream().anyMatch(checkPlayer -> checkPlayer.getEntityId() == entityId)) {
//                        if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) {
//                            List<WrappedWatchableObject> watchableObjectList = event.getPacket().getWatchableCollectionModifier().read(0);
//                            watchableObjectList.stream().filter(object -> object.getIndex() == 0).findFirst().ifPresent(watchableObject -> {
//                                byte b = (byte) watchableObject.getValue();
//                                b |= 0b01000000;
//                                watchableObject.setValue(b);
//                            });
//                        } else {
//                            WrappedDataWatcher watcher = event.getPacket().getDataWatcherModifier().read(0);
//                            if (watcher.hasIndex(0)) {
//                                byte b = watcher.getByte(0);
//                                b |= 0b01000000;
//                                watcher.setObject(0, b);
//                            }
//                        }
//                    }
//                }
//            }
//
//        });

    }

}
