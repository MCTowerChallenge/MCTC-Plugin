package io.github.mystievous.towerchallenge.configs;

import io.github.mystievous.towerchallenge.TowerChallenge;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

public class DatabaseConfig {

    private final TowerChallenge plugin;

    public DatabaseConfig(TowerChallenge plugin) {
        this.plugin = plugin;
    }

    private @Nullable ConfigurationSection getConfigSection() {
        return plugin.getConfig().getConfigurationSection("database");
    }

    public String getHost() throws InvalidConfigurationException {
        final String key = "host";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isString(key)) {
            return dbConfig.getString(key);
        } else {
            throw new InvalidConfigurationException("No database host set in the config.");
        }
    }

    public int getPort() {
        final String key = "port";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isInt(key)) {
            return dbConfig.getInt(key);
        } else {
//            throw new InvalidConfigurationException("No database port set in the config.");
            return 3306;
        }
    }

    public String getDatabase() throws InvalidConfigurationException {
        final String key = "database";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isString(key)) {
            return dbConfig.getString(key);
        } else {
            throw new InvalidConfigurationException("No database name set in the config.");
        }
    }

    public String getUser() throws InvalidConfigurationException {
        final String key = "user";
        ConfigurationSection dbConfig = getConfigSection();
        if (dbConfig != null && dbConfig.isString(key)) {
            return dbConfig.getString(key);
        } else {
            throw new InvalidConfigurationException("No database user set in the config.");
        }
    }

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
