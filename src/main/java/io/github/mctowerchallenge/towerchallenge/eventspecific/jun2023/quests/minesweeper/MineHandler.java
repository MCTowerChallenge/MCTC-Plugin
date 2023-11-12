package io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.quests.minesweeper;

import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.quests.minesweeper.gamepieces.Flag;
import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.quests.minesweeper.gamepieces.GamePiece;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mctowerchallenge.towerchallenge.Worlds;
import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.quests.Door;
import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.quests.Jun2023QuestInstance;
import io.github.mctowerchallenge.towerchallenge.utility.TeamUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Brushable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Handles the Minesweeper game mechanics and interactions for the Jun 2023 quest.
 * This class includes methods to load the game grid, perform sweeping actions, place flags,
 * check completion status, and handle player interactions.
 */
public class MineHandler implements Listener {

    public static final String MINESWEEPER_REMOVE_TAG = "jun2023-minesweeper-remove";
    public static final Vector DOOR_OFFSET = new Vector(-2, 0, 54);

    public static final String BRUSH_TAG = "minesweeper-brush";
    public static final Set<UUID> brushPlayers = new HashSet<>();
    public static final String FLAG_TAG = "minesweeper-flag";
    public static final Set<UUID> flagPlayers = new HashSet<>();

    public static final Location BASE_GRID_LOCATION = new Location(Worlds.Jun2023_quest(), -5, 64, 49);

    public static final int GRID_SIZE = 11;

    private static final int[][] BASE_GRID = {
            {0, 1, -1, 2, 2, 3, -1, 1, 0, 0, 0},
            {0, 1, 2, 3, -1, -1, 3, 2, 1, 0, 0},
            {0, 0, 1, -1, 3, 2, 2, -1, 1, 0, 0},
            {0, 0, 1, 1, 1, 1, 3, 3, 2, 1, 1},
            {0, 0, 0, 0, 0, 1, -1, -1, 1, 1, -1},
            {0, 0, 1, 2, 2, 3, 3, 3, 1, 1, 1},
            {0, 0, 1, -1, -1, 2, -1, 1, 0, 1, 1},
            {0, 0, 1, 2, 2, 2, 1, 1, 1, 2, -1},
            {0, 0, 0, 1, 1, 1, 0, 0, 1, -1, 3},
            {0, 0, 1, 2, -1, 1, 1, 1, 2, 2, -1},
            {0, 0, 1, -1, 2, 1, 1, -1, 1, 1, 1},
    };

    private final Plugin plugin;
    private final Location location;
    private final GamePiece[][] gamePieces;
    private final Door door;
    private boolean completed;

    private final String teamRemoveTag;

    /**
     * Creates a new instance of the MineHandler for handling Minesweeper mechanics.
     *
     * @param plugin   The plugin instance.
     * @param instance The associated Jun2023QuestInstance for this Minesweeper game.
     */
    public MineHandler(Plugin plugin, Jun2023QuestInstance instance) {
        this.plugin = plugin;
        this.location = instance.offsetLocation(BASE_GRID_LOCATION);
        this.gamePieces = new GamePiece[GRID_SIZE][GRID_SIZE];
        this.door = new Door(instance, DOOR_OFFSET);
        this.completed = false;

        this.teamRemoveTag = TeamUtils.toTeamTag(instance.getTeam(), MINESWEEPER_REMOVE_TAG);

        Bukkit.getScheduler().runTaskLater(plugin, this::load, 0);

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    /**
     * Gets the team-specific tag used for removing Minesweeper entities associated with this handler.
     *
     * @return The team-specific tag for removing Minesweeper entities.
     */
    public String getTeamRemoveTag() {
        return teamRemoveTag;
    }

    /**
     * Loads the Minesweeper game grid, game pieces, and initializes game mechanics.
     * This method is called during initialization to set up the Minesweeper game.
     */
    public void load() {
        List<Entity> entities = Bukkit.selectEntities(Bukkit.getConsoleSender(), String.format("@e[tag=%s]", teamRemoveTag));
        for (Entity entity : entities) {
            entity.remove();
        }
        completed = false;
        door.reset(null);
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int z = 0; z < GRID_SIZE; z++) {
                int value = BASE_GRID[x][z];
                gamePieces[x][z] = new GamePiece(plugin, this, location.clone().add(x, 0, z), value);
            }
        }

