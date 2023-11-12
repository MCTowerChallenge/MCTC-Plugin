package io.github.mctowerchallenge.towerchallenge.eventspecific.oct2023.quest;

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
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mctowerchallenge.towerchallenge.Worlds;
import io.github.mctowerchallenge.towerchallenge.gui.page.TeamGui;
import io.github.mctowerchallenge.towerchallenge.quest.Quest;
import io.github.mctowerchallenge.towerchallenge.quest.QuestManager;
import io.github.mctowerchallenge.towerchallenge.quest.util.BlockVoucher;
import io.github.mctowerchallenge.towerchallenge.quest.util.FullInventory;
import io.github.mctowerchallenge.towerchallenge.team.TeamManager;
import io.github.mctowerchallenge.towerchallenge.team.TowerTeam;
import io.github.mctowerchallenge.towerchallenge.utility.MVPortalUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the specific quest instance for the October 2023 event.
 */
public class Oct2023QuestManager implements Listener, Openable {

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {
    });
    public static final String REMOVE_TAG = "oct2023-remove";

    public static final int BLOCKS_BETWEEN_TEAMS = 80;
    public static final Vector DIRECTION = new Vector(-1, 0, 0);

    public static final Location[] TemplateBounds = new Location[]{
            new Location(Worlds.Oct2023_quest(), -41, 58, -55),
            new Location(Worlds.Oct2023_quest(), -1, 149, 0)
    };

    public static final Location[] ENTER_PORTAL = new Location[]{
            new Location(Worlds.Oct2023(), 45, 64, -118),
            new Location(Worlds.Oct2023(), 46, 66, -118)
    };
    public static final String ENTER_PORTAL_NAME = "Oct2023_toQuest";
    public static final Location TEMPLATE_ENTER_LOCATION = new Location(Worlds.Oct2023_quest(), -16.0, 65.0, -17.0, 0, 0);

    private final Plugin plugin;
    private final TeamManager teamManager;
    private final Map<Integer, Oct2023QuestInstance> questInstances;

    /**
     * Creates a Oct2023QuestManager instance.
     *
     * @param plugin      The plugin instance.
     * @param teamManager The TeamManager instance.
     */
    public Oct2023QuestManager(Plugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;

        unloadAll();

        MVPortalUtils.initPortal(ENTER_PORTAL_NAME, ENTER_PORTAL, TEMPLATE_ENTER_LOCATION);

        questInstances = new HashMap<>();
        for (TowerTeam team : teamManager.getAllTeams()) {
            Location location = TemplateBounds[0].clone().add(DIRECTION.clone().multiply(BLOCKS_BETWEEN_TEAMS * team.getDatabaseId()));
//            Bukkit.getLogger().info(String.format("%s %.2fX %.2fY %.2fZ", team.getTextName(), location.getX(), location.getY(), location.getZ()));
            questInstances.put(team.getDatabaseId(), new Oct2023QuestInstance(plugin, teamManager, team.getDatabaseId(), location));
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    public void copyTemplateToTeams() {
        World world = BukkitAdapter.adapt(TemplateBounds[0].getWorld());
        CuboidRegion cuboidRegion = new CuboidRegion(BukkitAdapter.adapt(TemplateBounds[0]).toVector().toBlockPoint(), BukkitAdapter.adapt(TemplateBounds[1]).toVector().toBlockPoint());
        for (Map.Entry<Integer, Oct2023QuestInstance> entry : questInstances.entrySet()) {
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
        for (Map.Entry<Integer, Oct2023QuestInstance> entry : questInstances.entrySet()) {
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

    /**
     * Gets the Oct2023QuestInstance associated with a specific team.
     *
     * @param team The TowerTeam to retrieve the quest instance for.
     * @return The associated Oct2023QuestInstance, or null if not found.
     */
    public @Nullable Oct2023QuestInstance getQuestInstance(TowerTeam team) {
        return questInstances.get(team.getDatabaseId());
    }

    public static final Location EXIT_LOCATION = new Location(Worlds.Oct2023(), 46.0, 64.0, -116.0, 0, 0);

    /**
     * Teleports the player to the exit location.
     *
     * @param entity The player to teleport.
     */
    public void exitTeleport(Entity entity) {
        entity.teleport(EXIT_LOCATION);
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
            Oct2023QuestInstance instance = getQuestInstance(team);
            if (instance != null) {
                instance.getGui(player1).openInventory(player1);
            }
        }, Element.blank());
    }

    @EventHandler
    public void onPlayerPortal(final MVPortalEvent event) {
        MVPortal portal = event.getSendingPortal();
        if (portal.getName().equals(ENTER_PORTAL_NAME)) {
            event.setCancelled(true);
            Player player = event.getTeleportee();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }

            Oct2023QuestInstance instance = getQuestInstance(team);
            if (instance == null) {
                return;
            }

            instance.enterTeleport(player);
        }
    }

    public static void checkCompletedQuests(TowerTeam team, Player player) {
        Quest parkour = team.getQuest(QuestManager.PARKOUR);
        Quest trivia = team.getQuest(QuestManager.TRIVIA);
        if (parkour == null || !parkour.isCompleted() || trivia == null || !trivia.isCompleted()) {
            return;
        }

        Quest hauntedHouse = team.getQuest(QuestManager.HAUNTED_HOUSE);
        if (hauntedHouse == null || hauntedHouse.isCompleted()) {
            return;
        }

        team.completeQuest(QuestManager.HAUNTED_HOUSE);
        ItemStack blockVouchers = BlockVoucher.getVouchers(2);
        player.sendMessage(QuestManager.getRewards(blockVouchers));
        FullInventory.givePlayerItems(player, blockVouchers);
    }
}
