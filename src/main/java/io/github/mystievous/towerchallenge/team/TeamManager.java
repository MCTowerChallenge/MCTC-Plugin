package io.github.mystievous.towerchallenge.team;

import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiHeldItem;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.Database;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.god.GodTeam;
import io.github.mystievous.towerchallenge.quest.Quest;
import io.github.mystievous.towerchallenge.quest.QuestGui;
import io.github.mystievous.towerchallenge.quest.QuestManager;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;

/**
 * Manages teams, players, and various operations related to the Tower Challenge game.
 */
public class TeamManager implements Listener {

    private static TeamManager instance;
    public static TeamManager getInstance() {
        return instance;
    }

    private List<TowerTeam> allTeams;
    private GodTeam godTeam;
    private List<ParticipantTeam> teams;

    private final TowerChallenge plugin;
    private final Database database;
    private final QuestManager questManager;

    public TeamManager(TowerChallenge plugin, Database database, QuestManager questManager) {
        this.plugin = plugin;
        this.database = database;
        this.questManager = questManager;
        loadTeams();
        loadPlayers();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        instance = this;
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
            for (TowerTeam team : allTeams) {
                questManager.initTeamQuests(team);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning("SQL Error retrieving teams: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Assigns all players to their correct teams.
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
     * @param name The name of the team to retrieve.
     * @return The {@link TowerTeam} instance, or null if not found.
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
     * Gets the team with the specified database ID.
     *
     * @param databaseId The ID of the team to retrieve.
     * @return The {@link TowerTeam} instance, or null if not found.
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
     * @return The god team instance.
     */
    public GodTeam getGodTeam() {
        return godTeam;
    }

    /**
     * Gets all participant teams.
     *
     * @return A list of the participant teams.
     */
    public List<ParticipantTeam> getParticipantTeams() {
        return teams;
    }

    /**
     * Returns all teams, including participant and god teams.
     *
     * @return A list of all teams.
     */
    public List<TowerTeam> getAllTeams() {
        return allTeams;
    }

    /**
     * Retrieves a GUI for the current quest of a player's team.
     *
     * @param player The player whose team's quest GUI is requested.
     * @return The quest GUI for the player's team's current quest.
     */
    public Gui getTeamQuestGui(Player player) {
        TowerTeam team = getPlayerTeam(player);
        if (team != null) {
            String currentQuestId = team.getCurrentQuestTag();
            Quest currentQuest = team.getQuest(currentQuestId);
            if (currentQuest != null) {
                return currentQuest.getGui(player);
            }
        }
        return new QuestGui(plugin, "Error", "Error fetching quest.");
    }

    /**
     * Retrieves a GUI-held item representing the quest book.
     *
     * @return The quest book GUI-held item.
     */
    public GuiHeldItem getQuestBook() {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(Component.text("Quest Book").decoration(TextDecoration.ITALIC, false));
        bookMeta.setCustomModelData(2);
        bookMeta.lore(TextUtil.formatTexts(
                Component.text("Use ").append(Component.keybind("key.use")).append(Component.text(" with me in your hand")),
                Component.text("to open the quest menu!")
        ));
        book.setItemMeta(bookMeta);
        return new GuiHeldItem(plugin, QuestManager.GUI_ID, book, this::getTeamQuestGui);
    }

    /**
     * Updates the winning team in the database.
     *
     * @param team The winning team to update.
     */
    public void updateWinningTeam(TowerTeam team) {
        try {
            database.updateWinningTeam(team);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error updating winning team to " + team.getTextName());
        }
    }

    /**
     * Gives a team a specific hat group.
     *
     * @param hatGroupId The ID of the hat group to give.
     * @param team       The team to give the hat group to.
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
     * Assigns a player to a team.
     *
     * @param player The player to assign.
     * @param team   The team to assign the player to.
     * @return True if the player was assigned successfully, false otherwise.
     */
    public boolean setPlayerTeam(OfflinePlayer player, TowerTeam team) {
        try {
            return database.upsertUserTeam(player.getUniqueId(), team);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Retrieves the team of a player.
     *
     * @param player The player to check.
     * @return The team the player belongs to, or null if they have no team.
     */
    public @Nullable TowerTeam getPlayerTeam(OfflinePlayer player) {
        for (TowerTeam team : allTeams) {
            if (team.getOfflinePlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    /**
     * Resets all teams and reloads players.
     */
    public void resetTeams() {
        for (ParticipantTeam team : getParticipantTeams()) {
            team.clearPlayers();
        }
        getGodTeam().clearPlayers();
        loadTeams();
        loadPlayers();
    }

    /**
     * Adds extra score to a participant team.
     *
     * @param team  The participant team to add score to.
     * @param score The amount of score to add.
     * @throws SQLException if a SQL error occurs.
     */
    public void addExtraScore(ParticipantTeam team, int score) throws SQLException {
        database.addTeamScore(team, score);
    }

    /**
     * Gets the extra score of a participant team.
     *
     * @param participantTeam The participant team to get the extra score of.
     * @return The extra score of the participant team.
     * @throws SQLException if a SQL error occurs.
     */
    public int getExtraScore(ParticipantTeam participantTeam) throws SQLException {
        return database.getTeamScore(participantTeam);
    }

    /**
     * Grabs all team scores from the scoreboard and database
     * and displays them in chat to the given audience.
     *
     * @param audience The audience to show the scores to.
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
     * Deals starting items to all players on participant teams.
     */
    public void dealAllItems() {
        for (TowerTeam team : getParticipantTeams()) {
            team.dealItemsAllPlayers();
        }
    }

    /**
     * Deals starting items only to the given player.
     *
     * @param player The player to give starting items to.
     */
    public void dealPlayerItems(Player player) {
        TowerTeam team = getPlayerTeam(player);
        if (team != null) {
            team.dealItems(player);
        }
    }

    /**
     * Sets the end portal frame state for a participant team.
     * <p></p>
     * Directly calls database, so should be called asynchronously.
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
     * Gets the location of a participant team's end portal frame.
     *
     * @param team The participant team.
     * @return The location of the end portal frame.
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
     * Gets the number of remaining incomplete portal frames.
     *
     * @return The number of remaining portal frames.
     */
    public int getRemainingPortalFrames() {
        try {
            return database.getRemainingPortalFrames();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error getting remaining portalframes");
            return -1;
        }
    }

    /**
     * Resets all current teams' portal frames.
     */
    public void resetTeamPortalFrames() {
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
