package io.github.mctowerchallenge.mctcplugin;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.Color;
import io.github.mctowerchallenge.mctcplugin.configs.DatabaseConfig;
import io.github.mctowerchallenge.mctcplugin.god.GodTeam;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Manages database operations for the plugin.
 * Handles interactions with the database for various functionalities.
 */
public class Database {

    private final MCTCPlugin plugin;
    private final DatabaseConfig config;
    private DataSource dataSource;

    /**
     * Constructs a new Database instance.
     * Initializes the database connection using the provided configuration.
     *
     * @param plugin         The main plugin instance.
     * @param databaseConfig The database configuration.
     */
    public Database(MCTCPlugin plugin, DatabaseConfig databaseConfig) {
        this.plugin = plugin;
        this.config = databaseConfig;
        MCTCPlugin.log("Loading database...");
        try {
            this.dataSource = initMySQLDataSource();
            MCTCPlugin.log("Database initialized!");
        } catch (SQLException e) {
            MCTCPlugin.log(e.getMessage());
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if the provided data source is valid by establishing a connection.
     *
     * @param dataSource The data source to test.
     * @throws SQLException If the connection cannot be established.
     */
    private void testDataSource(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1)) {
                throw new SQLException("Could not establish database connection.");
            }
        }
    }

    /**
     * Initializes a MySQL data source using the database configuration.
     *
     * @return The initialized data source.
     * @throws SQLException                  If there's an issue with the SQL connection.
     * @throws InvalidConfigurationException If the configuration is invalid.
     */
    private DataSource initMySQLDataSource() throws SQLException, InvalidConfigurationException {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        String host = config.getHost();
        int port = config.getPort();
        String database = config.getDatabase();
        String user = config.getUser();
        String password = config.getPassword();
        dataSource.setServerName(host.strip());
        dataSource.setPortNumber(port);
        dataSource.setDatabaseName(database.strip());
        dataSource.setUser(user.strip());
        dataSource.setPassword(password.strip());
        testDataSource(dataSource);
        return dataSource;
    }

    /**
     * Retrieves the players belonging to the given teams,
     * and sets the players to their proper teams.
     *
     * @param teams The teams to set players for.
     */
    public void setGameTeamPlayers(List<TowerTeam> teams) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT team_id, uuid FROM users;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, List<OfflinePlayer>> users = new HashMap<>();
            while (resultSet.next()) {
                String userIdString = resultSet.getString("uuid");
                try {
                    UUID userId = UUID.fromString(userIdString);
                    int teamId = resultSet.getInt("team_id");
                    if (!resultSet.wasNull()) {
                        List<OfflinePlayer> teamUsers = users.getOrDefault(teamId, new ArrayList<>());
                        teamUsers.add(Bukkit.getOfflinePlayer(userId));
                        users.put(teamId, teamUsers);
                    }
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid User Id: " + userIdString);
                }
            }
            for (TowerTeam team : teams) {
                List<OfflinePlayer> teamUsers = users.get(team.getDatabaseId());
                if (teamUsers != null) {
                    team.addAllPlayers(teamUsers);
                }
            }
        }
    }

    /**
     * Retrieves and creates the
     * object for the god team.
     *
     * @param teamManager Current team manager instance.
     * @return the God Team.
     */
    public @Nullable GodTeam getGodTeam(TeamManager teamManager) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT teams.id, teams.name, teams.color, teams.dye
                        FROM teams
                        WHERE teams.id = 1;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int color = resultSet.getInt("color");
                String dye = resultSet.getString("dye");
                return new GodTeam(plugin, teamManager, id, name, new Color(color), dye);
            } else {
                return null;
            }
        }
    }

    /**
     * Retrieves and creates all
     * non-disabled participant teams.
     *
     * @param teamManager Current team manager instance.
     * @return The list of participant teams.
     */
    public List<ParticipantTeam> getParticipantTeams(TeamManager teamManager) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT teams.id, teams.name, teams.color, teams.dye
                        FROM teams
                        WHERE teams.disabled = 0
                        AND teams.is_participant = 1;
                        """
        )) {
            List<ParticipantTeam> teams = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int color = resultSet.getInt("color");
                String dye = resultSet.getString("dye");
                ParticipantTeam team = new ParticipantTeam(plugin, teamManager, id, name, new Color(color), dye);
                teams.add(team);
            }
            return teams;
        }
    }

    /**
     * Entity tag for the armor stands
     * holding the frame borders.
     */
    public static final String FRAME_TAG = "portal-frame-colorborder";
    /**
     * Custom Model ID for the
     * portal frame borders.
     */
    private static final int FRAME_CUSTOM_MODEL = 1000;

    /**
     * Offset from the block location to
     * summon the armor stand for the borders.
     */
    private static final Vector FRAME_OFFSET = new Vector(0.5, -1.31, 0.5);

    /**
     * Places the portal frame borders for
     * all teams.
     */
    public void placePortalBorders() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT filled, facing, x, y, z, worlds.name AS world, t.color, t.name AS team_name FROM portalframes
                        LEFT JOIN worlds ON portalframes.world_id = worlds.id
                        INNER JOIN teams t on portalframes.team_id = t.id;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Location location = new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                BlockFace facing = BlockFace.valueOf(resultSet.getString("facing"));
                location.setDirection(facing.getDirection());
                Block block = location.getBlock();
                if (block.getType().equals(Material.END_PORTAL_FRAME)) {
                    EndPortalFrame blockData = (EndPortalFrame) block.getBlockData();
                    blockData.setFacing(facing);
                    blockData.setEye(resultSet.getBoolean("filled"));
                    block.setBlockData(blockData);
                } else {
                    Bukkit.getLogger().warning("End Portal location for " + resultSet.getString("team_name") + " is not a frame.");
                }
                Color color = new Color(resultSet.getInt("color"));
                ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(FRAME_OFFSET), EntityType.ARMOR_STAND);
                armorStand.addScoreboardTag(FRAME_TAG);
                armorStand.setItem(EquipmentSlot.HEAD, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
                    LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
                    meta.setColor(color.toBukkitColor());
                    meta.setCustomModelData(FRAME_CUSTOM_MODEL);
                    setItemMeta(meta);
                }});
                armorStand.setGravity(false);
                armorStand.setInvulnerable(true);
                armorStand.setInvisible(true);
                armorStand.setDisabledSlots(EquipmentSlot.values());
                Bukkit.getLogger().info("Loaded portal frame for " + resultSet.getString("team_name") + "at location " + location.getX() + " " + location.getY() + " " + location.getZ());
            }
        }
    }

    /**
     * Unloads all portal frame borders from the world.
     */
    public static void unloadPortalBorders() {
        CommandSender sender = Bukkit.createCommandSender(component -> {
        });
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", FRAME_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    /**
     * Sets a team's portal frame's
     * filled state in the database.
     *
     * @param team   The team to set
     * @param filled The state to set it to.
     */
    public void setPortalFrameFilled(ParticipantTeam team, boolean filled) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement updateFrameStatement = conn.prepareStatement(
                """
                        UPDATE portalframes
                        SET portalframes.filled = ?
                        WHERE portalframes.team_id = ?;
                        """
        )) {
            updateFrameStatement.setBoolean(1, filled);
            updateFrameStatement.setInt(2, team.getDatabaseId());
            updateFrameStatement.executeUpdate();
        }
    }

    /**
     * Gets and sets in-world the portal frame
     * for the given team.
     *
     * @param team The team to get the frame
     *             of.
     * @return The location of the portal frame.
     */
    public Location getPortalFrame(ParticipantTeam team) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement getFrameStatement = conn.prepareStatement(
                """
                        SELECT filled, facing, x, y, z, worlds.name FROM portalframes
                        LEFT JOIN worlds ON portalframes.world_id = worlds.id
                        WHERE portalframes.team_id = ?;
                        """
        )) {
            getFrameStatement.setInt(1, team.getDatabaseId());
            ResultSet resultSet = getFrameStatement.executeQuery();
            if (resultSet.next()) {
                Location location = new Location(Bukkit.getWorld(resultSet.getString("name")), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                BlockFace facing = BlockFace.valueOf(resultSet.getString("facing"));
                location.setDirection(facing.getDirection());
                Block block = location.getBlock();
                if (block.getType().equals(Material.END_PORTAL_FRAME)) {
                    EndPortalFrame blockData = (EndPortalFrame) block.getBlockData();
                    blockData.setFacing(facing);
                    blockData.setEye(resultSet.getBoolean("filled"));
                    block.setBlockData(blockData);
                } else {
                    Bukkit.getLogger().warning("End Portal location for " + team.getTextName() + " is not a frame.");
                }
                return location;
            } else {
                return null;
            }
        }
    }

    /**
     * Creates or updates a team's end portal frame location in the database.
     *
     * @param team     The team to update.
     * @param location The new location to set it to.
     * @param facing   The direction the block is facing.
     * @return The number of rows updated.
     */
    public int upsertPortalFrame(ParticipantTeam team, Location location, BlockFace facing) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO portalframes (team_id, x, y, z, facing, world_id)
                        VALUES (?, ?, ?, ?, ?, (SELECT worlds.id FROM worlds WHERE worlds.name = ?))
                        ON DUPLICATE KEY UPDATE portalframes.x        = VALUES(x),
                                                portalframes.y        = VALUES(y),
                                                portalframes.z        = VALUES(z),
                                                portalframes.facing   = VALUES(facing),
                                                portalframes.world_id = VALUES(world_id);
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            statement.setString(5, facing.name());
            statement.setString(6, location.getWorld().getName());
            return statement.executeUpdate();
        }
    }

    /**
     * Gets the number of remaining unfilled portal frames.
     *
     * @return The number of frames.
     */
    public int getRemainingPortalFrames() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT COUNT(DISTINCT p.id) AS count
                        FROM teams
                                 JOIN portalframes p on teams.id = p.team_id
                        WHERE teams.disabled = 0
                          AND p.filled = 0;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            return -1;
        }
    }

    /**
     * Creates/updates a player and
     * gives them the specified team.
     *
     * @param uuid the player {@link UUID}.
     * @param team The team to set the
     *             player to.
     * @return Whether the update
     * was successful.
     */
    public boolean upsertUserTeam(@NotNull UUID uuid, @NotNull TowerTeam team) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO users (uuid, team_id)
                        VALUES(?, ?)
                        ON DUPLICATE KEY UPDATE users.team_id = VALUES(team_id);
                        """
        )) {
            String userId = uuid.toString();
            int teamId = team.getDatabaseId();
            statement.setString(1, userId);
            statement.setInt(2, teamId);
            int rowCount = statement.executeUpdate();
            if (rowCount == 0) {
                Bukkit.getLogger().warning(String.format("Failed to update team for:\n    user: %s\n    team: %s", userId, team.getTextName()));
                return false;
            }
            return true;
        }
    }

    /**
     * Sets the pride flag associated with the player.
     *
     * @param uuid   The player's uuid.
     * @param flagId The database id of the flag.
     * @return The updated flag id of the player.
     * @throws SQLException if there's an SQL error.
     */
    public int setPlayerFlag(@NotNull UUID uuid, int flagId) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement update = conn.prepareStatement(
                """
                        UPDATE users
                        SET flag_id = ?
                        WHERE users.uuid = ?;
                        """
        )) {
            update.setInt(1, flagId);
            update.setString(2, uuid.toString());
            update.executeUpdate();
            return getPlayerFlag(uuid);
        }
    }

    /**
     * Gets the pride flag associated with the player.
     *
     * @param uuid The player's uuid.
     * @return The flag id of the player.
     * @throws SQLException if there's an SQL error.
     */
    public int getPlayerFlag(@NotNull UUID uuid) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement select = conn.prepareStatement(
                """
                        SELECT users.flag_id
                        FROM users
                        WHERE users.uuid = ?;
                        """
        )) {
            select.setString(1, uuid.toString());
            ResultSet resultSet = select.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("flag_id");
            } else {
                return 0;
            }
        }
    }

    /**
     * Gets the pride flags of all players.
     *
     * @return A map of player UUIDs to their flag ids.
     * @throws SQLException if there's an SQL error.
     */
    public Map<UUID, Integer> getAllPlayerFlags() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT users.uuid, users.flag_id
                        FROM users
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            Map<UUID, Integer> selections = new HashMap<>();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                int flagId = resultSet.getInt("flag_id");
                selections.put(uuid, flagId);
            }
            return selections;
        }
    }

}
