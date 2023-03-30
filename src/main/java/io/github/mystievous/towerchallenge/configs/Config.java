package io.github.mystievous.towerchallenge.configs;

import io.github.mystievous.towerchallenge.TowerChallenge;

public class Config {

    private final DatabaseConfig dbConfig;

    public Config(TowerChallenge plugin) {

        plugin.saveDefaultConfig();

        dbConfig = new DatabaseConfig(plugin);

    }

    public DatabaseConfig getDBConfig() {
        return dbConfig;
    }
}
