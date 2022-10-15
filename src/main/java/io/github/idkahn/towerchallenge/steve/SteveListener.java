package io.github.idkahn.towerchallenge.steve;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.candy.CandyUtils;
import io.github.idkahn.towerchallenge.commands.CommandUtils;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

public class SteveListener implements Listener {

    public final static String STEVE_NAME = "steve skellington";
    public final static String STEVE_REGION_NAME = "steve_house";
    public final static World STEVE_WORLD = Bukkit.getWorld("world");

    private ProtectedRegion steveHouse;
    private SteveManager steveManager;

    private Dialogue notEnoughCandy;
    private Dialogue enoughCandy;

    public SteveListener(SteveManager steveManager) {
        this.steveManager = steveManager;
        Bukkit.getServer().getPluginManager().registerEvents(this, steveManager.getEventManager().getPlugin());
        steveHouse = TowerTeam.container.get(BukkitAdapter.adapt(STEVE_WORLD)).getRegion(STEVE_REGION_NAME);
        notEnoughCandy = new Dialogue(steveManager, Component.text("You haven't found all 32 candies yet! Come back when you have.").color(NamedTextColor.GRAY), 3);
        notEnoughCandy.setNext(new Dialogue(steveManager, Component.text("What do you mean “...32?” ??? Yes! Get to trick or treating already!").color(NamedTextColor.GRAY), 3))
                .setNext(new Dialogue(steveManager, Component.text("Now where was I? Oh yeah, maybe over here?").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true), 0))
                .setCallback((player) -> {
                    TowerTeam team = steveManager.getEventManager().getTowerListener().getPlayerTeam(player);
                    if (team != null) {
                        steveManager.setTeamStage(team, 2);
                    }
                });

        enoughCandy = new Dialogue(steveManager, Component.text("Amazing! I found the riddle! Here you go!").color(NamedTextColor.GRAY), 3);
        enoughCandy.setNext(new Dialogue(steveManager, Component.text("Oh don't worry about carrying all that candy around I’ll just take those :)").color(NamedTextColor.GRAY), 3))
                .setNext(new Dialogue(steveManager, Component.text("Happy Halloween! and have fun at MCTC… I guess").color(NamedTextColor.GRAY), 3))
                .setCallback((player) -> {
                    TowerTeam team = steveManager.getEventManager().getTowerListener().getPlayerTeam(player);
                    if (team != null) {
                        steveManager.setTeamStage(team, 3);
                    }
                });
    }

    private boolean isSteve(Entity entity) {
        String entityName = PlainTextComponentSerializer.plainText().serialize(entity.name());
        return entity.getType().equals(EntityType.SKELETON) && entityName.equalsIgnoreCase(STEVE_NAME);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        TowerTeam team = steveManager.getEventManager().getTowerListener().getPlayerTeam(player);
        if (steveManager.getEventManager().getQuestManager().getStage() == 2) {
            if (team != null) {
                if (steveHouse.contains(BukkitAdapter.adapt(event.getTo()).toVector().toBlockPoint()) && steveManager.getTeamStage(team) == 0) {
                    steveManager.setTeamStage(team, 1);
                    steveManager.playDialogue(player);

                    ComponentBuilder message = Component.text().decoration(TextDecoration.ITALIC, true).color(TowerChallenge.PRIMARY_COLOR);
                    message.append(Component.text("steve was found "));
                    message.append(Component.text("by ").append(player.name()));
                    message.append(Component.text(" [Teleport]").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
                            .clickEvent(ClickEvent.runCommand("/tp "+player.getName()))
                    );

                    steveManager.getEventManager().getTowerListener().getGodTeam().getAudience().sendMessage(message.build());
    //                player.sendMessage(message.build());
    //                event.getPlayer().sendMessage("You found steve!");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        if (event.isCancelled())
            return;


        if (isSteve(event.getRightClicked())) {
            Player player = event.getPlayer();
            TowerTeam team = steveManager.getEventManager().getTowerListener().getPlayerTeam(player);
            if (event.getHand().equals(EquipmentSlot.HAND)) {
                if (team != null) {
                    if (steveManager.getTeamStage(team) == 2) {
    //                    team.getAudience().sendMessage(Component.text("Right clicked after dialogue"));
                        ItemStack heldItem = player.getInventory().getItem(event.getHand());
                        if (heldItem.getType().equals(Material.BUNDLE)) {
                            BundleMeta bundleMeta = (BundleMeta) heldItem.getItemMeta();
                            if (CandyUtils.getTeam(heldItem).equals(team.getTeam().getName())) {
                                int candyCount = 0;
                                for (ItemStack item : bundleMeta.getItems()) {
                                    if (CandyUtils.isCandy(item)) {
                                        candyCount += item.getAmount();
                                    }
                                }
                                if (candyCount >= 32) {
                                    player.getInventory().setItem(event.getHand(), null);
                                    player.getInventory().addItem(steveManager.getRiddle());
                                    enoughCandy.play(player);
                                } else {
                                    notEnoughCandy.play(player);
                                }
                            } else {
                                player.sendMessage(Dialogue.steveMessage("Hey, that's not your candy basket! I don't know how you got it but you should really give that back."));
                            }
                        } else {
                            player.sendMessage(Dialogue.steveMessage("Talk to me with your Candy Basket so I can take a look at it!"));
                        }
                    } else if (steveManager.getTeamStage(team) == 3) {
                        player.sendMessage(Dialogue.steveMessage("Happy Halloween! and have fun at MCTC… I guess"));
                    }

                }
            }
        }

    }

    @EventHandler
    public void onEntityPath(final EntityPathfindEvent event) {
        if (event.isCancelled())
            return;

        Entity entity = event.getEntity();
        Location location = event.getLoc();

        if (isSteve(entity)) {
            if (!steveHouse.contains(BukkitAdapter.adapt(location).toVector().toBlockPoint())) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onEntityTarget(final EntityTargetEvent event) {
        if (event.isCancelled())
            return;

        event.setCancelled(isSteve(event.getEntity()));
    }

}
