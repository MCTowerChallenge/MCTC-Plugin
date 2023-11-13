package io.github.mctowerchallenge.mctcplugin;

import io.github.mctowerchallenge.mctcplugin.god.GodManager;
import io.github.mctowerchallenge.mctcplugin.misc.*;
import io.github.mctowerchallenge.mctcplugin.misc.resourcepack.ResourcePack;
import io.github.mctowerchallenge.mctcplugin.misc.resourcepack.ResourcePackListener;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.waitingroom.WaitingRoom;
import io.github.mctowerchallenge.mctcplugin.configs.Config;
import io.github.mctowerchallenge.mctcplugin.decoration.customblock.CustomBlockManager;
import io.github.mctowerchallenge.mctcplugin.decoration.WaterDrips;
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
import org.bukkit.plugin.java.JavaPlugin;

public final class MCTCPlugin extends JavaPlugin {

    public static final String MCTC_NAMESPACE = "mctc";

    // Get a key in the MCTC namespace
    public static Key key(String value) {
        return Key.key(MCTC_NAMESPACE, value);
    }

    private static MCTCPlugin me;

    public static MCTCPlugin getInstance() {
        return me;
    }

    public static void log(String text) {
        Bukkit.getLogger().info(text);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        me = this;

        // Load plugin configuration
        Config config = new Config(this);

        // Initialize the plugin's database
        Database database = new Database(this, config.getDBConfig());

        // Initialize timers and timer-related commands
        OrganizationTimer organizationTimer = new OrganizationTimer(this);
        TowerTimer timer = new TowerTimer(this);
        TimerCommands timerCommands = new TimerCommands(timer, organizationTimer);
        TimerTabComplete timerTabComplete = new TimerTabComplete();

        TeamManager teamManager = new TeamManager(this, database);

//        Jun2023QuestManager jun2023QuestManager = new Jun2023QuestManager(this, teamManager);

        PortalControllers portalControllers = new PortalControllers(this, teamManager);

        ChallengeManager challengeManager = new ChallengeManager(this, teamManager);

        GameFlowManager gameFlowManager = new GameFlowManager(timer, portalControllers);

        this.getCommand("timer").setExecutor(timerCommands);
        this.getCommand("timer").setTabCompleter(timerTabComplete);

        WaterDrips waterDrips = new WaterDrips(this, database);

        CustomBlockManager customBlockManager = new CustomBlockManager(this);

        new GodManager(this, customBlockManager, teamManager, portalControllers);

        WaitingRoom waitingRoom = new WaitingRoom(this);

        TowerCommands towerCommands = new TowerCommands(challengeManager, teamManager, portalControllers, waitingRoom, database);
        TowerTabComplete towerTabComplete = new TowerTabComplete(teamManager);

        this.getCommand("tower").setExecutor(towerCommands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);

        // Teams
        ChatHandler chatHandler = new ChatHandler(this);
        getServer().getPluginManager().registerEvents(chatHandler, this);

        // Other
        ResourcePack resourcePack = new ResourcePack();
        this.getCommand("resourcepack").setExecutor(resourcePack);
        new ResourcePackListener(challengeManager);

        BroadcastCommand broadcastCommand = new BroadcastCommand(teamManager);
        this.getCommand("broadcast").setExecutor(broadcastCommand);

        new EnderChestCommand(this);
        new InvseeCommand(this);
        new FlyCommand(this);
        new MessageCommands(this, teamManager);
        new AnvilCommand(this);
        new CraftCommand(this);
        new RenameCommand(this);
        new SpectateTPCommand();
        new MystiSkinListener(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Cancel all scheduled tasks.
        Bukkit.getScheduler().cancelTasks(this);
//        ferrisWheel.unloadCars();

        // Unload end portal borders.
        Database.unloadPortalBorders();
    }

}
