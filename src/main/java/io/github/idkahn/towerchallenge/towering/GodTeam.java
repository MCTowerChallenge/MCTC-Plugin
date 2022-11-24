package io.github.idkahn.towerchallenge.towering;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GodTeam extends ParticipantTeam {

    private final Map<String, String> playerHatColors = new HashMap<>();
    private final Map<String, HatGUI> hatColorGUIS = new HashMap<>();

    public static String GOD_NAME = "God";
    public static String GOD_COLOR = "#F7E983";
    public static String GOD_DYE = "yellow";

    public GodTeam(EventManager manager) {
        super(manager, GOD_NAME, GOD_COLOR, GOD_DYE);
        hatColorGUIS.put(getColor(), getHatGUI());
    }

    public void setPlayerHatColor(Player player, String hexColor) {
        if (hexColor != null) {
            if (hatColorGUIS.get(hexColor) != null) {
                playerHatColors.put(player.getUniqueId().toString(), hexColor);
            } else {
                try {
//                    getPlugin().getLogger().info("Setting color to " + hexColor);
                    hatColorGUIS.put(hexColor, new HatGUI(getManager(), hexColor));
                    playerHatColors.put(player.getUniqueId().toString(), hexColor);
//                    getPlugin().getLogger().info(hatColorGUIS.get(hexColor).toString());
                } catch (IllegalArgumentException exception) {
                    getPlugin().getLogger().info("Input is not a valid hex number!");
                    player.sendMessage(Component.text("Input is not a valid hex number!").color(NamedTextColor.DARK_RED));
                }
            }
        } else {
//            getPlugin().getLogger().info("Color is null, setting default color");
            resetPlayerColor(player);
        }
    }

    public void resetPlayerColor(Player player) {
        setPlayerHatColor(player, getColor());
    }

    private String getColor(Player player) {
        return playerHatColors.get(player.getUniqueId().toString());
    }

    @Override
    public void loadPortal() {

    }

    @Override
    public Location getFrameLocation() {
        return null;
    }

    @Override
    public boolean hasEye() {
        return false;
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
    public void addPlayer(OfflinePlayer player) {
        try {
            getTeam().addPlayer(player);
        } catch (IllegalArgumentException e) {
            getPlugin().getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
        }
    }


}
