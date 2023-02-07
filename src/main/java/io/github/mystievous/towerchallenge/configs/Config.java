package io.github.mystievous.towerchallenge.configs;

import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
