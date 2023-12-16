package io.github.mctowerchallenge.mctcplugin.data.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.github.mctowerchallenge.mctcplugin.utility.ConfigUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Represents the configuration for the database connection.
 * This class provides methods to retrieve database-related configuration values.
 */
public class DatabaseConfig {

    private final YamlDocument config;

    /**
     * Constructs a new DatabaseConfig instance associated with the provided plugin.
     *
     * @param plugin The main plugin instance.
     */
    public DatabaseConfig(Plugin plugin) throws IOException {
        config = ConfigUtils.initConfigFile(plugin, "database.yml");
    }

    /**
     * Gets the configuration section for the database from the plugin's default config.
     *
     * @return The database config section.
     */
    private @Nullable Section getConfigSection() {
        if (config == null) {
            return null;
        }
        return config.getSection("database");
    }

    /**
     * Retrieves the host address for the database connection.
     *
     * @return The host address as a string.
     * @throws InvalidConfigurationException If the host configuration is missing or invalid.
     */
    public String getHost() throws InvalidConfigurationException {
        final String key = "host";
        Section dbConfig = getConfigSection();
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
        Section dbConfig = getConfigSection();
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
        Section dbConfig = getConfigSection();
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
        Section dbConfig = getConfigSection();
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
        Section dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isString(key)) {
            return dbConfig.getString(key);
        } else {
            throw new InvalidConfigurationException("No database user set in the config.");
        }
    }


}
