package io.github.mystievous.towerchallenge;

import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.eventspecific.valentines.FerrisWheel;
import io.github.mystievous.towerchallenge.hats.HatCommands;
import io.github.mystievous.towerchallenge.hats.HatTabComplete;
import io.github.mystievous.towerchallenge.messaging.MessageCommands;
import io.github.mystievous.towerchallenge.misc.*;
import io.github.mystievous.towerchallenge.misc.resourcepack.ResourcePack;
import io.github.mystievous.towerchallenge.misc.resourcepack.ResourcePackListener;
import io.github.mystievous.towerchallenge.timer.Timer;
import io.github.mystievous.towerchallenge.timer.TimerCommands;
import io.github.mystievous.towerchallenge.timer.TimerTabComplete;
import io.github.mystievous.towerchallenge.towering.ChatHandler;
import io.github.mystievous.towerchallenge.towering.TowerCommands;
import io.github.mystievous.towerchallenge.towering.TowerTabComplete;
import io.github.mystievous.towerchallenge.wands.WandCommands;
import io.github.mystievous.towerchallenge.wands.WandListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TowerChallenge extends JavaPlugin {

    //d0608c
    //124f49

    public static final String MCTC_NAMESPACE = "mctc";

    private static TowerChallenge me;
    public static TowerChallenge getInstance() {
        return me;
    }

    private Config config;
    private ChallengeManager challengeManager;
    private Database database;

    private FerrisWheel ferrisWheel;

    @Override
    public void onEnable() {
        // Plugin startup logic

        me = this;

        this.config = new Config(this);

        this.challengeManager = new ChallengeManager(this);

        this.database = new Database(config.getDBConfig());

        TowerCommands towerCommands = new TowerCommands(challengeManager);
        TowerTabComplete towerTabComplete = new TowerTabComplete(this);

        this.getCommand("tower").setExecutor(towerCommands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);

        HatCommands hatCommands = new HatCommands(challengeManager.getTowerListener());
        HatTabComplete hatTabComplete = new HatTabComplete();
        this.getCommand("hat").setExecutor(hatCommands);
        this.getCommand("hat").setTabCompleter(hatTabComplete);

//        new SitEventHandler();

        // Teams
        ChatHandler chatHandler = new ChatHandler();
        getServer().getPluginManager().registerEvents(chatHandler, this);

        // Wands
        WandCommands wandCommands = new WandCommands(this);
        this.getCommand("wand").setExecutor(wandCommands);
//        WandListener wandListener = new WandListener();
//        getServer().getPluginManager().registerEvents(wandListener, this);

        // Timer
        Timer timer = new Timer(this);
        TimerCommands timerCommands = new TimerCommands(timer);
        TimerTabComplete timerTabComplete = new TimerTabComplete();

        this.getCommand("timer").setExecutor(timerCommands);
        this.getCommand("timer").setTabCompleter(timerTabComplete);

        // Other
        GodCommand godCommand = new GodCommand();
        this.getCommand("god").setExecutor(godCommand);

        ResourcePack resourcePack = new ResourcePack();
        this.getCommand("resourcepack").setExecutor(resourcePack);
        new ResourcePackListener(challengeManager);

        BroadcastCommand broadcastCommand = new BroadcastCommand(challengeManager);
        this.getCommand("broadcast").setExecutor(broadcastCommand);

        new EnderChestCommand(this);
        new InvseeCommand(this);
        new MessageCommands(challengeManager);
        new AnvilCommand(this);
        new CraftCommand(this);
        new RenameCommand(this);

        ferrisWheel = new FerrisWheel(this);

    }

    public FerrisWheel getFerrisWheel() {
        return ferrisWheel;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getScheduler().cancelTasks(this);
        ferrisWheel.unloadCars();
    }

    public Config getPluginConfig() {
        return this.config;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public Database getDatabase() {
        return database;
    }
}
