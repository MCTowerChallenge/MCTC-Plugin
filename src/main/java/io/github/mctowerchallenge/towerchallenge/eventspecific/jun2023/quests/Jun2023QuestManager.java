package io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.quests;

import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.quests.minesweeper.MineHandler;
import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.quests.minesweeper.gamepieces.Flag;
import io.github.mctowerchallenge.towerchallenge.quest.util.FullInventory;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mctowerchallenge.towerchallenge.Worlds;
import io.github.mctowerchallenge.towerchallenge.god.GodTeam;
import io.github.mctowerchallenge.towerchallenge.gui.page.TeamGui;
import io.github.mctowerchallenge.towerchallenge.interaction.InteractableTagManager;
import io.github.mctowerchallenge.towerchallenge.interaction.InteractableTaggedEntity;
import io.github.mctowerchallenge.towerchallenge.interaction.Sparkle;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.Dialogue;
import io.github.mctowerchallenge.towerchallenge.team.TeamManager;
import io.github.mctowerchallenge.towerchallenge.team.TowerTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Manages the specific quest instance for the June 2023 event.
 */
public class Jun2023QuestManager implements Listener, Openable {

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {
    });
    public static final String REMOVE_TAG = "jun2023-remove";

    private static final Map<Integer, Location> teamLocations = new HashMap<>() {{
        put(1, Jun2023QuestInstance.baseLocation); // God x
        put(3, new Location(Worlds.Jun2023_quest(), 98, 64, -3)); // Orange x
        put(4, new Location(Worlds.Jun2023_quest(), 198, 64, -3)); // Yellow x
        put(5, new Location(Worlds.Jun2023_quest(), 298, 64, -3)); // Lime x
        put(7, new Location(Worlds.Jun2023_quest(), 398, 64, -3)); // Cyan x
        put(8, new Location(Worlds.Jun2023_quest(), 498, 64, -3)); // Light Blue x
        put(10, new Location(Worlds.Jun2023_quest(), 598, 64, -3)); // Purple x
        put(11, new Location(Worlds.Jun2023_quest(), 698, 64, -3)); // Magenta x
        put(12, new Location(Worlds.Jun2023_quest(), 798, 64, -3)); // Pink x
    }};

    private final Plugin plugin;
    private final TeamManager teamManager;
    private final Map<Integer, Jun2023QuestInstance> questInstances;

    /**
     * Creates a Jun2023QuestManager instance.
     *
     * @param plugin      The plugin instance.
     * @param teamManager The TeamManager instance.
     */
    public Jun2023QuestManager(Plugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;

        unloadAll();

        questInstances = new HashMap<>();
        for (Map.Entry<Integer, Location> entry : teamLocations.entrySet()) {
            questInstances.put(entry.getKey(), new Jun2023QuestInstance(plugin, teamManager, entry.getKey(), entry.getValue()));
        }

        new Sparkle(plugin, new Location(Worlds.Jun2023(), 276, 62.5, -2034.55), 0.3f, 0.45f, 0.01f);

        Dialogue doorNoOpen = new Dialogue(plugin, Dialogue.playerThoughts("The door won't open..."), 0.0);
        BiConsumer<TowerTeam, PlayerInteractEntityEvent> doorNoOpenEvent = (team, event) -> {
            Player player = event.getPlayer();
            doorNoOpen.play(player);
        };

        InteractableTaggedEntity caveEnterDoor = new InteractableTaggedEntity(Jun2023QuestInstance.ENTER_DOOR);
        caveEnterDoor.setDefaultInteractionHandler((team, event) -> {
            Player player = event.getPlayer();
            Jun2023QuestInstance instance = getQuestInstance(team);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    instance.enterTeleport(player);
                });
            }
        });
        InteractableTagManager.registerTag(caveEnterDoor);

        InteractableTaggedEntity caveExitDoor = new InteractableTaggedEntity(Jun2023QuestInstance.EXIT_DOOR);
        caveExitDoor.setDefaultInteractionHandler((team, event) -> {
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTask(plugin, () -> {
                exitTeleport(player);
            });
        });
        InteractableTagManager.registerTag(caveExitDoor);

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    /**
     * Gets the Jun2023QuestInstance associated with a specific team.
     *
     * @param team The TowerTeam to retrieve the quest instance for.
     * @return The associated Jun2023QuestInstance, or null if not found.
     */
    public @Nullable Jun2023QuestInstance getQuestInstance(TowerTeam team) {
        return questInstances.get(team.getDatabaseId());
    }

    public static final Location EXIT_LOCATION = new Location(Worlds.Jun2023(), 282.5, 61, -2212.5, 0, 0);

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
            Jun2023QuestInstance instance = getQuestInstance(team);
            if (instance != null) {
                instance.getGui(player1).openInventory(player1);
            }
        }, Element.blank());
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (entity.getScoreboardTags().contains(MineHandler.FLAG_TAG)) {
            ItemStack flag = Flag.makeItemFlag(plugin, GuiUtil.formatItem("Flag (Progress)", Material.SCUTE, 38));
            FullInventory.givePlayerItems(player, flag);
        }
        if (entity.getScoreboardTags().contains(MineHandler.BRUSH_TAG) && (!MineHandler.brushPlayers.contains(player.getUniqueId()) || teamManager.getPlayerTeam(player) instanceof GodTeam)) {
            ItemStack brush = new ItemStack(Material.BRUSH);
            ItemMeta meta = brush.getItemMeta();
            meta.setPlaceableKeys(new HashSet<>(){{
                add(NamespacedKey.minecraft("suspicious_sand"));
                add(NamespacedKey.minecraft("suspicious_gravel"));
                add(NamespacedKey.minecraft("sandstone"));
            }});
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
//            meta.setUnbreakable(true);
            brush.setItemMeta(meta);
            FullInventory.givePlayerItems(player, brush);
            MineHandler.brushPlayers.add(player.getUniqueId());
        }
    }

}
