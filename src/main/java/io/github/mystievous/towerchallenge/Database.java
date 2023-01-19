package io.github.mystievous.towerchallenge;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.mystievous.towerchallenge.configs.DatabaseConfig;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    private final DatabaseConfig config;
    private DataSource dataSource;

    public Database(DatabaseConfig databaseConfig) {
        this.config = databaseConfig;
        ChallengeManager.log("Loading database...");
        try {
            this.dataSource = initMySQLDataSource();
            ChallengeManager.log("Database initialized!");
        } catch (SQLException e) {
            ChallengeManager.log(e.getMessage());
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void updateUserTeam(UUID uuid, @NotNull TowerTeam team) {

        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO users (uuid, team_id) " +
                        "SELECT ?, users.team_id " +
                        "FROM users " +
                        "INNER JOIN teams " +
                        "ON teams.name = ? " +
                        "ON DUPLICATE KEY UPDATE users.team_id = teams.id;"
        )) {
            statement.setString(1, uuid.toString());
            statement.setString(2, team.getTextName());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            ChallengeManager.log("Why is this null");
            e.printStackTrace();
            try {
                this.dataSource = initMySQLDataSource();
            } catch (SQLException | InvalidConfigurationException ex) {
                ex.printStackTrace();
            }
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
//        ChallengeManager.log(host + " " + port + " " + database + " " + user + " " + password);
        dataSource.setServerName(host.strip());
        dataSource.setPortNumber(port);
        dataSource.setDatabaseName(database.strip());
        dataSource.setUser(user.strip());
        dataSource.setPassword(password.strip());
        testDataSource(dataSource);
        return dataSource;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

}
