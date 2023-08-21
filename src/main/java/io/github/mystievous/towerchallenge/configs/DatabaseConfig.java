package io.github.mystievous.towerchallenge.configs;

import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the configuration for the database connection.
 * This class provides methods to retrieve database-related configuration values.
 */
public class DatabaseConfig {

    private final TowerChallenge plugin;

    /**
     * Constructs a new DatabaseConfig instance associated with the provided plugin.
     *
     * @param plugin The main plugin instance.
     */
    public DatabaseConfig(TowerChallenge plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the configuration section for the database from the plugin's default config.
     *
     * @return The database config section.
     */
    private @Nullable ConfigurationSection getConfigSection() {
        return plugin.getConfig().getConfigurationSection("database");
    }

    /**
     * Retrieves the host address for the database connection.
     *
     * @return The host address as a string.
     * @throws InvalidConfigurationException If the host configuration is missing or invalid.
     */
    public String getHost() throws InvalidConfigurationException {
        final String key = "host";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isString(key)) {
            return dbConfig.getString(key);
        } else {
            throw new InvalidConfigurationException("No database host set in the config.");
        }
    }

    /**
     * Retrieves the port number for the database connection.
     * If the port configuration is missing or invalid, the default port 3306 is used.
     *
     * @return The port number.
     */
    public int getPort() {
        final String key = "port";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isInt(key)) {
            return dbConfig.getInt(key);
        } else {
            return 3306; // Default port for MySQL databases
        }
    }

    /**
     * Retrieves the database name for the connection.
     *
     * @return The database name as a string.
     * @throws InvalidConfigurationException If the database name configuration is missing or invalid.
     */
    public String getDatabase() throws InvalidConfigurationException {
        final String key = "database";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isString(key)) {
            return dbConfig.getString(key);
        } else {
            throw new InvalidConfigurationException("No database name set in the config.");
        }
    }

    /**
     * Retrieves the username for the database connection.
     *
     * @return The username as a string.
     * @throws InvalidConfigurationException If the username configuration is missing or invalid.
     */
    public String getUser() throws InvalidConfigurationException {
        final String key = "user";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isString(key)) {
            return dbConfig.getString(key);
        } else {
            throw new InvalidConfigurationException("No database user set in the config.");
        }
    }

    /**
     * Retrieves the password for the database connection.
     *
     * @return The password as a string.
     * @throws InvalidConfigurationException If the password configuration is missing or invalid.
     */
    public String getPassword() throws InvalidConfigurationException {
        final String key = "password";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isString(key)) {
            return dbConfig.getString(key);
        } else {
            throw new InvalidConfigurationException("No database user set in the config.");
        }
    }


}
