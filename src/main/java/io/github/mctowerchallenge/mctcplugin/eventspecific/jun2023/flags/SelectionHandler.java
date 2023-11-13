package io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.flags;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mctowerchallenge.mctcplugin.Database;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.sql.SQLException;
import java.util.*;

/**
 * Handles the selection of pride flags and their indicators for the Jun 2023 event.
 */
public class SelectionHandler implements Listener {

    public static final String SELECT_TAG = "pride-select";
    public static final String SHOW_TO_TAG = "show-to";
    public static final String INDICATOR_TAG = "pride-indicator";

    public static final List<Selection> selections = new ArrayList<>() {{
        add(new Selection(2, 100, "Asexual", new Location(Worlds.Jun2023(), 178.500000d, 67.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(3, 101, "Agender", new Location(Worlds.Jun2023(), 175.500000d, 66.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(4, 102, "Aromantic", new Location(Worlds.Jun2023(), 176.500000d, 67.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(5, 103, "Aroace", new Location(Worlds.Jun2023(), 177.500000d, 67.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(6, 104, "Bisexual", new Location(Worlds.Jun2023(), 176.500000d, 65.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(7, 105, "Bigender", new Location(Worlds.Jun2023(), 175.031250d, 65.500000d, -2182.500000d, 270.000000f, 0.000000f)));
        add(new Selection(8, 106, "Demigender", new Location(Worlds.Jun2023(), 175.031250d, 65.500000d, -2183.500000d, 270.000000f, 0.000000f)));
        add(new Selection(9, 108, "Demisexual", new Location(Worlds.Jun2023(), 178.500000d, 66.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(10, 107, "Demiromantic", new Location(Worlds.Jun2023(), 176.500000d, 66.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(11, 109, "Gay", new Location(Worlds.Jun2023(), 179.500000d, 65.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(12, 110, "Genderfluid", new Location(Worlds.Jun2023(), 175.031250d, 65.500000d, -2181.500000d, 270.000000f, 0.000000f)));
        add(new Selection(13, 111, "Genderqueer", new Location(Worlds.Jun2023(), 175.031250d, 67.500000d, -2182.500000d, 270.000000f, 0.000000f)));
        add(new Selection(14, 112, "Graysexual", new Location(Worlds.Jun2023(), 179.500000d, 66.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(15, 113, "Intersex", new Location(Worlds.Jun2023(), 175.031250d, 66.500000d, -2183.500000d, 270.000000f, 0.000000f)));
        add(new Selection(16, 114, "Lesbian", new Location(Worlds.Jun2023(), 178.500000d, 65.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(17, 115, "Nonbinary", new Location(Worlds.Jun2023(), 175.031250d, 66.500000d, -2181.500000d, 270.000000f, 0.000000f)));
        add(new Selection(18, 116, "Omnisexual", new Location(Worlds.Jun2023(), 175.500000d, 65.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(21, 117, "Pansexual", new Location(Worlds.Jun2023(), 177.500000d, 65.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(22, 118, "Progress", new Location(Worlds.Jun2023(), 177.500000d, 66.500000d, -2181.031250d, 180.000000f, 0.000000f)));
        add(new Selection(23, 119, "Transgender", new Location(Worlds.Jun2023(), 175.031250d, 66.500000d, -2182.500000d, 270.000000f, 0.000000f)));
    }};

    /**
     * Get a Selection instance based on its icon model ID.
     *
     * @param modelId The custom model data of the icon.
     * @return The corresponding Selection instance, or null if not found.
     */
    public static @Nullable Selection getSelectionByModelId(int modelId) {
        for (Selection selection : selections) {
            if (selection.getIconModelId() == modelId) {
                return selection;
            }
        }
        return null;
    }

    /**
     * Get a Selection instance based on its database ID.
     *
     * @param databaseId The database ID of the flag.
     * @return The corresponding Selection instance, or null if not found.
     */
    public static @Nullable Selection getSelectionByDatabaseId(int databaseId) {
        for (Selection selection : selections) {
            if (selection.getDatabaseId() == databaseId) {
                return selection;
            }
        }
        return null;
    }

    private final Plugin plugin;
    private final Database database;
    private final Map<UUID, Selection> playerSelections;
    private final Map<UUID, Entity> playerIndicators;

    /**
     * Creates a new instance of the SelectionHandler class.
     *
     * @param plugin   The plugin instance.
     * @param database The database instance.
     */
    public SelectionHandler(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        this.playerSelections = new HashMap<>();
        this.playerIndicators = new HashMap<>();
        load();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Load player selections from the database.
     */
    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Map<UUID, Integer> dbPlayerSelections = database.getAllPlayerFlags();
                for (Map.Entry<UUID, Integer> entry : dbPlayerSelections.entrySet()) {
                    Selection selection = getSelectionByDatabaseId(entry.getValue());
                    if (selection != null) {
                        playerSelections.put(entry.getKey(), selection);
                    } else {
                        playerSelections.put(entry.getKey(), getSelectionByDatabaseId(22));
                    }
                }
            } catch (SQLException e) {
                Bukkit.getLogger().warning(e.getMessage());
            }
        });
    }

    /**
     * Load a player's selection from the database.
     *
     * @param player The player to load the selection for.
     */
    public void loadPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                int flagId = database.getPlayerFlag(player.getUniqueId());
                Selection selection = getSelectionByDatabaseId(flagId);
                if (selection != null) {
                    playerSelections.put(player.getUniqueId(), selection);
                } else {
                    int newId = database.setPlayerFlag(player.getUniqueId(), 22);
                    Selection newSelection = getSelectionByDatabaseId(newId);
                    if (newSelection != null) {
                        playerSelections.put(player.getUniqueId(), getSelectionByDatabaseId(22));
                    }
                }
                Bukkit.getScheduler().runTask(plugin, () -> loadIndicatorsForPlayer(player));
            } catch (SQLException e) {
                Bukkit.getLogger().warning(e.getMessage());
            }
        });
    }

    /**
     * Load indicators for a player.
     *
     * @param player The player to load indicators for.
     */
    public void loadIndicatorsForPlayer(Player player) {
        Selection selection = playerSelections.get(player.getUniqueId());
        if (selection != null) {
            Entity indicator = playerIndicators.get(player.getUniqueId());
            if (indicator != null) {
                indicator.remove();
            }
            indicator = selection.spawnIndicator();
            NBTUtils.setUniqueID(plugin, SHOW_TO_TAG, indicator, player.getUniqueId());
            playerIndicators.put(player.getUniqueId(), indicator);
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!otherPlayer.getUniqueId().equals(player.getUniqueId())) {
                    otherPlayer.hideEntity(plugin, indicator);
                } else {
                    otherPlayer.showEntity(plugin, indicator);
                }
            }
        }
        for (Map.Entry<UUID, Entity> entry : playerIndicators.entrySet()) {
            if (!entry.getKey().equals(player.getUniqueId())) {
                player.hideEntity(plugin, entry.getValue());
            } else {
                player.showEntity(plugin, entry.getValue());
            }
        }
    }

    /**
     * Handle player join event.
     *
     * @param event The PlayerJoinEvent.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayer(player);
    }

    /**
     * Handle player interact entity event.
     *
     * @param event The PlayerInteractEntityEvent.
     */
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();

        if (entity.getScoreboardTags().contains(SELECT_TAG) && entity instanceof ItemFrame itemFrame) {
            event.setCancelled(true);
            ItemStack itemStack = itemFrame.getItem();
            ItemMeta meta = itemStack.getItemMeta();
            Selection selection = getSelectionByModelId(meta.getCustomModelData());
            if (selection != null) {
                if (meta.getCustomModelData() == selection.getIconModelId()) {
                    Player player = event.getPlayer();
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            int newFlag = database.setPlayerFlag(player.getUniqueId(), selection.getDatabaseId());
                            Selection newSelection = getSelectionByDatabaseId(newFlag);
                            playerSelections.put(player.getUniqueId(), newSelection);
                            player.sendActionBar(Component.text(String.format("%s Flag", selection.getName())).append(TextUtil.formatText(" Selected!")));
                            player.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.bit"), Sound.Source.RECORD, 1f, 2f));
                            Bukkit.getScheduler().runTask(plugin, () -> loadIndicatorsForPlayer(player));
                        } catch (SQLException e) {
                            player.sendMessage(CommandUtils.errorMessage("Error setting flag in the database."));
                        }
                    });
                }
            }
        }

    }

    /**
     * Handle plugin disable event.
     *
     * @param event The PluginDisableEvent.
     */
    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        List<Entity> entities = Bukkit.selectEntities(Bukkit.getConsoleSender(), String.format("@e[tag=%s]", INDICATOR_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    /**
     * Represents a selection of a pride flag.
     */
    public static class Selection {

        private final int databaseId;
        private final int iconModelId;
        private final String name;
        private final Location itemFrameLocation;

        /**
         * Creates a new instance of the Selection class.
         *
         * @param databaseId        The database ID of the flag.
         * @param iconModelId       The custom model data of the icon.
         * @param name              The name of the flag.
         * @param itemFrameLocation The location of the item frame.
         */
        public Selection(int databaseId, int iconModelId, String name, Location itemFrameLocation) {
            this.databaseId = databaseId;
            this.iconModelId = iconModelId;
            this.name = name;
            this.itemFrameLocation = itemFrameLocation;
        }

        public int getDatabaseId() {
            return databaseId;
        }

        public int getIconModelId() {
            return iconModelId;
        }

        public String getName() {
            return name;
        }

        /**
         * Spawn an indicator entity for the selection.
         *
         * @return The spawned indicator entity.
         */
        public Entity spawnIndicator() {
            Location location = itemFrameLocation.clone().add(itemFrameLocation.getDirection().multiply(0.44375));
            ItemDisplay entity = (ItemDisplay) itemFrameLocation.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
            entity.addScoreboardTag(INDICATOR_TAG);
            entity.setItemStack(GuiUtil.formatItem("indicator", Material.PAPER, 120));
            entity.setTransformation(
                    new Transformation(
                            new Vector3f(0.0f, 0.0f, 0.0f),
                            new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f),
                            new Vector3f(1.0f, 1.0f, 1.0f),
                            new Quaternionf(-0.707f, 0.0f, 0.0f, 0.707f)
                    )
            );
            entity.setRotation(itemFrameLocation.getYaw()+180, itemFrameLocation.getPitch());
            entity.setBrightness(new Display.Brightness(15, 15));
            return entity;
        }
    }

}
