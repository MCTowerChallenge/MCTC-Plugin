package io.github.mystievous.towerchallenge.teams;

import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.Database;
import io.github.mystievous.towerchallenge.EndPortal;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;

public class TeamManager implements Listener {

    private List<TowerTeam> allTeams;
    private GodTeam godTeam;
    private List<ParticipantTeam> teams;

    private final TowerChallenge plugin;
    private final Database database;
    private final EndPortal endPortal;

    public TeamManager(TowerChallenge plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        endPortal = new EndPortal(plugin, this);
        loadTeams();
        loadPlayers();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Loads all teams and assigns their players
     */
    public void loadTeams() {
        try {
            Database.unloadPortalBorders();
            database.placePortalBorders();

            if (allTeams != null) {
                for (TowerTeam team : allTeams) {
                    team.unregisterEvents();
                }
            }

            this.godTeam = database.getGodTeam(this);
            this.teams = database.getParticipantTeams(this);

            this.allTeams = new ArrayList<>();
            this.allTeams.add(this.godTeam);
            this.allTeams.addAll(this.teams);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("SQL Error retrieving teams: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadPlayers() {
        try {
            database.setGameTeamPlayers(this.allTeams);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("SQL Error setting player teams: " + e.getMessage());
        }
    }

    public TowerChallenge getPlugin() {
        return plugin;
    }

    public Database getDatabase() {
        return database;
    }

    public @Nullable TowerTeam getTeam(@NotNull String name) {
        if (!name.equalsIgnoreCase(godTeam.getTextName())) {
            Optional<ParticipantTeam> teamOptional = this.teams.stream().filter(team -> team.getTextName().equals(name)).findFirst();
            if (teamOptional.isPresent()) {
                return teamOptional.get();
            }
        } else {
            return getGodTeam();
        }
        return null;
    }

    public @Nullable TowerTeam getTeam(int databaseId) {
        if (databaseId != godTeam.getDatabaseId()) {
            Optional<ParticipantTeam> teamOptional = this.teams.stream().filter(team -> team.getDatabaseId() == databaseId).findFirst();
            if (teamOptional.isPresent()) {
                return teamOptional.get();
            }
        } else {
            return getGodTeam();
        }
        return null;
    }

    public GodTeam getGodTeam() {
        return godTeam;
    }

    public List<ParticipantTeam> getParticipantTeams() {
        return teams;
    }

    public List<TowerTeam> getAllTeams() {
        return allTeams;
    }

    public void updateWinningTeam(TowerTeam team) {
        try {
            database.updateWinningTeam(team);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error updating winning team to " + team.getTextName());
        }
    }

    public boolean setPlayerTeam(OfflinePlayer player, TowerTeam team) {
        try {
            return database.upsertUserTeam(player.getUniqueId(), team);
        } catch (SQLException e) {
            return false;
        }
    }

    public @Nullable TowerTeam getPlayerTeam(OfflinePlayer player) {
        try {
            Integer teamId = database.getPlayerTeamId(player.getUniqueId());
            if (teamId == null) return null;
            return getTeam(teamId);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("SQLException grabbing team: " + player.getUniqueId());
            return null;
        }
    }

    public void resetTeams() {
        for (ParticipantTeam team : getParticipantTeams()) {
            team.clearPlayers();
        }
        getGodTeam().clearPlayers();
        loadTeams();
        loadPlayers();
    }

    public void addExtraScore(ParticipantTeam team, int score) throws SQLException {
        database.addTeamScore(team, score);
    }

    public int getExtraScore(ParticipantTeam participantTeam) throws SQLException {
        return database.getTeamScore(participantTeam);
    }

    public void showTowerScores(Audience audience) {
        Map<Integer, Integer> addedScores = new HashMap<>();
        try {
            addedScores.putAll(database.getAddedScores());
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error retrieving added team scores");
        }
        Map<Integer, Integer> totalScores = new HashMap<>();
        Objective objective = ChallengeManager.getScoreObjective();
        for (ParticipantTeam team : getParticipantTeams()) {
            int score = objective.getScore(team.getTextName()).getScore();
            if (addedScores.containsKey(team.getDatabaseId())) {
                score += addedScores.get(team.getDatabaseId());
            }
            totalScores.put(team.getDatabaseId(), score);
        }

        List<ParticipantTeam> sortedTeams = getParticipantTeams();
        sortedTeams.sort((o1, o2) -> {
            int score1 = totalScores.get(o1.getDatabaseId());
            int score2 = totalScores.get(o2.getDatabaseId());
            return Integer.compare(score2, score1);
        });

        for (ParticipantTeam team : sortedTeams) {
            audience.sendMessage(team.getDisplayName().color(team.getColor().toTextColor())
                    .append(Component.text(" has ").color(NamedTextColor.WHITE)
                            .append(Component.text(totalScores.get(team.getDatabaseId()))
                                    .color(Palette.PRIMARY.toTextColor()))
                            .append(Component.text(" blocks"))));
        }
    }

    public void dealAllItems() {
        for (TowerTeam team : getParticipantTeams()) {
            team.dealItemsAllPlayers();
        }
    }

    public void dealPlayerItems(Player player) {
        TowerTeam team = getPlayerTeam(player);
        if (team != null) {
            team.dealItems(player);
        }
    }


    public void setPortalFrameFilled(ParticipantTeam team, boolean filled) {
        try {
            database.setPortalFrameFilled(team, filled);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Failed to get team portal frame: " + team.getTextName());
        }
    }

    public Location getPortalFrame(ParticipantTeam team) {
        try {
            return database.getPortalFrame(team);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Failed to get team portal frame: " + team.getTextName());
            return null;
        }
    }

    public int getRemainingPortalFrames() {
        try {
            return database.getRemainingPortalFrames();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error getting remaining portalframes");
            return -1;
        }
    }

    public void openEndPortal() {
        endPortal.openPortal();
    }

    public void resetEndPortal() {
        Bukkit.getLogger().info("Resetting End Portal");
        endPortal.resetPortal();
//        TowerChallenge.log(teams.toString());
        for (ParticipantTeam team : getParticipantTeams()) {
            Bukkit.getLogger().info("Resetting frame for " + team.getTextName());
            team.resetFrame();
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        loadPlayers();

        if (!player.hasPlayedBefore()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (getPlayerTeam(player) instanceof ParticipantTeam team) {
                    Location spawnPoint = team.getSpawnpoint();
                    if (spawnPoint != null) {
                        player.teleport(team.getSpawnpoint());
                    }
                }
            }, 1);
        }
    }

    @EventHandler
    public void onPortalCreate(final PortalCreateEvent event) {
        if (event.isCancelled())
            return;

        if (event.getReason().equals(PortalCreateEvent.CreateReason.NETHER_PAIR)) {
            event.setCancelled(true);
        }
        if (event.getReason().equals(PortalCreateEvent.CreateReason.FIRE)) {
            if (event.getEntity() == null
                    || !(event.getEntity() instanceof Player player)
                    || !(getPlayerTeam(player) instanceof GodTeam || player.isOp())) {
                event.setCancelled(true);
                Location location = event.getBlocks().get(0).getLocation();
                ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text().decoration(TextDecoration.ITALIC, true).color(Palette.PRIMARY.toTextColor());
                message.append(Component.text("A portal was attempted to be opened at ")
                        .append(Component.text("X: " + location.getBlockX()))
                        .append(Component.text(", Y: " + location.getBlockY()))
                        .append(Component.text(", Z: " + location.getBlockZ()))
                );
                Entity entity = event.getEntity();
                if (entity != null) {
                    message.append(Component.text(" by ").append(entity.name()));
                    message.append(Component.text(" [Teleport]").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
                            .clickEvent(ClickEvent.runCommand("/spectatetp " + entity.getUniqueId()))
                    );
                } else if (event.getEntity() != null) {
                    message.append(Component.text(" [Teleport]").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
                            .clickEvent(ClickEvent.runCommand("/tp " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()))
                    );
                }
                godTeam.sendMessage(message.build());
            }
        }
        if (event.getBlocks().get(0).getType().equals(Material.END_PORTAL_FRAME)) {
            event.setCancelled(true);
        }
    }

}
