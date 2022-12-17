package io.github.mystievous.towerchallenge.gods.godgui.teamregionsetups;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.gods.GodManager;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.Openable;
import io.github.mystievous.towerchallenge.gui.page.PresetGui;
import io.github.mystievous.towerchallenge.gui.page.TeamGui;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import io.github.mystievous.towerchallenge.towering.regions.GingerbreadRegion;
import io.github.mystievous.towerchallenge.towering.regions.SpawnRegion;
import io.github.mystievous.towerchallenge.towering.regions.TowerRegion;
import io.github.mystievous.towerchallenge.towering.TowerListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RegionListGui implements Openable {

    private GodManager godManager;
    private TowerListener towerListener;

    public RegionListGui(GodManager godManager, TowerListener towerListener) {
        this.godManager = godManager;
        this.towerListener = towerListener;
    }

    @Override
    public Gui getGui(Player player) {
        TeamGui regionGui = new TeamGui(Component.text("Team to set:"), participantTeam -> {
            TowerRegion towerRegion = participantTeam.getTowerRegion();
            SpawnRegion spawnRegion = participantTeam.getSpawnRegion();
            GingerbreadRegion gingerbreadRegion = participantTeam.getGingerbreadRegion();

            List<Component> lore = new ArrayList<>(
                    TextUtil.formatTexts(
                            String.format("Tower: %s", towerRegion == null ? "No Region" : towerRegion.getId()),
                            String.format("Spawn: %s", spawnRegion == null ? "No Region" : spawnRegion.getId()),
                            String.format("Gingerbread: %s", gingerbreadRegion == null ? "No Region" : gingerbreadRegion.getId())
                    )
            );

            return lore;

        }, towerListener.getTeams().values().stream().toList(), (player1, participantTeam) -> {
            PresetGui gui = new PresetGui(Component.text(participantTeam.getTextName()), 3);

            RegionManager spawnWorldManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(ParticipantTeam.getSpawnWorld()));
            ItemStack spawnItem = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta spawnMeta = spawnItem.getItemMeta();
            spawnMeta.displayName(Component.text("Spawn Region"));
            spawnMeta.lore(new ArrayList<>(){{
                add(TextUtil.formatText(participantTeam.getSpawnRegion() != null ? "Redefine Region" : "Define Region"));
            }});
            spawnItem.setItemMeta(spawnMeta);
            ButtonElement spawnElement = new ButtonElement(spawnItem, player2 -> {
                if (spawnWorldManager.hasRegion(participantTeam.getSpawnName())) {
//                    player2.sendMessage(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player2.performCommand(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                } else {
//                    player2.sendMessage(String.format("rg define -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player2.performCommand(String.format("rg define -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                }
                if (spawnWorldManager.hasRegion(participantTeam.getSpawnName())) {
                    if (spawnWorldManager.hasRegion("spawn")) {
                        try {
                            spawnWorldManager.getRegion(participantTeam.getSpawnName()).setParent(spawnWorldManager.getRegion("spawn"));
                        } catch (ProtectedRegion.CircularInheritanceException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    player2.sendMessage(Component.text("Error creating region, "+participantTeam.getSpawnName()));
                }
                player2.performCommand(String.format("rg setpriority -w \"%s\" %s 1", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                participantTeam.loadRegions();
                participantTeam.centerRegions(player2.getLocation().getY());
                player2.closeInventory();
                player2.performCommand(String.format("rg info -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
            });
            gui.placeElement(3, 2, spawnElement);

            RegionManager towerWorldManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(ParticipantTeam.getTowerWorld()));
            ItemStack towerItem = new ItemStack(Material.BRICKS);
            ItemMeta towerMeta = towerItem.getItemMeta();
            towerMeta.displayName(Component.text("Tower Region"));
            towerMeta.lore(new ArrayList<>(){{
                add(TextUtil.formatText(participantTeam.getTowerRegion() != null ? "Redefine Region" : "Define Region"));
            }});
            towerItem.setItemMeta(towerMeta);
            ButtonElement towerElement = new ButtonElement(towerItem, player2 -> {
                if (towerWorldManager.hasRegion(participantTeam.getTowerName())) {
//                    player1.sendMessage(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player2.performCommand(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getTowerWorld().getName(), participantTeam.getTowerName()));
                } else {
//                    player1.sendMessage(String.format("rg define -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player2.performCommand(String.format("rg define -w \"%s\" %s", ParticipantTeam.getTowerWorld().getName(), participantTeam.getTowerName()));
                }
                if (towerWorldManager.hasRegion(participantTeam.getTowerName())) {
                    if (towerWorldManager.hasRegion("spawn")) {
                        try {
                            towerWorldManager.getRegion(participantTeam.getTowerName()).setParent(towerWorldManager.getRegion("spawn"));
                        } catch (ProtectedRegion.CircularInheritanceException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    player2.sendMessage(Component.text("Error creating region, "+participantTeam.getTowerName()));
                }
                player2.performCommand(String.format("rg setpriority -w \"%s\" %s 1", ParticipantTeam.getTowerWorld().getName(), participantTeam.getTowerName()));
                participantTeam.loadRegions();
                participantTeam.getTowerRegion().setTeleportCenter(player2.getLocation().getY());
                player2.closeInventory();
                player2.performCommand(String.format("rg info -w \"%s\" %s", ParticipantTeam.getTowerWorld().getName(), participantTeam.getTowerName()));
            });
            gui.placeElement(5, 2, towerElement);

            RegionManager gingerbreadWorldManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(ParticipantTeam.getGingerbreadWorld()));
            ItemStack gingerbreadItem = new ItemStack(Material.SCUTE);
            ItemMeta gingerbreadMeta = gingerbreadItem.getItemMeta();
            gingerbreadMeta.setCustomModelData(2);
            gingerbreadMeta.displayName(Component.text("Gingerbread Region"));
            gingerbreadMeta.lore(new ArrayList<>(){{
                add(TextUtil.formatText(participantTeam.getGingerbreadRegion() != null ? "Redefine Region" : "Define Region"));
            }});
            gingerbreadItem.setItemMeta(gingerbreadMeta);
            ButtonElement gingerbreadElement = new ButtonElement(gingerbreadItem, player2 -> {
                if (gingerbreadWorldManager.hasRegion(participantTeam.getGingerbreadName())) {
//                    player1.sendMessage(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player2.performCommand(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getGingerbreadWorld().getName(), participantTeam.getGingerbreadName()));
                } else {
//                    player1.sendMessage(String.format("rg define -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player2.performCommand(String.format("rg define -w \"%s\" %s", ParticipantTeam.getGingerbreadWorld().getName(), participantTeam.getGingerbreadName()));
                }
                if (gingerbreadWorldManager.hasRegion(participantTeam.getGingerbreadName())) {
                    if (gingerbreadWorldManager.hasRegion("candy-village")) {
                        try {
                            gingerbreadWorldManager.getRegion(participantTeam.getGingerbreadName()).setParent(gingerbreadWorldManager.getRegion("candy-village"));
                        } catch (ProtectedRegion.CircularInheritanceException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    player2.sendMessage(Component.text("Error creating region, "+participantTeam.getGingerbreadName()));
                }
                player2.performCommand(String.format("rg setpriority -w \"%s\" %s 1", ParticipantTeam.getGingerbreadWorld().getName(), participantTeam.getGingerbreadName()));
                participantTeam.loadRegions();
                participantTeam.getGingerbreadRegion().setTeleportCenter(player2.getLocation().getY());
                player2.closeInventory();
                player2.performCommand(String.format("rg info -w \"%s\" %s", ParticipantTeam.getGingerbreadWorld().getName(), participantTeam.getGingerbreadName()));
            });
            gui.placeElement(7, 2, gingerbreadElement);

            gui.openInventory(player1);

        }, new ButtonElement(ButtonElement.backItem(), player1 -> {
            godManager.getGodGui().openInventory(player1);
        }));
        return regionGui;
    }
}
