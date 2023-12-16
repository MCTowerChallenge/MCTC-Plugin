package io.github.mctowerchallenge.mctcplugin.data.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.github.mctowerchallenge.mctcplugin.utility.ConfigUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class WorldConfig {

    private final YamlDocument config;

    /**
     * Constructs a new DatabaseConfig instance associated with the provided plugin.
     *
     * @param plugin The main plugin instance.
     */
    public WorldConfig(Plugin plugin) throws IOException {
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
}
