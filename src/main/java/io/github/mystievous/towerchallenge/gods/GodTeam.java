package io.github.mystievous.towerchallenge.gods;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Team for the Gods/Admins of the event
 */
public class GodTeam extends TowerTeam {

    public static final String GOD_NAME = "God";
    public static final String GOD_COLOR = "#F7E983";
    public static final String GOD_DYE = "yellow";

    /**
     * Team for the Gods/Admins of the event
     *
     * @param challengeManager Challenge Manager for the event
     */
    public GodTeam(ChallengeManager challengeManager) {
        super(challengeManager, GOD_NAME, GOD_COLOR, GOD_DYE);
    }

    @Override
    public void registerConfigPlayer(OfflinePlayer player) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(Config.teamConfigFile);
        for (Map.Entry<String, ParticipantTeam> entry : getManager().getTowerListener().getTeams().entrySet()) {
            ParticipantTeam team = entry.getValue();
            if (team.hasPlayer(player)) {
                String path = "Teams." + team.getTextName() + ".players";
                List<String> players = config.getStringList(path);
                players.remove(player.getUniqueId().toString());
                config.set(path, players);
            }
        }
        if (getManager().getTowerListener().getGodTeam().hasPlayer(player)) {
            String path = "Gods";
            List<String> players = config.getStringList(path);
            players.remove(player.getUniqueId().toString());
            config.set(path, players);
        }
        String path = "Gods";
        List<String> players = config.getStringList(path);
        players.add(player.getUniqueId().toString());
        config.set(path, players);
        try {
            config.save(Config.teamConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
