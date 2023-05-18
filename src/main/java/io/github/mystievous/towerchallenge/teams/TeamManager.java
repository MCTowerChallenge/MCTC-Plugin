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
     * Loads and creates all teams and assigns their players.
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

    /**
     * Assigns all players to
     * their correct teams.
     */
    public void loadPlayers() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                database.setGameTeamPlayers(this.allTeams);
            } catch (SQLException e) {
                Bukkit.getLogger().warning("SQL Error setting player teams: " + e.getMessage());
            }
        });
    }

    public TowerChallenge getPlugin() {
        return plugin;
    }

    public Database getDatabase() {
        return database;
    }

    /**
     * Gets the team with the specified name.
     *
     * @param name The team to get.
     * @return The {@link TowerTeam} instance.
     */
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

    /**
     * Gets the team with the specified id.
     *
     * @param databaseId The team id to get.
     * @return The {@link TowerTeam} instance.
     */
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

    /**
     * Gets the god team instance.
     *
     * @return The god team.
     */
    public GodTeam getGodTeam() {
        return godTeam;
    }

    /**
     * Gets all participant teams.
     *
     * @return A list of the Participant teams.
     */
    public List<ParticipantTeam> getParticipantTeams() {
        return teams;
    }

    /**
     * Returns all teams, participant and god.
     *
     * @return A list of all teams.
     */
    public List<TowerTeam> getAllTeams() {
        return allTeams;
    }

    /**
     * Updates the winning team in the database.
     *
     * @param team The team to make the winners.
     */
    public void updateWinningTeam(TowerTeam team) {
        try {
            database.updateWinningTeam(team);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error updating winning team to " + team.getTextName());
        }
    }

    /**
     * Gives a team a specific hatgroup.
     *
     * @param hatGroupId The database id of the
     *                   hatgroup to give the team.
     * @param team       The team to give the hatgroup to.
     */
    public void giveTeamHatgroup(int hatGroupId, TowerTeam team) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                database.giveTeamHatGroup(hatGroupId, team);
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Failed to give team hatgroup: " + e.getMessage());
            }
        });
    }

    /**
     * Assigns the player to
     * the given team.
     *
     * @param player The player to assign.
     * @param team   The team to assign
     *               the player to.
     * @return True, if the player was
     * assigned successfully.
     */
    public boolean setPlayerTeam(OfflinePlayer player, TowerTeam team) {
        try {
            return database.upsertUserTeam(player.getUniqueId(), team);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Retrieves the team of the
     * specified player.
     *
     * @param player The player to check.
     * @return The team the player belongs
     * to, or null if they have no team.
     */
    public @Nullable TowerTeam getPlayerTeam(OfflinePlayer player) {
        for (TowerTeam team : allTeams) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    /**
     * Empties all teams and
     * reloads players.
     */
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

    /**
     * Grabs all team scores from
     * the scoreboard and database
     * and displays them in chat
     * to the given audience.
     *
     * @param audience The audience to
     *                 show the scores to.
     */
    public void showTowerScores(Audience audience) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
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
        });
    }

    /**
     * Deals starting items to all
     * players on participant teams.
     */
    public void dealAllItems() {
        for (TowerTeam team : getParticipantTeams()) {
            team.dealItemsAllPlayers();
        }
    }

    /**
     * Deals starting items only
     * to the given player.
     *
     * @param player The player to give
     *               starting items to.
     */
    public void dealPlayerItems(Player player) {
        TowerTeam team = getPlayerTeam(player);
        if (team != null) {
            team.dealItems(player);
        }
    }

    /**
     * Sets the end portal frame state for
     * the given team.
     * <p></p>
     * Directly calls database, so
     * should be called asynchronously.
     *
     * @param team   The team to set.
     * @param filled The state to set
     *               the frame to.
     */
    public void setPortalFrameFilled(ParticipantTeam team, boolean filled) {
        try {
            database.setPortalFrameFilled(team, filled);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Failed to get team portal frame: " + team.getTextName());
        }
    }

    /**
     * Initializes and gets the location
     * of a team's end portal frame.
     *
     * @param team The team to make the
     *             frame for.
     */
    public Location getPortalFrame(ParticipantTeam team) {
        try {
            return database.getPortalFrame(team);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Failed to get team portal frame: " + team.getTextName());
            return null;
        }
    }

    /**
     * Counts how many portal frames are
     * left incomplete.
     *
     * @return The number of portal frames
     * left.
     */
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

    /**
     * Closes the end portal and
     * resets all current teams'
     * portal frames.
     */
    public void resetEndPortal() {
        Bukkit.getLogger().info("Resetting End Portal");
        endPortal.resetPortal();
//        TowerChallenge.log(teams.toString());
        for (ParticipantTeam team : getParticipantTeams()) {
            Bukkit.getLogger().info("Resetting frame for " + team.getTextName());
            team.resetFrame();
        }
    }

    /**
     * When a player joins, sets them to
     * the proper team, and, if it is their
     * first time joining, sets them to their
     * team spawn.
     *
     * @param event The player join event.
     */
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

    /**
     * When a player tries to create a portal,
     * cancels the event and notifies the gods >:)
     *
     * @param event The portal create event.
     */
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
