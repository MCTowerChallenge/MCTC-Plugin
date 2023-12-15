package io.github.mctowerchallenge.mctcplugin.configs;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents the configuration for the database connection.
 * This class provides methods to retrieve database-related configuration values.
 */
public class DatabaseConfig {

    private final YamlDocument databaseConfig;

    /**
     * Constructs a new DatabaseConfig instance associated with the provided plugin.
     *
     * @param plugin The main plugin instance.
     */
    public DatabaseConfig(MCTCPlugin plugin) {
        try {
            databaseConfig = YamlDocument.create(
                    new File(plugin.getDataFolder(), "database.yml"),
                    Objects.requireNonNull(plugin.getResource("database.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the configuration section for the database from the plugin's default config.
     *
     * @return The database config section.
     */
    private @Nullable Section getConfigSection() {
        if (databaseConfig == null) {
            return null;
        }
        return databaseConfig.getSection("database");
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
