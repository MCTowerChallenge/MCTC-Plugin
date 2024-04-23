package io.github.mctowerchallenge.mctcplugin.eventspecific.may2024.quests;

import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.event.MVPortalEvent;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.gui.page.TeamGui;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.character.GenericBeeConservationistMan;
import io.github.mctowerchallenge.mctcplugin.quest.Quest;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mctowerchallenge.mctcplugin.utility.MVPortalUtils;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.*;

/**
 * Manages the specific quest instance for the October 2023 event.
 */
public class May2024QuestManager implements Listener, Openable {

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {
    });
    public static final String REMOVE_TAG = "may2024-remove";

    public static final int BLOCKS_BETWEEN_TEAMS = 80;
    public static final Vector DIRECTION = new Vector(-1, 0, 0);

    public static final Location[] TemplateBounds = new Location[]{
            new Location(Worlds.May2024_quest(), 7, 63, -30),
            new Location(Worlds.May2024_quest(), 29, 82, -11)
    };

    public static final Location[][] ENTER_PORTALS = new Location[][]{
            {new Location(Worlds.May2024(), 579, 55, 411), new Location(Worlds.May2024(), 579, 58, 414)},
            {new Location(Worlds.May2024(), 581, 55, 416), new Location(Worlds.May2024(), 584, 57, 416)}
    };

    public static final String ENTER_PORTAL_NAME = "May2024_toQuest";
    public static final Location TEMPLATE_ENTER_LOCATION = new Location(Worlds.May2024_quest(), 12.6, 65.0, -16.85, -100.0f, 6.2f);

    public static final Location EXIT_LOCATION = new Location(Worlds.May2024(), 581.555, 55.0, 414.393, -135, 4.6f);

    private final Random random;
    private final Plugin plugin;
    private final TeamManager teamManager;
    private final Map<Integer, May2024QuestInstance> questInstances;

    /**
     * Creates a May2024QuestManager instance.
     *
     * @param plugin      The plugin instance.
     * @param teamManager The TeamManager instance.
     */
    public May2024QuestManager(Plugin plugin, TeamManager teamManager) {
        this.random = new SecureRandom();
        this.plugin = plugin;
        this.teamManager = teamManager;

        unloadAll();

        for (int i = 0; i < ENTER_PORTALS.length; i++) {
            MVPortalUtils.initPortal(ENTER_PORTAL_NAME+"_"+i, ENTER_PORTALS[i], TEMPLATE_ENTER_LOCATION);
        }

        questInstances = new HashMap<>();
        for (TowerTeam team : teamManager.getAllTeams()) {
            Location location = TemplateBounds[0].clone().add(DIRECTION.clone().multiply(BLOCKS_BETWEEN_TEAMS * team.getDatabaseId()));
            questInstances.put(team.getDatabaseId(), new May2024QuestInstance(plugin, teamManager, team.getDatabaseId(), location));
            new BukkitRunnable() {
                @Override
                public void run() {
                    Quest quest = team.getQuest(QuestTags.COLLECT_NESTS);
                    if (quest != null && quest.isCompleted()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                GenericBeeConservationistMan.hideTape(team);
                            }
                        }.runTask(plugin);
                    }
                }
            }.runTaskAsynchronously(plugin);
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void copyTemplateToTeams() {
        World world = BukkitAdapter.adapt(TemplateBounds[0].getWorld());
        CuboidRegion cuboidRegion = new CuboidRegion(BukkitAdapter.adapt(TemplateBounds[0]).toVector().toBlockPoint(), BukkitAdapter.adapt(TemplateBounds[1]).toVector().toBlockPoint());
        for (Map.Entry<Integer, May2024QuestInstance> entry : questInstances.entrySet()) {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(world, cuboidRegion, editSession, BukkitAdapter.asBlockVector(entry.getValue().getInstanceLocation()));
                forwardExtentCopy.setCopyingEntities(true);
                Operations.complete(forwardExtentCopy);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteTeamInstances() {
        World world = BukkitAdapter.adapt(TemplateBounds[0].getWorld());
        for (Map.Entry<Integer, May2024QuestInstance> entry : questInstances.entrySet()) {
            CuboidRegion cuboidRegion = new CuboidRegion(BukkitAdapter.adapt(entry.getValue().offsetLocation(TemplateBounds[0])).toVector().toBlockPoint(), BukkitAdapter.adapt(entry.getValue().offsetLocation(TemplateBounds[1])).toVector().toBlockPoint());
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                for (com.sk89q.worldedit.entity.Entity entity : editSession.getEntities(cuboidRegion)) {
                    entity.remove();
                }
                editSession.setBlocks(cuboidRegion, BukkitAdapter.adapt(Bukkit.createBlockData(Material.AIR)));
                Operations.complete(editSession.commit());
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public @Nullable May2024QuestInstance getQuestInstance(TowerTeam team) {
        return questInstances.get(team.getDatabaseId());
    }

    public void exitTeleport(Entity entity) {
        entity.teleport(EXIT_LOCATION);
    }

    @EventHandler
    public void onMobTarget(final EntityTargetEvent event) {
        if (event.getEntityType().equals(EntityType.BEE)) {
            event.setCancelled(true);
        }
    }

    /**
     * Unloads all tagged entities with the REMOVE_TAG.
     */
    public void unloadAll() {
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", REMOVE_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    @Override
    public Gui getGui(Player player) {
        return new TeamGui(plugin, Component.text("Which team?"), new ArrayList<>(), teamManager.getAllTeams(), (player1, team) -> {
            May2024QuestInstance instance = getQuestInstance(team);
            if (instance != null) {
                instance.getGui(player1).openInventory(player1);
            }
        }, Element.blank());
    }

    @EventHandler
    public void onPlayerPortal(final MVPortalEvent event) {
        MVPortal portal = event.getSendingPortal();
        if (portal.getName().contains(ENTER_PORTAL_NAME)) {
            event.setCancelled(true);
            Player player = event.getTeleportee();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }
            Quest nestQuest = team.getQuest(QuestTags.COLLECT_NESTS);
            if (nestQuest == null || !nestQuest.isCompleted()) {
                event.setCancelled(true);
                return;
            }

            May2024QuestInstance instance = getQuestInstance(team);
            if (instance == null) {
                return;
            }

            instance.enterTeleport(player);
        }
    }

}
