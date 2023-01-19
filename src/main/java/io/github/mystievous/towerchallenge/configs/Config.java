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
    public static File wandConfigFile;
    public static File endPortalConfigFile;
    public static File hatConfigFile;
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

        wandConfigFile = new File(plugin.getDataFolder(), "wands.yml");
        plugin.saveResource("wands.yml", false);
        YamlConfiguration wandConfig = YamlConfiguration.loadConfiguration(wandConfigFile);

        endPortalConfigFile = new File(plugin.getDataFolder(), "portalframes.yml");
        plugin.saveResource("portalframes.yml", false);
        YamlConfiguration endPortalConfig = YamlConfiguration.loadConfiguration(endPortalConfigFile);

        hatConfigFile = new File(plugin.getDataFolder(), "hat.yml");
        plugin.saveResource("hat.yml", false);
        YamlConfiguration hatConfig = YamlConfiguration.loadConfiguration(hatConfigFile);

        teamScoreConfigFile = new File(plugin.getDataFolder(), "teamscores.yml");
        YamlConfiguration teamScoreConfig = YamlConfiguration.loadConfiguration(teamScoreConfigFile);

        teamDataConfigFile = new File(plugin.getDataFolder(), "teamdata.yml");
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(teamDataConfigFile);

        try {
            teamConfig.save(teamConfigFile);
            wandConfig.save(wandConfigFile);
            endPortalConfig.save(endPortalConfigFile);
            hatConfig.save(hatConfigFile);
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
