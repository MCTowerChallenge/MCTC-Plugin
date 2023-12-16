package io.github.mctowerchallenge.mctcplugin;

import io.github.mctowerchallenge.mctcplugin.data.database.Database;
import io.github.mctowerchallenge.mctcplugin.data.database.PortalFrameDB;
import io.github.mctowerchallenge.mctcplugin.god.GodManager;
import io.github.mctowerchallenge.mctcplugin.god.GodMenuCommand;
import io.github.mctowerchallenge.mctcplugin.misc.*;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.data.config.PluginConfig;
import io.github.mctowerchallenge.mctcplugin.messaging.MessageCommands;
import io.github.mctowerchallenge.mctcplugin.portal.PortalControllers;
import io.github.mctowerchallenge.mctcplugin.timer.OrganizationTimer;
import io.github.mctowerchallenge.mctcplugin.timer.TimerCommands;
import io.github.mctowerchallenge.mctcplugin.timer.TimerTabComplete;
import io.github.mctowerchallenge.mctcplugin.timer.TowerTimer;
import io.github.mctowerchallenge.mctcplugin.towering.ChatHandler;
import io.github.mctowerchallenge.mctcplugin.towering.TowerCommands;
import io.github.mctowerchallenge.mctcplugin.towering.TowerTabComplete;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class MCTCPlugin extends JavaPlugin {

    public static final String MCTC_NAMESPACE = "mctc";

    // Get a key in the MCTC namespace
    public static Key key(String value) {
        return Key.key(MCTC_NAMESPACE, value);
    }

    public static NamespacedKey namespacedKey(String value) {
        return NamespacedKey.fromString(value, getInstance());
    }

    private static MCTCPlugin me;

    public static MCTCPlugin getInstance() {
        return me;
    }

    public static void log(String text) {
        Bukkit.getLogger().info(text);
    }

    private PluginConfig pluginConfig;
    private Database pluginDatabase;

    @Override
    public void onEnable() {
        // Plugin startup logic

        me = this;

        try {
            // Load plugin configuration
            pluginConfig = new PluginConfig(this);

            // Initialize the plugin's database
            pluginDatabase = new Database(this, pluginConfig.getDBConfig());

            // Initialize timers and timer-related commands
            OrganizationTimer organizationTimer = new OrganizationTimer(this);
            TowerTimer timer = new TowerTimer(this);
            TimerCommands timerCommands = new TimerCommands(timer, organizationTimer);
            TimerTabComplete timerTabComplete = new TimerTabComplete();

            TeamManager teamManager = new TeamManager(this, pluginDatabase);

            PortalControllers portalControllers = new PortalControllers(this, teamManager);

            ChallengeManager challengeManager = new ChallengeManager(this, teamManager);

            GameFlowManager gameFlowManager = new GameFlowManager(timer, portalControllers);

            this.getCommand("timer").setExecutor(timerCommands);
            this.getCommand("timer").setTabCompleter(timerTabComplete);

            GodManager godManager = new GodManager(this, teamManager, portalControllers);
            GodMenuCommand command = new GodMenuCommand(godManager);
            Bukkit.getPluginCommand("godmenu").setExecutor(command);

            TowerCommands towerCommands = new TowerCommands(challengeManager, teamManager, portalControllers, pluginDatabase);
            TowerTabComplete towerTabComplete = new TowerTabComplete(teamManager);

            this.getCommand("tower").setExecutor(towerCommands);
            this.getCommand("tower").setTabCompleter(towerTabComplete);

            // Teams
            ChatHandler chatHandler = new ChatHandler(this);
            getServer().getPluginManager().registerEvents(chatHandler, this);

            // Other
            BroadcastCommand broadcastCommand = new BroadcastCommand(teamManager);
            this.getCommand("broadcast").setExecutor(broadcastCommand);

            new EnderChestCommand(this);
            new InvseeCommand(this);
            new FlyCommand(this);
            new MessageCommands(this, teamManager);
            new AnvilCommand(this);
            new CraftCommand(this);
            new RenameCommand(this);
        } catch (IOException e) {
            Bukkit.getLogger().severe("IOException has occurred: " + e.getMessage());
            Bukkit.getLogger().severe("Disabling Tower Challenge Plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

    }

    public Database getPluginDatabase() {
        return pluginDatabase;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Cancel all scheduled tasks.
        Bukkit.getScheduler().cancelTasks(this);

        // Unload end portal borders.
        PortalFrameDB.unloadPortalBorders();
    }

}
