package io.github.idkahn.towerchallenge.other;

import io.github.idkahn.towerchallenge.EventManager;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class PeacefulListener implements Listener {

    public PeacefulListener(EventManager eventManager) {
        Bukkit.getServer().getPluginManager().registerEvents(this, eventManager.getPlugin());
    }

    @EventHandler
    public void onEntityTarget(final EntityTargetEvent event) {
        String entityName = PlainTextComponentSerializer.plainText().serialize(event.getEntity().name());
        if (event.getEntity().getType().equals(EntityType.SKELETON) && entityName.equalsIgnoreCase("steve skellington")) {
            event.setCancelled(true);
        }
    }

}
