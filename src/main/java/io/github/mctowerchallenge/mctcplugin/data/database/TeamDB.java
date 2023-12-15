package io.github.mctowerchallenge.mctcplugin.data.database;

import io.github.mctowerchallenge.mctcplugin.god.GodTeam;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TeamDB {

    private final Plugin plugin;
    private final DataSource dataSource;

    public TeamDB(Plugin plugin, DataSource dataSource) {
        this.plugin = plugin;
        this.dataSource = dataSource;
    }

    /**
     * Retrieves the players belonging to the given teams,
     * and sets the players to their proper teams.
     *
     * @param teams The teams to set players for.
     */
    public void setGameTeamPlayers(List<TowerTeam> teams) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT team_id, uuid FROM users;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            Map<Integer, List<OfflinePlayer>> users = new HashMap<>();
            while (resultSet.next()) {
                String userIdString = resultSet.getString("uuid");
                try {
                    UUID userId = UUID.fromString(userIdString);
                    int teamId = resultSet.getInt("team_id");
                    if (!resultSet.wasNull()) {
                        List<OfflinePlayer> teamUsers = users.getOrDefault(teamId, new ArrayList<>());
                        teamUsers.add(Bukkit.getOfflinePlayer(userId));
                        users.put(teamId, teamUsers);
                    }
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid User Id: " + userIdString);
                }
            }
            for (TowerTeam team : teams) {
                List<OfflinePlayer> teamUsers = users.get(team.getDatabaseId());
                if (teamUsers != null) {
                    team.addAllPlayers(teamUsers);
                }
            }
        }
    }

    /**
     * Retrieves and creates the
     * object for the god team.
     *
     * @param teamManager Current team manager instance.
     * @return the God Team.
     */
    public @Nullable GodTeam getGodTeam(TeamManager teamManager) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT teams.id, teams.name, teams.color, teams.dye
                        FROM teams
                        WHERE teams.id = 1;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int color = resultSet.getInt("color");
                String dye = resultSet.getString("dye");
                return new GodTeam(plugin, teamManager, id, name, new Color(color), dye);
            } else {
                return null;
            }
        }
    }

    /**
     * Retrieves and creates all
     * non-disabled participant teams.
     *
     * @param teamManager Current team manager instance.
     * @return The list of participant teams.
     */
    public List<ParticipantTeam> getParticipantTeams(TeamManager teamManager) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT teams.id, teams.name, teams.color, teams.dye
                        FROM teams
                        WHERE teams.disabled = 0
                        AND teams.is_participant = 1;
                        """
        )) {
            List<ParticipantTeam> teams = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int color = resultSet.getInt("color");
                String dye = resultSet.getString("dye");
                ParticipantTeam team = new ParticipantTeam(plugin, teamManager, id, name, new Color(color), dye);
                teams.add(team);
            }
            return teams;
        }
    }

}
