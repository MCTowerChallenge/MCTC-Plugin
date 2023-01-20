package io.github.mystievous.towerchallenge;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.mystievous.towerchallenge.configs.DatabaseConfig;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.hats.HatElement;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {

    public static HatElement getHatFromResultSet(ResultSet resultSet) throws SQLException, IllegalArgumentException {
        String name = resultSet.getString("name");
        Material material;
        material = Material.valueOf(resultSet.getString("material"));
        int customModelData = resultSet.getInt("custom_model_data");
        Color color = new Color(resultSet.getInt("color"));
        String author = resultSet.getString("author");
        String referenced = resultSet.getString("referenced");
        return new HatElement(name, material, customModelData, color, author, referenced);
    }

    private final DatabaseConfig config;
    private DataSource dataSource;

    public Database(DatabaseConfig databaseConfig) {
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

    public boolean updateWinningTeam(@NotNull TowerTeam team) throws SQLException {
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
                    return false;
                }
            }
        }
        return true;
    }

    public boolean upsertUserTeam(@NotNull UUID uuid, @NotNull TowerTeam team) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO users (uuid, team_id)
                        SELECT ?, teams.id
                        FROM teams
                        WHERE teams.name = ?
                        ON DUPLICATE KEY UPDATE users.team_id = teams.id;
                        """
        )) {
            String userId = uuid.toString();
            String teamName = team.getTextName();
            statement.setString(1, userId);
            statement.setString(2, teamName);
            int rowCount = statement.executeUpdate();
            if (rowCount == 0) {
                Bukkit.getLogger().warning(String.format("Failed to update team for:\n    user: %s\n    team: %s", userId, team));
                return false;
            }
            return true;
        }
    }

    public boolean updatePlayerColor(@NotNull UUID uuid, @Nullable Color color) throws SQLException {
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
                return false;
            }
            return true;
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

    public List<Element> getPlayerHatElements(@NotNull UUID uuid) throws SQLException {
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

}