        // Start the game by sweeping the initial game piece.
        gamePieces[0][0].sweep();
    }

    /**
     * Converts a vector to its corresponding game piece on the Minesweeper grid.
     *
     * @param vector The vector representing the location.
     * @return The corresponding GamePiece, or null if the vector is outside the grid.
     */
    public @Nullable GamePiece vectorToGamePiece(Vector vector) {
        Vector gridVector = vectorToGrid(vector);
        if (gridVector != null) {
            return gamePieces[gridVector.getBlockX()][gridVector.getBlockZ()];
        } else {
            return null;
        }
    }

    /**
     * Converts a vector to its corresponding grid position on the Minesweeper grid.
     *
     * @param vector The vector representing the location.
     * @return The grid position vector, or null if the vector is outside the grid.
     */
    public @Nullable Vector vectorToGrid(Vector vector) {
        int x = vector.getBlockX() - location.getBlockX();
        int y = vector.getBlockY() - location.getBlockY();
        int z = vector.getBlockZ() - location.getBlockZ();
        if (0 <= x && x <= GRID_SIZE - 1
                && y == 0
                && 0 <= z && z <= GRID_SIZE - 1) {
            return new Vector(x, y, z);
        } else {
            return null;
        }
    }

    /**
     * Clears the game pieces around the specified game piece.
     *
     * @param gamePiece The game piece to clear around.
     * @param force     Whether to force the clearing of adjacent game pieces.
     */
    public void clearAround(GamePiece gamePiece, boolean force) {
        Location cornerLocation = gamePiece.getLocation().clone().add(-1, 0, -1);
        for (int x = cornerLocation.getBlockX(); x < (cornerLocation.getBlockX() + 3); x++) {
            for (int z = cornerLocation.getBlockZ(); z < (cornerLocation.getBlockZ() + 3); z++) {
                GamePiece checkPiece = vectorToGamePiece(new Vector(x, cornerLocation.getBlockY(), z));
                if (checkPiece != null) {
                    if (gamePiece.getMines() == 0) {
                        checkPiece.sweep();
                    } else if (gamePiece.getMines() > 0) {
                        if (force || checkPiece.getMines() == 0) {
                            checkPiece.sweep();
                        }
                    }
                }

            }
        }
    }

    /**
     * Clears the game pieces around the specified game piece.
     * Does not force non-zeros to clear.
     *
     * @param gamePiece The game piece to clear around.
     */
    public void clearAround(GamePiece gamePiece) {
        clearAround(gamePiece, false);
    }

    /**
     * Clears the adjacent game pieces around a swept game piece when flagged game pieces match mines count.
     *
     * @param gamePiece The game piece to clear sweep around.
     */
    public void clearSweep(GamePiece gamePiece) {
        Location cornerLocation = gamePiece.getLocation().clone().add(-1, 0, -1);
        int flags = 0;
        for (int x = cornerLocation.getBlockX(); x < (cornerLocation.getBlockX() + 3); x++) {
            for (int z = cornerLocation.getBlockZ(); z < (cornerLocation.getBlockZ() + 3); z++) {
                GamePiece checkPiece = vectorToGamePiece(new Vector(x, cornerLocation.getBlockY(), z));
                if (checkPiece != null && (checkPiece.hasFlag() || (checkPiece.getMines() < 0 && checkPiece.isSwept()))) {
                    flags++;
                }
            }
        }
        if (flags == gamePiece.getMines()) {
            clearAround(gamePiece, true);
        }
    }

    /**
     * Checks whether the Minesweeper game is completed.
     * If all non-mined game pieces have been swept, completes the room.
     */
    public void checkComplete() {
        boolean nowComplete = true;
        for (GamePiece[] row : gamePieces) {
            for (GamePiece gamePiece : row) {
                if (!gamePiece.isSwept() && !(gamePiece.getMines() < 0 && gamePiece.hasFlag())) {
                    Location pieceLocation = gamePiece.getLocation();
                    nowComplete = false;
                    break;
                }
            }
            if (!nowComplete) {
                break;
            }
        }
        if (nowComplete) {
            completeRoom();
        }
    }

    /**
     * Checks whether the Minesweeper game has been completed.
     *
     * @return {@code true} if the game is completed, {@code false} otherwise.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Completes the Minesweeper game by opening the associated door.
     * This method is called when all game pieces have been successfully swept.
     */
    public void completeRoom() {
        if (!completed) {
            completed = true;
            door.open(null);
        }
    }

    @EventHandler
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        Block block = event.getSourceBlock();
        GamePiece gamePiece = vectorToGamePiece(block.getLocation().toVector());
        if (gamePiece != null) {
            if (block.getBlockData() instanceof Brushable sandData) {
                if (sandData.getDusted() == 2) {
                    gamePiece.sweep();
                    checkComplete();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHit(final EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        UUID flagLink = NBTUtils.getUniqueID(plugin, Flag.FLAG_LINK_TAG, entity);
        if (flagLink != null && event.getDamager() instanceof Player) {
            GamePiece gamePiece = vectorToGamePiece(NBTUtils.getLocation(plugin, Flag.FLAG_LOCATION_TAG, entity).toVector());
            if (gamePiece != null) {
                gamePiece.removeFlag();
                event.getDamager().sendActionBar(Component.text("Removed Flag").color(Palette.NEGATIVE_COLOR.toTextColor()));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block != null && item != null) {
            GamePiece gamePiece = vectorToGamePiece(block.getLocation().toVector());
            if (gamePiece != null) {
                if (NBTUtils.boolState(plugin, Flag.FLAG_TAG, item)) {
                    if (gamePiece.placeFlag(item)) {
                        event.getPlayer().sendActionBar(TextUtil.formatText("Placed Flag"));
                    }
                    checkComplete();
                } else if (gamePiece.isSwept() && item.getType().equals(Material.BRUSH)) {
                    clearSweep(gamePiece);
                    checkComplete();
                }
            }
        }
    }

}
