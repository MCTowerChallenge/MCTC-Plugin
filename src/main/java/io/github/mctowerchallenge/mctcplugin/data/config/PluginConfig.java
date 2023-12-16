package io.github.mctowerchallenge.mctcplugin.data.config;

import org.bukkit.plugin.Plugin;

import java.io.IOException;

/**
 * Represents the configuration manager for the plugin.
 * This class handles loading and managing various configuration settings.
 */
public class PluginConfig {

    private final DatabaseConfig dbConfig;

    /**
     * Constructs a new Config instance for the plugin.
     * Loads default configuration settings and initializes the database configuration.
     *
     * @param plugin The main plugin instance.
     */
    public PluginConfig(Plugin plugin) throws IOException {

        dbConfig = new DatabaseConfig(plugin);

    }

    /**
     * Retrieves the database configuration settings.
     *
     * @return The DatabaseConfig instance.
     */
    public DatabaseConfig getDBConfig() {
        return dbConfig;
    }
}
