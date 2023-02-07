package io.github.mystievous.towerchallenge;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.mystievous.towerchallenge.configs.DatabaseConfig;
import io.github.mystievous.towerchallenge.decoration.ModelElement;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.ListGui;
import io.github.mystievous.towerchallenge.hats.HatElement;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import io.github.mystievous.towerchallenge.utility.Color;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

public class Database {

    public static HatElement getHatFromResultSet(ResultSet resultSet) throws SQLException, IllegalArgumentException {
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
        return new HatElement(name, material, customModelData, color, author, referenced);
    }

    private final TowerChallenge plugin;
    private final DatabaseConfig config;
    private DataSource dataSource;

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

    private void testDataSource(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1)) {
                throw new SQLException("Could not establish database connection.");
            }
        }
    }

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
                    team.setCurrentQuestId(questName);
                }
                return team;
            } else {
                return null;
            }
        }
    }

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
                    team.setCurrentQuestId(questName);
                }
                teams.add(team);
            }
            return teams;
        }
    }

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
                int teamId = resultSet.getInt("team_id");
                int addedScore = resultSet.getInt("added_score");
                addedScores.put(teamId, addedScore);
            }
            return addedScores;
        }
    }

    public boolean addTeamScore(ParticipantTeam team, int score) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        UPDATE teams
                        SET added_score = added_score + ?
                        WHERE id = ?;
                        """
        )) {
            statement.setInt(1, score);
            statement.setInt(2, team.getDatabaseId());
            return statement.executeUpdate() > 0;
        }
    }

    public static final String FRAME_TAG = "portal-frame-colorborder";
    private static final int FRAME_CUSTOM_MODEL = 1006;
    private static final org.bukkit.util.Vector FRAME_OFFSET = new Vector(0.5, -1.31, 0.5);

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

    public static void unloadPortalBorders() {
        CommandSender sender = Bukkit.createCommandSender(component -> {
        });
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", FRAME_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    public boolean setPortalFrameFilled(ParticipantTeam team, boolean filled) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement updateFrameStatement = conn.prepareStatement(
                """
                        UPDATE portalframes
                        SET portalframes.filled = ?
                        WHERE portalframes.team_id = ?;
                        """
        )) {
            updateFrameStatement.setBoolean(1, filled);
            updateFrameStatement.setInt(2, team.getDatabaseId());
            return updateFrameStatement.executeUpdate() > 0;
        }
    }

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

    public String getTeamQuest(TowerTeam team) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT tags.name 
                        FROM tags
                        WHERE tags.id = (SELECT quest_tag_id FROM teams WHERE id = ?);
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
            return null;
        }
    }

    public boolean setTeamQuest(TowerTeam team, String tag) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        UPDATE teams
                        SET teams.quest_tag_id = (SELECT id FROM tags WHERE tags.name = ?)
                        WHERE teams.id = ?;   
                        """
        )) {
            statement.setString(1, tag);
            statement.setInt(2, team.getDatabaseId());
            if (statement.executeUpdate() > 0) {
                team.setCurrentQuestId(tag);
                return true;
            }
            return false;
        }
    }

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

    public Map<String, Integer> getObjectives(TowerTeam team, String tag) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT objectives.name, value FROM objectives
                        WHERE objectives.team_id = ? AND
                            objectives.quest_tag_id = (SELECT id FROM tags WHERE tags.name = ?);
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setString(2, tag);
            ResultSet resultSet = statement.executeQuery();
            Map<String, Integer> objectives = new HashMap<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int value = resultSet.getInt("value");
                objectives.put(name, value);
            }
            return objectives;
        }
    }

    public boolean addObjectiveScore(TowerTeam team, String tag, String name, int value) throws SQLException {
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
            return statement.executeUpdate() > 0;
        }
    }

    public boolean isItemCollected(TowerTeam team, String tag, UUID uuid) throws SQLException {
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
            statement.setString(3, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("collected");
            }
            return false;
        }
    }

    public boolean setItemCollected(TowerTeam team, String tag, UUID uuid, boolean collected) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO collected_items (team_id, tag_id, uuid, collected)
                        VALUES (?, (SELECT id FROM tags WHERE name = ?), ?, ?)
                        ON DUPLICATE KEY UPDATE collected = VALUES(collected);
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setString(2, tag);
            statement.setString(3, uuid.toString());
            statement.setBoolean(4, collected);
            return statement.executeUpdate() > 0;
        }
    }

    public @Nullable Integer getPlayerTeamId(@NotNull UUID uuid) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                "SELECT team_id FROM users WHERE users.uuid = ?"
        )) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                if (!resultSet.wasNull()) {
                    return teamId;
                }
            }
            return null;
        }
    }

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
            for (OfflinePlayer player : team.getPlayers()) {
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
                            referenced_people.name AS referenced
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

    public List<Element> getPlayerHats(@NotNull UUID uuid) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT
                        uuid as user_uuid,
                            name,
                            material,
                            custom_model_data,
                            color,
                            author,
                            referenced
                        FROM
                            (SELECT
                                    users.uuid,
                                    COALESCE(hat_hatgroups.color, hats.color, users.color, teams.color) AS color,
                                    hats.name,
                                    materials.name AS material,
                                    hats.custom_model_data,
                                    referenced_people.name AS referenced,
                                    author_people.name AS author,
                                    hats.priority AS hat_priority,
                                    hatgroups.priority AS hatgroups_priority
                            FROM
                                users
                            INNER JOIN teams ON users.team_id = teams.id
                            JOIN global_hatgroups
                            INNER JOIN hatgroups ON global_hatgroups.hatgroup_id = hatgroups.id
                            INNER JOIN hat_hatgroups ON hatgroups.id = hat_hatgroups.hatgroup_id
                            INNER JOIN hats ON hat_hatgroups.hat_id = hats.id
                            INNER JOIN materials ON hats.material_id = materials.id
                            LEFT JOIN hat_people AS author_people ON hats.author_id = author_people.id
                            LEFT JOIN hat_people AS referenced_people ON hats.referenced_id = referenced_people.id UNION SELECT
                                    users.uuid,
                                    COALESCE(hat_hatgroups.color, hats.color, users.color, teams.color) AS color,
                                    hats.name,
                                    materials.name AS material,
                                    hats.custom_model_data,
                                    referenced_people.name AS referenced,
                                    author_people.name AS author,
                                    hats.priority AS hat_priority,
                                    hatgroups.priority AS hatgroups_priority
                            FROM
                                users
                            INNER JOIN teams ON users.team_id = teams.id
                            INNER JOIN team_hatgroups ON teams.id = team_hatgroups.team_id
                            INNER JOIN hatgroups ON team_hatgroups.hatgroup_id = hatgroups.id
                            INNER JOIN hat_hatgroups ON hatgroups.id = hat_hatgroups.hatgroup_id
                            INNER JOIN hats ON hat_hatgroups.hat_id = hats.id
                            INNER JOIN materials ON hats.material_id = materials.id
                            LEFT JOIN hat_people AS author_people ON hats.author_id = author_people.id
                            LEFT JOIN hat_people AS referenced_people ON hats.referenced_id = referenced_people.id UNION SELECT
                                    users.uuid,
                                    COALESCE(hat_hatgroups.color, hats.color, users.color, teams.color) AS color,
                                    hats.name,
                                    materials.name AS material,
                                    hats.custom_model_data,
                                    referenced_people.name AS referenced,
                                    author_people.name AS author,
                                    hats.priority AS hat_priority,
                                    hatgroups.priority AS hatgroups_priority
                            FROM
                                users
                            INNER JOIN teams ON users.team_id = teams.id
                            INNER JOIN user_hatgroups ON users.uuid = user_hatgroups.user_uuid
                            INNER JOIN hatgroups ON user_hatgroups.hatgroup_id = hatgroups.id
                            INNER JOIN hat_hatgroups ON hatgroups.id = hat_hatgroups.hatgroup_id
                            INNER JOIN hats ON hat_hatgroups.hat_id = hats.id
                            INNER JOIN materials ON hats.material_id = materials.id
                            LEFT JOIN hat_people AS author_people ON hats.author_id = author_people.id
                            LEFT JOIN hat_people AS referenced_people ON hats.referenced_id = referenced_people.id
                            ORDER BY hat_priority) AS result
                        WHERE
                            result.uuid = ?
                        ORDER BY hatgroups_priority DESC , hat_priority DESC , name ASC;
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
     * Grabs the modelgroups from the database
     *
     * @return A list of elements leading to the model groups
     * @throws SQLException             If there is an error interacting with the database
     * @throws IllegalArgumentException If the material name is invalid
     */
    public List<Element> getModelGroups() throws SQLException, IllegalArgumentException {
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
                groups.put(id, new ListGui(Component.text(name), Element.empty()));
            }

            ResultSet modelsResultSet = models.executeQuery();
            while (modelsResultSet.next()) {
                int groupId = modelsResultSet.getInt("modelgroup");
                String name = modelsResultSet.getString("model");
                Material material = Material.valueOf(modelsResultSet.getString("material"));
                int customModelData = modelsResultSet.getInt("custom_model_data");
                String author = modelsResultSet.getString("author");
                groups.get(groupId).addElement(new ModelElement(name, material, customModelData, null, author, true));
            }
            List<Element> elements = new ArrayList<>();
            for (ListGui listGui : groups.values()) {
                ItemStack item = listGui.getFirstInventory().getItem(0);
                if (item != null) {
                    ItemStack representation = item.clone();
                    ItemMeta meta = representation.getItemMeta();
                    meta.displayName(TextUtil.noItalic(listGui.getInventoryTitle()));
                    representation.setItemMeta(meta);
                    ButtonElement groupButton = new ButtonElement(representation, listGui::openInventory);
                    elements.add(groupButton);
                }
            }
            return elements;
        }
    }

    public Element getModel(int databaseId, boolean debug) throws SQLException {
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
                String author = resultSet.getString("author");
                return new ModelElement(name, material, customModelData, null, author, debug);
            }
            return null;
        }
    }

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
                int id = resultSet.getInt("id");
                return id;
            }
            return null;
        }
    }

    public @Nullable Integer getModelId(ItemStack itemStack) throws SQLException {
        return getModelId(itemStack.getType(), itemStack.getItemMeta().getCustomModelData());
    }

}
