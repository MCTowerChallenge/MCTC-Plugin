package io.github.mystievous.towerchallenge.configs;

import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    public static File teamDataConfigFile;

    private final DatabaseConfig dbConfig;

    public Config(TowerChallenge plugin) {

        plugin.saveDefaultConfig();

        teamDataConfigFile = new File(plugin.getDataFolder(), "teamdata.yml");
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(teamDataConfigFile);

        try {
            teamDataConfig.save(teamDataConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dbConfig = new DatabaseConfig(plugin);

    }

    public DatabaseConfig getDBConfig() {
        return dbConfig;
    }
}
