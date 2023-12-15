package io.github.mctowerchallenge.mctcplugin.data.database;

import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserDB {

    private final DataSource dataSource;

    public UserDB(DataSource dataSource) {
        this.dataSource = dataSource;
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
