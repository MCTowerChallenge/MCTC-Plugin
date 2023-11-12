package io.github.mctowerchallenge.towerchallenge;

import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.IceCream;
import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.Posters;
import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.flags.SelectionHandler;
import io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.gallery.Gallery;
import io.github.mctowerchallenge.towerchallenge.eventspecific.oct2023.quest.Oct2023QuestManager;
import io.github.mctowerchallenge.towerchallenge.god.GodManager;
import io.github.mctowerchallenge.towerchallenge.hats.HatCommands;
import io.github.mctowerchallenge.towerchallenge.hats.HatTabComplete;
import io.github.mctowerchallenge.towerchallenge.hideentity.HiddenEntityManager;
import io.github.mctowerchallenge.towerchallenge.interaction.InteractableTagManager;
import io.github.mctowerchallenge.towerchallenge.interaction.InteractableTaggedEntity;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.CharacterManager;
import io.github.mctowerchallenge.towerchallenge.interaction.npc.DialogueCommands;
import io.github.mctowerchallenge.towerchallenge.magic.MagicItems;
import io.github.mctowerchallenge.towerchallenge.magic.WandCommands;
import io.github.mctowerchallenge.towerchallenge.misc.*;
import io.github.mctowerchallenge.towerchallenge.misc.resourcepack.ResourcePack;
import io.github.mctowerchallenge.towerchallenge.misc.resourcepack.ResourcePackListener;
import io.github.mctowerchallenge.towerchallenge.quest.QuestCommands;
import io.github.mctowerchallenge.towerchallenge.quest.QuestManager;
import io.github.mctowerchallenge.towerchallenge.quest.util.FullInventory;
import io.github.mctowerchallenge.towerchallenge.team.TeamManager;
import io.github.mctowerchallenge.towerchallenge.waitingroom.WaitingRoom;
import io.github.mctowerchallenge.towerchallenge.configs.Config;
import io.github.mctowerchallenge.towerchallenge.decoration.customblock.CustomBlockManager;
import io.github.mctowerchallenge.towerchallenge.decoration.WaterDrips;
import io.github.mctowerchallenge.towerchallenge.messaging.MessageCommands;
import io.github.mctowerchallenge.towerchallenge.portal.PortalControllers;
import io.github.mctowerchallenge.towerchallenge.timer.OrganizationTimer;
import io.github.mctowerchallenge.towerchallenge.timer.TimerCommands;
import io.github.mctowerchallenge.towerchallenge.timer.TimerTabComplete;
import io.github.mctowerchallenge.towerchallenge.timer.TowerTimer;
import io.github.mctowerchallenge.towerchallenge.towering.ChatHandler;
import io.github.mctowerchallenge.towerchallenge.towering.TowerCommands;
import io.github.mctowerchallenge.towerchallenge.towering.TowerTabComplete;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class TowerChallenge extends JavaPlugin {

    public static final String MCTC_NAMESPACE = "mctc";

    // Get a key in the MCTC namespace
    public static Key key(String value) {
        return Key.key(MCTC_NAMESPACE, value);
    }

    private static TowerChallenge me;

    public static TowerChallenge getInstance() {
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

        QuestManager questManager = new QuestManager(this);

        TeamManager teamManager = new TeamManager(this, database, questManager);

//        Jun2023QuestManager jun2023QuestManager = new Jun2023QuestManager(this, teamManager);
        Oct2023QuestManager oct2023QuestManager = new Oct2023QuestManager(this, teamManager);

        QuestCommands commands = new QuestCommands(teamManager);
        getCommand("questbook").setExecutor(commands);

        PortalControllers portalControllers = new PortalControllers(this, teamManager);

        ChallengeManager challengeManager = new ChallengeManager(this, teamManager);

        CharacterManager characterManager = new CharacterManager(this, teamManager, database);

        DialogueCommands dialogueCommands = new DialogueCommands(teamManager);
        PluginCommand stopDialogue = this.getCommand("stopdialogue");
        if (stopDialogue != null) {
            stopDialogue.setExecutor(dialogueCommands);
            stopDialogue.setTabCompleter(dialogueCommands);
        }

        GameFlowManager gameFlowManager = new GameFlowManager(this, timer, characterManager, teamManager, portalControllers);

        InteractableTagManager tagManager = new InteractableTagManager(this, teamManager);

        new SelectionHandler(this, database);
        new IceCream(this);
        new Gallery(this);
        Posters.registerPosterInteractables(teamManager);

        InteractableTaggedEntity seat = new InteractableTaggedEntity("seat");
        seat.setDefaultInteractionHandler((team, event) -> Bukkit.getScheduler().runTask(this, () -> {
            Entity entity = event.getRightClicked();
            if (entity.getPassengers().isEmpty()) {
                entity.addPassenger(event.getPlayer());
            }
        }));
        InteractableTagManager.registerTag(seat);

        this.getCommand("timer").setExecutor(timerCommands);
        this.getCommand("timer").setTabCompleter(timerTabComplete);

        HiddenEntityManager hiddenEntityManager = new HiddenEntityManager();
        WaterDrips waterDrips = new WaterDrips(this, database);

        MagicItems magicItems = new MagicItems(this, database, teamManager, characterManager, waterDrips);

        CustomBlockManager customBlockManager = new CustomBlockManager(this);

        new GodManager(this, gameFlowManager, customBlockManager, questManager, oct2023QuestManager, teamManager, magicItems, portalControllers);

        WaitingRoom waitingRoom = new WaitingRoom(this);

        TowerCommands towerCommands = new TowerCommands(challengeManager, teamManager, portalControllers, waitingRoom, oct2023QuestManager, database);
        TowerTabComplete towerTabComplete = new TowerTabComplete(teamManager);

        this.getCommand("tower").setExecutor(towerCommands);
        this.getCommand("tower").setTabCompleter(towerTabComplete);

        HatCommands hatCommands = new HatCommands(this, database);
        HatTabComplete hatTabComplete = new HatTabComplete();
        this.getCommand("hat").setExecutor(hatCommands);
        this.getCommand("hat").setTabCompleter(hatTabComplete);

        PluginCommand fullInvCommand = getCommand("fullinventory");
        if (fullInvCommand != null) {
            FullInventory fullInventory = new FullInventory(this);
            fullInvCommand.setExecutor(fullInventory);
        }

        // Teams
        ChatHandler chatHandler = new ChatHandler(this);
        getServer().getPluginManager().registerEvents(chatHandler, this);

        // Wands
        WandCommands wandCommands = new WandCommands(magicItems);
        this.getCommand("wand").setExecutor(wandCommands);

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
