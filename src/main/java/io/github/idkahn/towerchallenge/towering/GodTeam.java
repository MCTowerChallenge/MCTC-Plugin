package io.github.idkahn.towerchallenge.towering;

import io.github.idkahn.towerchallenge.Hats.HatGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GodTeam extends TowerTeam {

    private Map<String, String> playerHatColors;
    private Map<String, HatGUI> hatColorGUIS;

    public GodTeam(JavaPlugin plugin, String displayName, String color) {
        super(plugin, displayName, color);
        playerHatColors = new HashMap<>();
        hatColorGUIS = new HashMap<>();
        hatColorGUIS.put(getColor(), getHatGUI());
    }

    public void setPlayerHatColor(Player player, String hexColor) {
        if (hexColor != null) {
            if (hatColorGUIS.get(hexColor) != null) {
                playerHatColors.put(player.getUniqueId().toString(), hexColor);
            } else {
                try {
//                    getPlugin().getLogger().info("Setting color to " + hexColor);
                    hatColorGUIS.put(hexColor, new HatGUI(getPlugin(), hexColor));
                    playerHatColors.put(player.getUniqueId().toString(), hexColor);
//                    getPlugin().getLogger().info(hatColorGUIS.get(hexColor).toString());
                } catch (NumberFormatException exception) {
                    getPlugin().getLogger().info("Input is not a valid hex number!");
                } catch (IllegalArgumentException exception) {
                    getPlugin().getLogger().info("Input is not a valid hex number!");
                }
            }
        } else {
//            getPlugin().getLogger().info("Color is null, setting default color");
            playerHatColors.put(player.getUniqueId().toString(), getColor());
        }
    }

    public void resetPlayerColor(Player player) {
        setPlayerHatColor(player, getColor());
    }

    private String getColor(Player player) {
        return playerHatColors.get(player.getUniqueId().toString());
    }

    @Override
    public void openHatGUI(Player player) {
//        Bukkit.getLogger().info(getColor(player) + " " + hatColorGUIS.get(getColor(player)));
        HatGUI gui = hatColorGUIS.get(getColor(player));
        if (gui != null) {
            gui.openInventory(player);
        } else {
            setPlayerHatColor(player, null);
            gui = hatColorGUIS.get(getColor(player));
            gui.openInventory(player);
        }

    }

    @Override
    public void addPlayer(OfflinePlayer player, Boolean addToConfig) {
        try {
            getTeam().addPlayer(player);
        } catch (IllegalArgumentException e) {
            getPlugin().getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
        }

    }


}
