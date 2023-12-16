package io.github.mctowerchallenge.mctcplugin.data.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.data.config.DatabaseConfig;
import org.bukkit.configuration.InvalidConfigurationException;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Manages database operations for the plugin.
 * Handles interactions with the database for various functionalities.
 */
public class Database {

    private final MCTCPlugin plugin;
    private final DatabaseConfig config;
    private DataSource dataSource;

    private UserDB userDB;
    private TeamDB teamDB;
    private PortalFrameDB portalFrameDB;

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
            userDB = new UserDB(this.dataSource);
            teamDB = new TeamDB(plugin, this.dataSource);
            portalFrameDB = new PortalFrameDB(this.dataSource);
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

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public UserDB getUsersDB() {
        return userDB;
    }

    public TeamDB getTeamDB() {
        return teamDB;
    }

    public PortalFrameDB getPortalFrameDB() {
        return portalFrameDB;
    }
}
