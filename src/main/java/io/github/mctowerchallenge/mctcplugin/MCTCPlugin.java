package io.github.mctowerchallenge.mctcplugin;

import io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.quests.Jan2024QuestManager;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.IceCream;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.Posters;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.flags.SelectionHandler;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jun2023.gallery.Gallery;
import io.github.mctowerchallenge.mctcplugin.god.GodManager;
import io.github.mctowerchallenge.mctcplugin.hats.HatCommands;
import io.github.mctowerchallenge.mctcplugin.hats.HatTabComplete;
import io.github.mctowerchallenge.mctcplugin.hideentity.HiddenEntityManager;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTagManager;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTaggedEntity;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.CharacterManager;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.DialogueCommands;
import io.github.mctowerchallenge.mctcplugin.magic.MagicItems;
import io.github.mctowerchallenge.mctcplugin.magic.WandCommands;
import io.github.mctowerchallenge.mctcplugin.misc.*;
import io.github.mctowerchallenge.mctcplugin.misc.resourcepack.ResourcePack;
import io.github.mctowerchallenge.mctcplugin.misc.resourcepack.ResourcePackListener;
import io.github.mctowerchallenge.mctcplugin.quest.QuestCommands;
import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mctowerchallenge.mctcplugin.quest.util.FullInventory;
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
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

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
        Jan2024QuestManager jan2024QuestManager = new Jan2024QuestManager(this, teamManager);

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

        GameFlowManager gameFlowManager = new GameFlowManager(this, timer, characterManager, teamManager, portalControllers, jan2024QuestManager);

        InteractableTagManager tagManager = new InteractableTagManager(this, teamManager);

        new SelectionHandler(this, database);
        new IceCream(this);
        new Gallery(this);
        Posters.registerPosterInteractables();

        InteractableTaggedEntity seat = new InteractableTaggedEntity("seat");
        seat.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
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

        new GodManager(this, gameFlowManager, customBlockManager, questManager, jan2024QuestManager, teamManager, magicItems, portalControllers);

        WaitingRoom waitingRoom = new WaitingRoom(this);

        TowerCommands towerCommands = new TowerCommands(challengeManager, teamManager, portalControllers, waitingRoom, jan2024QuestManager, database);
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
