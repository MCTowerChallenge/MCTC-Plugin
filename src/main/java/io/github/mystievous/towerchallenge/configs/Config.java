package io.github.mystievous.towerchallenge.configs;

import io.github.mystievous.towerchallenge.TowerChallenge;

/**
 * Represents the configuration manager for the plugin.
 * This class handles loading and managing various configuration settings.
 */
public class Config {

    private final DatabaseConfig dbConfig;

    /**
     * Constructs a new Config instance for the plugin.
     * Loads default configuration settings and initializes the database configuration.
     *
     * @param plugin The main plugin instance.
     */
    public Config(TowerChallenge plugin) {

        plugin.saveDefaultConfig();

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
