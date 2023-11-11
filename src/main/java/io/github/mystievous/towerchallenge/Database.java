package io.github.mystievous.towerchallenge;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mystievous.towerchallenge.configs.DatabaseConfig;
import io.github.mystievous.towerchallenge.decoration.CustomModel;
import io.github.mystievous.towerchallenge.god.GodTeam;
import io.github.mystievous.towerchallenge.hats.HatElement;
import io.github.mystievous.towerchallenge.quest.Quest;
import io.github.mystievous.towerchallenge.team.ParticipantTeam;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import io.github.mystievous.towerchallenge.utility.WorldNotStoredException;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    private final TowerChallenge plugin;
    private final DatabaseConfig config;
    private DataSource dataSource;

    /**
     * Constructs a new Database instance.
     * Initializes the database connection using the provided configuration.
     *
     * @param plugin         The main plugin instance.
     * @param databaseConfig The database configuration.
     */
    public Database(TowerChallenge plugin, DatabaseConfig databaseConfig) {
        this.plugin = plugin;
        this.config = databaseConfig;
        TowerChallenge.log("Loading database...");
        try {
            this.dataSource = initMySQLDataSource();
            TowerChallenge.log("Database initialized!");
        } catch (SQLException e) {
            TowerChallenge.log(e.getMessage());
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

    public Collection<UUID> getAllPlayers() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT uuid FROM users
                        WHERE users.team_id IS NOT NULL;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            Collection<UUID> users = new ArrayList<>();

            while (resultSet.next()) {
                String userIdString = resultSet.getString("uuid");
                try {
                    UUID userId = UUID.fromString(userIdString);
                    users.add(userId);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid User Id: " + userIdString);
                }
            }

            return users;
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
                        SELECT teams.id, teams.name, teams.color, teams.dye, t.name AS quest_name
                        FROM teams
                        LEFT JOIN tags t on teams.quest_tag_id = t.id
                        WHERE teams.id = 1;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int color = resultSet.getInt("color");
                String dye = resultSet.getString("dye");
                GodTeam team = new GodTeam(plugin, teamManager, id, name, new Color(color), dye);
                String questName = resultSet.getString("quest_name");
                if (!resultSet.wasNull()) {
                    team.setCurrentQuestTag(questName);
                }
                return team;
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
                        SELECT teams.id, teams.name, teams.color, teams.dye, t.name AS quest_name
                        FROM teams
                        LEFT JOIN tags t on teams.quest_tag_id = t.id
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
                String questName = resultSet.getString("quest_name");
                if (!resultSet.wasNull()) {
                    team.setCurrentQuestTag(questName);
                }
                teams.add(team);
            }
            return teams;
        }
    }

    /**
     * Gets the added scores for all teams,
     * associated by their database ids.
     *
     * @return The map with scores to ids.
     */
    public Map<Integer, Integer> getAddedScores() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT teams.id, teams.added_score FROM teams
                        WHERE teams.is_participant = 1
                        AND teams.disabled = 0;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, Integer> addedScores = new HashMap<>();
            while (resultSet.next()) {
                int teamId = resultSet.getInt("id");
                int addedScore = resultSet.getInt("added_score");
                addedScores.put(teamId, addedScore);
            }
            return addedScores;
        }
    }

    /**
     * Adds extra score to a team.
     *
     * @param team  The team to add to.
     * @param score The score to add.
     */
    public void addTeamScore(ParticipantTeam team, int score) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        UPDATE teams
                        SET added_score = added_score + ?
                        WHERE id = ?;
                        """
        )) {
            statement.setInt(1, score);
            statement.setInt(2, team.getDatabaseId());
            statement.executeUpdate();
        }
    }

    /**
     * Gets the current added score for a team.
     *
     * @param team The team to get.
     * @return The score.
     */
    public int getTeamScore(ParticipantTeam team) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT added_score FROM teams
                        WHERE id = ?;
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("added_score");
            } else {
                return 0;
            }
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

    // TODO: Change to item displays

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
     * Sets the current quest for a team in the database.
     *
     * @param team The team to set.
     * @param tag  The quest to set the team to.
     */
    public void setTeamQuest(TowerTeam team, String tag) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        UPDATE teams
                        SET teams.quest_tag_id = (SELECT id FROM tags WHERE tags.name = ?)
                        WHERE teams.id = ?;
                        """
        )) {
            statement.setString(1, tag);
            statement.setInt(2, team.getDatabaseId());
            statement.executeUpdate();
            team.setCurrentQuestTag(tag);
        }
    }

    /**
     * Gets the value of an objective for the given team.
     *
     * @param team The team to select.
     * @param tag  The quest TAG.
     * @param name The objective name.
     * @return The value of the objective.
     */
    public int getObjective(TowerTeam team, String tag, String name) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT value FROM objectives
                        WHERE objectives.team_id = ? AND
                            objectives.quest_tag_id = (SELECT id FROM tags WHERE tags.name = ?) AND
                            objectives.name = ?;
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setString(2, tag);
            statement.setString(3, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("value");
            }
            return 0;
        }
    }

    /**
     * Adds the given value to the objective for a team.
     *
     * @param team  The team to add to.
     * @param tag   The quest tag.
     * @param name  The objective name.
     * @param value The value to add.
     */
    public void addObjectiveScore(TowerTeam team, String tag, String name, int value) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO objectives (team_id, quest_tag_id, name, value)
                        VALUES (?, (SELECT id FROM tags WHERE tags.name = ?), ?, ?)
                        ON DUPLICATE KEY UPDATE value = value + VALUES(value);
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setString(2, tag);
            statement.setString(3, name);
            statement.setInt(4, value);
            statement.executeUpdate();
        }
    }

    /**
     * Checks whether an item is collected for a team.
     *
     * @param team The team to check for.
     * @param tag  The quest TAG.
     * @param uuid The item UUID.
     * @return True if the item is collected, false otherwise.
     */
    @Deprecated
    public boolean isItemCollected(TowerTeam team, String tag, String uuid) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT collected FROM collected_items
                        WHERE team_id = ?
                        AND tag_id = (SELECT id FROM tags WHERE name = ?)
                        AND uuid = ?;
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setString(2, tag);
            statement.setString(3, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("collected");
            }
            return false;
        }
    }

    /**
     * Sets the collected state of an item for a team.
     *
     * @param team      The team to set for.
     * @param tag       The quest TAG.
     * @param uuid      The item UUID.
     * @param collected The collected state.
     * @return True if the update was successful, false otherwise.
     */
    @Deprecated
    public boolean setItemCollected(TowerTeam team, String tag, String uuid, boolean collected) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO collected_items (team_id, tag_id, uuid, collected)
                        VALUES (?, (SELECT id FROM tags WHERE name = ?), ?, ?)
                        ON DUPLICATE KEY UPDATE collected = VALUES(collected);
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setString(2, tag);
            statement.setString(3, uuid);
            statement.setBoolean(4, collected);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Sets a team as the winners, granting access to the crown.
     *
     * @param team The team to set as winners.
     */
    public void updateWinningTeam(@NotNull TowerTeam team) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement clearWinnersStatement = conn.prepareStatement(
                """
                        DELETE FROM user_hatgroups
                        WHERE
                            user_hatgroups.hatgroup_id = 8;
                            """
        ); PreparedStatement addWinnersStatement = conn.prepareStatement(
                """
                            INSERT INTO user_hatgroups (user_uuid, hatgroup_id)
                            VALUES (?, 8);
                        """
        )) {
            clearWinnersStatement.executeUpdate();
            ItemStack hat = getHat(16).getItem();
            for (OfflinePlayer player : team.getOfflinePlayers()) {
                String userId = player.getUniqueId().toString();
                addWinnersStatement.setString(1, userId);
                Player onlinePlayer = player.getPlayer();
                if (onlinePlayer != null) {
                    onlinePlayer.getInventory().setHelmet(hat);
                }
                int rowCount = addWinnersStatement.executeUpdate();
                if (rowCount == 0) {
                    Bukkit.getLogger().warning(String.format("Failed to set winner:\n    user: %s", userId));
                    return;
                }
            }
        }
    }

    /**
     * Gives a team a specific hat group.
     *
     * @param hatGroupId The hat group to give.
     * @param team       The team to give the hats.
     */
    public void giveTeamHatGroup(int hatGroupId, @NotNull TowerTeam team) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO user_hatgroups (user_uuid, hatgroup_id)
                        VALUES (?, ?)
                        """
        )) {
            for (OfflinePlayer player : team.getOfflinePlayers()) {
                if (player.isOnline() || player.hasPlayedBefore()) {
                    String userId = player.getUniqueId().toString();
//                    Bukkit.getLogger().info(String.format("Giving hatgroup %d to user %s", hatGroupId, userId));
                    statement.setString(1, userId);
                    statement.setInt(2, hatGroupId);
                    statement.executeUpdate();
                }
            }
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
     * Updates the hat color for a
     * specific player.
     *
     * @param uuid  The player's {@link UUID}.
     * @param color The color to set for the player.
     */
    public void updatePlayerColor(@NotNull UUID uuid, @Nullable Color color) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        UPDATE users
                        SET color = ?
                        WHERE users.uuid = ?;
                        """
        )) {
            String userId = uuid.toString();
            if (color != null) {
                statement.setInt(1, color.intValue());
            } else {
                statement.setNull(1, Types.INTEGER);
            }
            statement.setString(2, userId);
            int rowCount = statement.executeUpdate();
            if (rowCount == 0) {
                Bukkit.getLogger().warning(String.format("Failed to update color for:\n    user: %s\n    color: %s", userId, color));
            }
        }
    }

    /**
     * Takes the result set from getting player
     * hats and returns the element for
     * the current row.
     *
     * @param resultSet The result set from the
     *                  hat query.
     * @return The hat element.
     * @throws IllegalArgumentException If the material of
     *                                  the hat is invalid.
     */
    public HatElement getHatFromResultSet(ResultSet resultSet) throws SQLException, IllegalArgumentException {
        String name = resultSet.getString("name");
        Material material;
        material = Material.valueOf(resultSet.getString("material"));
        int customModelData = resultSet.getInt("custom_model_data");
        int colorInt = resultSet.getInt("color");
        Color color = null;
        if (!resultSet.wasNull()) {
            color = new Color(colorInt);
        }
        String author = resultSet.getString("author");
        String referenced = resultSet.getString("referenced");
        boolean handheld = resultSet.getBoolean("handheld");
        return new HatElement(name, material, customModelData, color, author, referenced, handheld);
    }

    /**
     * Gets a specific hat from
     * the database.
     *
     * @param hatId The hat to get.
     * @return The hat element.
     */
    public HatElement getHat(int hatId) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT
                            hats.id,
                            hats.name,
                            materials.name AS material,
                            hats.custom_model_data,
                            hats.color,
                            author_people.name AS author,
                            referenced_people.name AS referenced,
                            hats.handheld
                        FROM
                            hats
                                INNER JOIN
                            materials ON hats.material_id = materials.id
                                LEFT JOIN
                            hat_people AS author_people ON hats.author_id = author_people.id
                                LEFT JOIN
                            hat_people AS referenced_people ON hats.referenced_id = referenced_people.id
                        WHERE hats.id = ?;
                        """
        )) {
            statement.setInt(1, hatId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return getHatFromResultSet(resultSet);
            }
            return null;
        }
    }

    /**
     * Gets all hats for the given player.
     *
     * @param uuid The player's {@link UUID}
     * @return The list of elements for the hats.
     */
    public List<Element> getPlayerHats(@NotNull UUID uuid) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT * FROM user_hats
                        WHERE user_hats.user_uuid = ?;
                        """
        )) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            List<Element> hats = new ArrayList<>();
            while (resultSet.next()) {
                try {
                    hats.add(getHatFromResultSet(resultSet));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid Hat: " + resultSet.getString("name"));
                }
            }
            return hats;
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

    /**
     * Retrieves model groups from the database.
     *
     * @return A list of elements representing the model groups.
     * @throws SQLException             If there's an error interacting with the database.
     * @throws IllegalArgumentException If the material name is invalid.
     */
    public List<Element> getModelGroupGuis() throws SQLException, IllegalArgumentException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement modelGroups = conn.prepareStatement(
                """
                        SELECT id, name FROM modelgroups
                        ORDER BY name;
                        """
        ); PreparedStatement models = conn.prepareStatement(
                """
                        SELECT mm.modelgroup_id AS modelgroup, models.name AS model, m.name AS material, models.custom_model_data, hp.name AS author
                        FROM models
                                 LEFT JOIN materials m on models.material_id = m.id
                                 LEFT JOIN hat_people hp on models.author_id = hp.id
                                 LEFT JOIN model_modelgroups mm on models.id = mm.model_id
                        ORDER BY modelgroup ASC, model ASC, priority DESC;
                        """
        )) {
            Map<Integer, ListGui> groups = new HashMap<>();
            ResultSet groupsResultSet = modelGroups.executeQuery();
            while (groupsResultSet.next()) {
                int id = groupsResultSet.getInt("id");
                String name = groupsResultSet.getString("name");
                groups.put(id, new ListGui(plugin, Component.text(name), Element.blank()));
            }

            ResultSet modelsResultSet = models.executeQuery();
            while (modelsResultSet.next()) {
                int groupId = modelsResultSet.getInt("modelgroup");
                String name = modelsResultSet.getString("model");
                Material material = Material.valueOf(modelsResultSet.getString("material"));
                int customModelData = modelsResultSet.getInt("custom_model_data");
                String author = modelsResultSet.getString("author");
                groups.get(groupId).addElement(new CustomModel(name, material, customModelData, null, author, true).getGiveElement());
            }
            List<Element> elements = new ArrayList<>();
            for (ListGui listGui : groups.values()) {
                ItemStack item = listGui.getFirstInventory().getItem(0);
                if (item != null) {
                    ItemStack representation = item.clone();
                    ItemMeta meta = representation.getItemMeta();
                    meta.displayName(TextUtil.noItalic(listGui.getInventoryTitle()));
                    meta.lore(new ArrayList<>());
                    representation.setItemMeta(meta);
                    ButtonElement groupButton = new ButtonElement(representation, listGui::openInventory);
                    elements.add(groupButton);
                }
            }
            return elements;
        }
    }

    /**
     * Retrieves the model element for a specific model from the database.
     *
     * @param databaseId The database ID of the model.
     * @param showAuthor Whether the author of the model should be shown.
     * @param debug      Whether to show material and custom model ID.
     * @return The element for the model.
     * @throws SQLException If there's an SQL error.
     */
    public CustomModel getModel(int databaseId, boolean showAuthor, boolean debug) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT models.name AS model, m.name AS material, models.custom_model_data, hp.name AS author
                                FROM models
                                         LEFT JOIN materials m on models.material_id = m.id
                                         LEFT JOIN hat_people hp on models.author_id = hp.id
                                         WHERE models.id = ?;
                        """
        )) {
            statement.setInt(1, databaseId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("model");
                Material material = Material.valueOf(resultSet.getString("material"));
                int customModelData = resultSet.getInt("custom_model_data");
                String author = null;
                if (showAuthor) {
                    author = resultSet.getString("author");
                }
                return new CustomModel(name, material, customModelData, null, author, debug);
            }
            return null;
        }
    }

    /**
     * Retrieves the model ID for a specific custom model.
     *
     * @param material        The material of the model.
     * @param customModelData The custom model id of the model.
     * @return The database ID for the model.
     * @throws SQLException if there's an SQL error.
     */
    public @Nullable Integer getModelId(@NotNull Material material, int customModelData) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT models.id FROM models
                        WHERE models.material_id = (SELECT id FROM materials WHERE materials.name = ?)
                        AND models.custom_model_data = ?;
                        """
        )) {
            statement.setString(1, material.name());
            statement.setInt(2, customModelData);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            return null;
        }
    }

    /**
     * Retrieves the model ID for the given item stack.
     *
     * @param itemStack The item stack to query.
     * @return The database ID of the model.
     * @throws SQLException if there's an SQL error.
     */
    public @Nullable Integer getModelId(ItemStack itemStack) throws SQLException {
        return getModelId(itemStack.getType(), itemStack.getItemMeta().getCustomModelData());
    }

    /**
     * Gets the database ID of the given world.
     *
     * @param name The name of the world.
     * @return The database ID.
     * @throws WorldNotStoredException If the world is not in the database.
     * @throws SQLException            If there's an SQL error.
     */
    public Integer getWorldId(String name) throws SQLException, WorldNotStoredException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT worlds.id FROM worlds
                        WHERE worlds.name = ?;
                        """
        )) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            throw new WorldNotStoredException("This world is not stored in the database.");
        }
    }

    /**
     * Adds a waterdrip to the world in the database.
     *
     * @param location The location to add the waterdrip at.
     * @throws WorldNotStoredException If the world is not in the database.
     * @throws SQLException            If there's an SQL error.
     */
    public void addWaterDrip(Location location) throws SQLException, WorldNotStoredException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO waterdrips(x, y, z, world_id)
                        VALUES (?, ?, ?, ?);
                        """
        )) {
            statement.setInt(1, location.getBlockX());
            statement.setInt(2, location.getBlockY());
            statement.setInt(3, location.getBlockZ());
            statement.setInt(4, getWorldId(location.getWorld().getName()));
            statement.executeUpdate();
        }
    }

    /**
     * Removes a waterdrip from the world in the database.
     *
     * @param location The location of the waterdrip to remove.
     * @return True if the removal was successful, false otherwise.
     * @throws WorldNotStoredException If the world is not in the database.
     * @throws SQLException            If there's an SQL error.
     */
    public boolean removeWaterDrip(Location location) throws SQLException, WorldNotStoredException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        DELETE FROM waterdrips
                        WHERE x = ? && y = ? && z = ? && world_id = ?;
                        """
        )) {
            statement.setInt(1, location.getBlockX());
            statement.setInt(2, location.getBlockY());
            statement.setInt(3, location.getBlockZ());
            statement.setInt(4, getWorldId(location.getWorld().getName()));
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Gets all water drips from the database.
     *
     * @return The collection of locations for water drips.
     * @throws SQLException If there's an SQL error.
     */
    public Collection<Location> getWaterDrips() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT waterdrips.x, waterdrips.y, waterdrips.z, w.name FROM waterdrips
                        INNER JOIN worlds w on waterdrips.world_id = w.id
                        """
        )) {
            Collection<Location> locations = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String worldName = resultSet.getString("name");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    Bukkit.getLogger().warning(String.format("World %s does not exist.", worldName));
                    continue;
                }
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                int z = resultSet.getInt("z");
                Location location = new Location(world, x, y, z);
                locations.add(location);
            }

            return locations;
        }
    }

    public Map<Integer, Collection<String>> getCompletedQuests() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
            """
                    SELECT teams.id AS team_id, tags.name FROM completed_quests
                    INNER JOIN teams ON completed_quests.team_id = teams.id
                    INNER JOIN tags ON completed_quests.tag_id = tags.id;                
                    """
        )) {
            Map<Integer, Collection<String>> completedQuests = new HashMap<>();

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                String quest = resultSet.getString("name");

                Collection<String> quests = completedQuests.getOrDefault(teamId, new ArrayList<>());
                quests.add(quest);
                completedQuests.put(teamId, quests);

            }

            return completedQuests;
        }
    }

    public void setCompletedQuest(TowerTeam team, Quest quest) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
            """
                    INSERT INTO completed_quests(team_id, tag_id)
                    VALUES (?, (SELECT id FROM tags WHERE name = ?));
                    """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setString(2, quest.getTag());
            statement.executeUpdate();
        }
    }

}
