package io.github.idkahn.towerchallenge.gods.godgui.teamregionsetups;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.TextUtil;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.gods.GodManager;
import io.github.idkahn.towerchallenge.gods.godgui.regionteleports.WorldIndividualGui;
import io.github.idkahn.towerchallenge.gui.element.ButtonElement;
import io.github.idkahn.towerchallenge.gui.page.Gui;
import io.github.idkahn.towerchallenge.gui.page.Openable;
import io.github.idkahn.towerchallenge.gui.page.PresetGui;
import io.github.idkahn.towerchallenge.gui.page.TeamGui;
import io.github.idkahn.towerchallenge.misc.CommandUtils;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;
import io.github.idkahn.towerchallenge.towering.regions.GingerbreadRegion;
import io.github.idkahn.towerchallenge.towering.regions.SpawnRegion;
import io.github.idkahn.towerchallenge.towering.regions.TowerRegion;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
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
    public Gui getGui() {
        TeamGui regionGui = new TeamGui(Component.text("Team to set:"), participantTeam -> {
            TowerRegion towerRegion = participantTeam.getTowerRegion();
            SpawnRegion spawnRegion = participantTeam.getSpawnRegion();
            GingerbreadRegion gingerbreadRegion = participantTeam.getGingerbreadRegion();

            List<Component> lore = new ArrayList<>(
                    TextUtil.formatText(
                            String.format("Tower: %s", towerRegion == null ? "No Region" : towerRegion.getId()),
                            String.format("Spawn: %s", spawnRegion == null ? "No Region" : spawnRegion.getId()),
                            String.format("Gingerbread: %s", gingerbreadRegion == null ? "No Region" : gingerbreadRegion.getId())
                    )
            );

            return lore;

        }, towerListener.getTeams().values().stream().toList(), (player, participantTeam) -> {
            PresetGui gui = new PresetGui(Component.text(participantTeam.getTextName()), 3);

            RegionManager spawnWorldManager = ChallengeManager.regionContainer().get(BukkitAdapter.adapt(ParticipantTeam.getSpawnWorld()));
            ItemStack spawnItem = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta spawnMeta = spawnItem.getItemMeta();
            spawnMeta.displayName(Component.text("Spawn Region"));
            spawnMeta.lore(new ArrayList<>(){{
                add(TextUtil.formatText(participantTeam.getSpawnRegion() != null ? "Redefine Region" : "Define Region"));
            }});
            spawnItem.setItemMeta(spawnMeta);
            ButtonElement spawnElement = new ButtonElement(spawnItem, player1 -> {
                if (spawnWorldManager.hasRegion(participantTeam.getSpawnName())) {
//                    player1.sendMessage(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player1.performCommand(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                } else {
//                    player1.sendMessage(String.format("rg define -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player1.performCommand(String.format("rg define -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
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
                    player1.sendMessage(Component.text("Error creating region, "+participantTeam.getSpawnName()));
                }
                player1.performCommand(String.format("rg setpriority -w \"%s\" %s 1", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                participantTeam.loadRegions();
                participantTeam.centerRegions(player1.getLocation().getY());
                player1.closeInventory();
                player1.performCommand(String.format("rg info -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
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
            ButtonElement towerElement = new ButtonElement(towerItem, player1 -> {
                if (towerWorldManager.hasRegion(participantTeam.getTowerName())) {
//                    player1.sendMessage(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player1.performCommand(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getTowerWorld().getName(), participantTeam.getTowerName()));
                } else {
//                    player1.sendMessage(String.format("rg define -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player1.performCommand(String.format("rg define -w \"%s\" %s", ParticipantTeam.getTowerWorld().getName(), participantTeam.getTowerName()));
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
                    player1.sendMessage(Component.text("Error creating region, "+participantTeam.getTowerName()));
                }
                player1.performCommand(String.format("rg setpriority -w \"%s\" %s 1", ParticipantTeam.getTowerWorld().getName(), participantTeam.getTowerName()));
                participantTeam.loadRegions();
                participantTeam.getTowerRegion().setTeleportCenter(player1.getLocation().getY());
                player1.closeInventory();
                player1.performCommand(String.format("rg info -w \"%s\" %s", ParticipantTeam.getTowerWorld().getName(), participantTeam.getTowerName()));
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
            ButtonElement gingerbreadElement = new ButtonElement(gingerbreadItem, player1 -> {
                if (gingerbreadWorldManager.hasRegion(participantTeam.getGingerbreadName())) {
//                    player1.sendMessage(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player1.performCommand(String.format("rg redefine -w \"%s\" %s", ParticipantTeam.getGingerbreadWorld().getName(), participantTeam.getGingerbreadName()));
                } else {
//                    player1.sendMessage(String.format("rg define -w \"%s\" %s", ParticipantTeam.getSpawnWorld().getName(), participantTeam.getSpawnName()));
                    player1.performCommand(String.format("rg define -w \"%s\" %s", ParticipantTeam.getGingerbreadWorld().getName(), participantTeam.getGingerbreadName()));
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
                    player1.sendMessage(Component.text("Error creating region, "+participantTeam.getGingerbreadName()));
                }
                player1.performCommand(String.format("rg setpriority -w \"%s\" %s 1", ParticipantTeam.getGingerbreadWorld().getName(), participantTeam.getGingerbreadName()));
                participantTeam.loadRegions();
                participantTeam.getGingerbreadRegion().setTeleportCenter(player1.getLocation().getY());
                player1.closeInventory();
                player1.performCommand(String.format("rg info -w \"%s\" %s", ParticipantTeam.getGingerbreadWorld().getName(), participantTeam.getGingerbreadName()));
            });
            gui.placeElement(7, 2, gingerbreadElement);

            gui.openInventory(player);

        }, new ButtonElement(ButtonElement.backItem(), player -> {
            godManager.getGodGui().openInventory(player);
        }));
        return regionGui;
    }
}
