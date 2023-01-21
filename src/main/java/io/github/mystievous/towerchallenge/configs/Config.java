package io.github.mystievous.towerchallenge.configs;

import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class Config {

    public static File teamConfigFile;
    public static File endPortalConfigFile;
    public static File teamScoreConfigFile;
    public static File teamDataConfigFile;
    private final TowerChallenge plugin;

    private final DatabaseConfig dbConfig;

    public Config(TowerChallenge plugin) {
        this.plugin = plugin;

        plugin.saveDefaultConfig();

        teamConfigFile = new File(plugin.getDataFolder(), "teams.yml");
        plugin.saveResource("teams.yml", false);
        YamlConfiguration teamConfig = YamlConfiguration.loadConfiguration(teamConfigFile);

        endPortalConfigFile = new File(plugin.getDataFolder(), "portalframes.yml");
        plugin.saveResource("portalframes.yml", false);
        YamlConfiguration endPortalConfig = YamlConfiguration.loadConfiguration(endPortalConfigFile);

        teamScoreConfigFile = new File(plugin.getDataFolder(), "teamscores.yml");
        YamlConfiguration teamScoreConfig = YamlConfiguration.loadConfiguration(teamScoreConfigFile);

        teamDataConfigFile = new File(plugin.getDataFolder(), "teamdata.yml");
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(teamDataConfigFile);

        try {
            teamConfig.save(teamConfigFile);
            endPortalConfig.save(endPortalConfigFile);
            teamScoreConfig.save(teamScoreConfigFile);
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
