package io.github.mystievous.towerchallenge.gods;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.hats.HatGUI;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Team for the Gods/Admins of the event
 */
public class GodTeam extends TowerTeam {

    private final Map<String, String> playerHatColors = new HashMap<>();
    private final Map<String, HatGUI> hatColorGUIS = new HashMap<>();

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
        hatColorGUIS.put(getColor(), getHatGUI());
    }

    /**
     * Sets the hat color for a specific player
     *
     * @param player   the player to set the color for
     * @param hexColor the hex string to set it to
     */
    public void setPlayerHatColor(Player player, String hexColor) {
        if (hexColor != null) {
            if (hatColorGUIS.get(hexColor) != null) {
                playerHatColors.put(player.getUniqueId().toString(), hexColor);
            } else {
                try {
                    hatColorGUIS.put(hexColor, new HatGUI(getManager(), hexColor));
                    playerHatColors.put(player.getUniqueId().toString(), hexColor);
                } catch (IllegalArgumentException exception) {
                    getPlugin().getLogger().info("Input is not a valid hex number!");
                    player.sendMessage(Component.text("Input is not a valid hex number!").color(NamedTextColor.DARK_RED));
                }
            }
        } else {
            resetPlayerColor(player);
        }
    }

    /**
     * Resets the player's hat color
     *
     * @param player the player to reset
     */
    public void resetPlayerColor(Player player) {
        setPlayerHatColor(player, getColor());
    }

    /**
     * Gets the player's current hat color
     *
     * @param player Player to get the color of
     * @return the color of the player
     */
    private String getPlayerColor(Player player) {
        return playerHatColors.get(player.getUniqueId().toString());
    }

    @Override
    public void openHatGUI(Player player) {
        HatGUI gui = hatColorGUIS.get(getPlayerColor(player));
        if (gui != null) {
            gui.openInventory(player);
        } else {
            setPlayerHatColor(player, null);
            gui = hatColorGUIS.get(getPlayerColor(player));
            gui.openInventory(player);
        }
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
